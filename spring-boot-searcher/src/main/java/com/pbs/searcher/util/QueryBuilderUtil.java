package com.pbs.searcher.util;

import java.util.Optional;

import com.pbs.searcher.elasticQuery.*;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;

public final class QueryBuilderUtil {

  private QueryBuilderUtil() {
    throw new UnsupportedOperationException();
  }

  public static BoolQueryBuilder getBooleanQueryBuilder(SearcherQuery searcherQuery) {
    BoolQueryBuilder boolQuery = new BoolQueryBuilder();
    Optional<IdQuery> idQuery = Optional.ofNullable(searcherQuery.getIdQuery());
    if (idQuery.isPresent() && idQuery.get().getIds() != null) {

      boolQuery.must(
          QueryBuilders.idsQuery().addIds(idQuery.get().getIds().stream().toArray(String[]::new)));

    } else {

      if (searcherQuery.getTermQueryList() != null && !searcherQuery.getTermQueryList().isEmpty()) {
        searcherQuery.getTermQueryList()
            .forEach(termQuery -> boolQuery.must(getTermQueryBuilder(termQuery)));
      }
      if (searcherQuery.getRangeQueryList() != null && !searcherQuery.getRangeQueryList()
          .isEmpty()) {
        searcherQuery.getRangeQueryList()
            .forEach(rangeQuery -> boolQuery.must(getRangeQueryBuilder(rangeQuery)));
      }
      if (searcherQuery.getNestedQuery() != null) {
        boolQuery.must(getNestedQueryBuilder(searcherQuery.getNestedQuery()));
      }

      if (searcherQuery.getMatchQueryList() != null && !searcherQuery.getMatchQueryList()
          .isEmpty()) {
        searcherQuery.getMatchQueryList()
            .forEach(matchQuery -> boolQuery.filter(getMatchQueryBuilder(matchQuery)));
      }
    }
    return boolQuery;
  }

  public static TermQueryBuilder getTermQueryBuilder(TermQuery termQuery) {
    TermQueryBuilder termQueryBuilder = null;
    if (null != termQuery) {
      termQueryBuilder = QueryBuilders.termQuery(termQuery.getField(), termQuery.getValue());
    }
    return termQueryBuilder;
  }

  public static MatchQueryBuilder getMatchQueryBuilder(MatchQuery matchQuery) {
    MatchQueryBuilder matchQueryBuilder = null;
    if (null != matchQuery) {
      matchQueryBuilder = QueryBuilders
          .matchQuery(matchQuery.getField(), matchQuery.getQuery());
      if (Operator.AND.equals(matchQuery.getOperator())) {
        matchQueryBuilder.operator(org.elasticsearch.index.query.Operator.AND);
      }

    }
    return matchQueryBuilder;
  }

  public static RangeQueryBuilder getRangeQueryBuilder(RangeQuery rangeQuery) {
    RangeQueryBuilder rangeQueryBuilder = null;
    if (null != rangeQuery) {
      rangeQueryBuilder = QueryBuilders.rangeQuery(rangeQuery.getField());
      rangeQueryBuilder.gte(rangeQuery.getGte());
      rangeQueryBuilder.lte(rangeQuery.getLte());
    }
    return rangeQueryBuilder;
  }

  public static NestedQueryBuilder getNestedQueryBuilder(NestedQuery nestedQuery) {
    NestedQueryBuilder nestedQueryBuilder = null;
    if (null != nestedQuery && null != nestedQuery.getMatchQuery()) {
      nestedQueryBuilder = QueryBuilders
          .nestedQuery(nestedQuery.getPath(), getMatchQueryBuilder(nestedQuery.getMatchQuery()),
              ScoreMode.None);
    }
    return nestedQueryBuilder;
  }
}
