package com.pbs.searcher.rest.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbs.searcher.config.Constants;
import com.pbs.searcher.rest.entity.Data;
import com.pbs.searcher.rest.entity.ResponsePage;
import com.pbs.searcher.rest.entity.ResponsePageScroll;
import com.pbs.searcher.util.QueryBuilderUtil;
import com.pbs.searcher.rest.entity.ObjectElastic;
import com.pbs.searcher.elasticQuery.SearcherQuery;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class ElasticClient {

  @Autowired private RestHighLevelClient client;

  @Autowired private ObjectMapper mapper;

  public Map listIndexes() throws IOException {
    Map indexCount = new HashMap();

    GetIndexRequest getIndexRequest =
        new GetIndexRequest("*").indicesOptions(IndicesOptions.lenientExpandOpen());

    String[] indexes = client.indices().get(getIndexRequest, RequestOptions.DEFAULT).getIndices();
    for (String index : indexes) {
      indexCount.put(index, count(index, null));
    }
    return indexCount;
  }

  public GetResponse findOne(String index, String objectElasticId, Integer version) {
    GetResponse getResponse = null;
    try {
      GetRequest request = new GetRequest(index, objectElasticId);
      if (null != version) {
        request.version(version);
      }
      getResponse = client.get(request, RequestOptions.DEFAULT);
    } catch (ElasticsearchException exception) {
      log.error(exception);

    } catch (IOException ioException) {
      log.error(ioException);
    }
    return getResponse;
  }

  public ResponsePage search(
      String index, Integer limit, Integer offset, String sort, SearcherQuery searcherQuery) {
    ResponsePage responsePage = new ResponsePage();
    try {
      SearchRequest searchRequest = getSearchRequest(index, limit, offset, sort, searcherQuery);

      SearchResponse seachResponse = client.search(searchRequest, RequestOptions.DEFAULT);
      getResponsePage(limit, offset, seachResponse, responsePage);
    } catch (ElasticsearchException exception) {
      log.error(exception);
    } catch (IOException ioException) {
      log.error(ioException);
    }
    return responsePage;
  }

  public ResponsePageScroll searchScroll(
      String index, Integer limit, String sort, String scrollId, SearcherQuery searcherQuery) {
    ResponsePageScroll responsePageScroll = new ResponsePageScroll();
    try {
      SearchResponse searchResponse;
      if (null != scrollId) {
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(TimeValue.timeValueSeconds(30));
        searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
        if (searchResponse != null && searchResponse.getHits() == null) {
          ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
          clearScrollRequest.addScrollId(searchResponse.getScrollId());
          client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        }
      } else {
        SearchRequest searchRequest = getSearchRequest(index, limit, null, sort, searcherQuery);
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));
        searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
      }
      getResponsePageScroll(limit, searchResponse, responsePageScroll);
    } catch (ElasticsearchException exception) {
      log.error(exception);
    } catch (IOException ioException) {
      log.error(ioException);
    }
    return responsePageScroll;
  }

  public boolean deleteScrollContext(String scrollId) {
    ClearScrollResponse clearScrollResponse = null;
    try {
      ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
      if (null != scrollId) {
        clearScrollRequest.addScrollId(scrollId);
      }
      clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
    } catch (IOException ioException) {
      log.error(ioException);
    }
    return clearScrollResponse != null && clearScrollResponse.isSucceeded();
  }

  public boolean putOne(String index, ObjectElastic objectElastic) {
    IndexResponse indexResponse = null;
    try {
      if (null != objectElastic) {
        IndexRequest indexRequest =
            getIndexRequestByObject(index, objectElastic.getId(), objectElastic.getContent());
        indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
      }
    } catch (ElasticsearchException exception) {
      log.error(exception);
    } catch (IOException ioException) {
      log.error(ioException);
    }
    return (indexResponse != null && (RestStatus.OK.equals(indexResponse.status()))
        || RestStatus.CREATED.equals(indexResponse.status()));
  }

  private IndexRequest getIndexRequestByObject(String index, String objectElasticId, Object object) {
    IndexRequest indexRequest = null;
    try {
      String json = mapper.writeValueAsString(object);
      indexRequest = new IndexRequest(index).id(objectElasticId).source(json, XContentType.JSON);
    } catch (JsonProcessingException e) {
      log.error(e);
    }
    return indexRequest;
  }

  public boolean putAll(String index, List<ObjectElastic> lObjectsElastic) {
    try {
      if (!existIndex(index)) {
        createIndex(index);
      }
      BulkRequest bulkRequest = new BulkRequest();
      lObjectsElastic.forEach(
              searcherBulkRequest ->
                      bulkRequest.add(
                              getIndexRequestByObject(
                                      index, searcherBulkRequest.getId(), searcherBulkRequest.getContent())));
      bulkRequest.timeout(TimeValue.timeValueMinutes(4));

      BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
      return !bulkResponse.hasFailures();
    } catch (ElasticsearchException exception) {
      log.error(exception);
    } catch (IOException ioException) {
      log.error(ioException);
    }
    return Boolean.TRUE;
  }

  public boolean deleteOne(String index, String objectElasticId) {
    try {
      DeleteRequest request = new DeleteRequest(index, objectElasticId);
      DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
      if (null != deleteResponse && RestStatus.NOT_FOUND != deleteResponse.status()) {
        return true;
      }
    } catch (ElasticsearchException exception) {
      log.error(exception);
    } catch (IOException ioException) {
      log.error(ioException);
    }
    return false;
  }

  public boolean deleteIndex(String index) {
    try {
      if (existIndex(index)) {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        AcknowledgedResponse deleteIndexResponse =
            client.indices().delete(request, RequestOptions.DEFAULT);
        if (null != deleteIndexResponse) {
          return deleteIndexResponse.isAcknowledged();
        }
      } else {
        return Boolean.TRUE;
      }
    } catch (ElasticsearchException exception) {
      log.error(exception);
    } catch (IOException ioException) {
      log.error(ioException);
    }
    return false;
  }

  private boolean existIndex(String index) {
    try {
      GetIndexRequest request = new GetIndexRequest(index);
      return client.indices().exists(request, RequestOptions.DEFAULT);
    } catch (ElasticsearchException exception) {
      log.error(exception);
    } catch (IOException ioException) {
      log.error(ioException);
    }
    return Boolean.FALSE;
  }

  public boolean createIndex(String index) {
    try {
      CreateIndexRequest request = new CreateIndexRequest(index);
      CreateIndexResponse createIndexResponse =
          client.indices().create(request, RequestOptions.DEFAULT);
      if (createIndexResponse != null) {
       return createIndexResponse.isAcknowledged();
      }
    } catch (ElasticsearchException exception) {
      log.error(exception);
    } catch (IOException ioException) {
      log.error(ioException);
    }
    return Boolean.FALSE;
  }

  private void getResponsePage(
      int limit, int offset, SearchResponse seachResponse, ResponsePage responsePage) {
    List<Object> included = new ArrayList();
    if (null != seachResponse) {
      seachResponse.getHits().forEach(hit -> included.add(hit.getSourceAsMap()));
      Data data = new Data<>();
      data.setIncluded(included);
      data.setLimit(limit);
      data.setOffset(offset);

      if (null != seachResponse.getHits()) {
        data.setTotal(seachResponse.getHits().getTotalHits().value);
        if (null != seachResponse.getHits().getHits()) {
          data.setNumberOfElements(seachResponse.getHits().getHits().length);
        }
      }
      responsePage.setData(data);
    }
  }

  public String[] getIndex(String index) throws IOException {
    try {
      GetIndexRequest request = new GetIndexRequest(index);
      GetIndexResponse getIndexResponse = client.indices().get(request, RequestOptions.DEFAULT);
      return getIndexResponse.getIndices();
    } catch (ElasticsearchException | IOException exception) {
      log.error(exception);
      throw exception;
    }
  }

  public Long count(String index, SearcherQuery searcherQuery) {
    try {
      CountRequest countRequest = new CountRequest(index);
      if (searcherQuery != null) {
        countRequest.query(QueryBuilderUtil.getBooleanQueryBuilder(searcherQuery));
      } else {
        countRequest.query(QueryBuilders.matchAllQuery());
      }
      CountResponse seachResponse = client.count(countRequest, RequestOptions.DEFAULT);
      return seachResponse.getCount();
    } catch (ElasticsearchException exception) {
      log.error(exception);
    } catch (IOException ioException) {
      log.error(ioException);
    }
    return 0L;
  }

  private SearchRequest getSearchRequest(
      String index, Integer limit, Integer offset, String sort, SearcherQuery searcherQuery) {
    SearchRequest searchRequest = new SearchRequest(index);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    if (null != searcherQuery) {
      searchSourceBuilder.query(QueryBuilderUtil.getBooleanQueryBuilder(searcherQuery));
    }
    if (null != limit) {
      searchSourceBuilder.size(limit);
    }
    if (null != offset) {
      searchSourceBuilder.from(offset == 1 ? 0 : (offset - 1) * limit);
    }
    if (null != sort && !sort.isEmpty()) {
      if (sort.contains(Constants.DASH)) {
        searchSourceBuilder.sort(sort.replace(Constants.DASH, Constants.EMPTY), SortOrder.DESC);
      } else {
        searchSourceBuilder.sort(sort, SortOrder.ASC);
      }
    }
    searchRequest.source(searchSourceBuilder);
    return searchRequest;
  }

  private void getResponsePageScroll(
      int limit, SearchResponse seachResponse, ResponsePageScroll responsePageScroll) {
    responsePageScroll.setScrollId(seachResponse.getScrollId());
    getResponsePage(limit, 0, seachResponse, responsePageScroll.getResponsePage());
  }
}
