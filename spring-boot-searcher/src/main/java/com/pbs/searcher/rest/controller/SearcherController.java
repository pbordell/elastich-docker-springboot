package com.pbs.searcher.rest.controller;

import com.pbs.searcher.config.Constants;
import com.pbs.searcher.rest.client.ElasticClient;
import com.pbs.searcher.rest.entity.Data;
import com.pbs.searcher.rest.entity.ResponsePage;
import com.pbs.searcher.rest.entity.ResponsePageScroll;
import com.pbs.searcher.util.SearcherDocument;
import com.pbs.searcher.util.SearcherQuery;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.action.get.GetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/es")
public class SearcherController {

  @Autowired
  private ElasticClient elasticClient;

  @ApiOperation(value = "Read an object on specific index and ID")
  @GetMapping("/{index}/{documentId}/{version}")
  public ResponseEntity<ResponsePage> findOne(
      @PathVariable String index,
      @PathVariable String documentId,
      @PathVariable(required = false) Integer version) {
    GetResponse getResponse = elasticClient.findOne(index, documentId, version);
    if (null != getResponse) {
      List<Object> included = new ArrayList<>();
      included.add(getResponse.getSourceAsMap());
      return new ResponseEntity<>(
          getResponsePage(included, Constants.UN, Constants.UN, Constants.UN),
          HttpStatus.ACCEPTED);
    } else {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  private ResponsePage getResponsePage(List<Object> included, int limit, int total,
                                             int numberOfElements) {
    ResponsePage result = new ResponsePage();
    Data data = new Data();
    data.setIncluded(included);
    data.setLimit(limit);
    data.setTotal(total);
    data.setNumberOfElements(numberOfElements);
    return result;
  }

  @ApiOperation(value = "List all index created")
  @GetMapping("/listIndexes")
  public ResponseEntity listIndexes() throws IOException {
    return new ResponseEntity<>(elasticClient.listIndexes(), HttpStatus.ACCEPTED);
  }

  @ApiOperation(value = "Read multiple objects on specific index")
  @PostMapping("/{index}/{limit}/{offset}/{sort}")
  public ResponsePage search(
      @PathVariable String index,
      @PathVariable Integer limit,
      @PathVariable Integer offset,
      @PathVariable(required = false) String sort,
      @RequestBody(required = false) SearcherQuery searcherQuery) {
    return elasticClient.search(index, limit, offset, sort, searcherQuery);
  }

  @ApiOperation(value = "Read multiple objects on specific index using Scroll")
  @PostMapping("/scroll/{index}/{limit}/{sort}")
  public ResponsePageScroll searchScroll(
      @PathVariable String index,
      @PathVariable Integer limit,
      @PathVariable(required = false) String sort,
      @RequestParam(value = "scrollId", required = false) String scrollId,
      @RequestBody(required = false) SearcherQuery searcherQuery) {
    return elasticClient.searchScroll(index, limit, sort, scrollId, searcherQuery);
  }

  @ApiOperation(value = "Delete scroll context")
  @DeleteMapping("/scroll")
  public ResponseEntity deleteScrollContext(
      @RequestParam(value = "scrollId", required = false) String scrollId) {
    if (elasticClient.deleteScrollContext(scrollId)) {
      return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity(HttpStatus.CONFLICT);
  }

  @ApiOperation(value = "Get index information on specific index")
  @GetMapping(
      path = "/{index}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity getIndex(@PathVariable String index) throws IOException {
    return new ResponseEntity<>(elasticClient.getIndex(index), HttpStatus.ACCEPTED);
  }

  @ApiOperation(value = "Count objects on specific index")
  @PostMapping("/count/{index}")
  public ResponseEntity count(
      @PathVariable String index, @RequestBody(required = false) SearcherQuery searcherQuery) {
    return new ResponseEntity<>(elasticClient.count(index, searcherQuery), HttpStatus.ACCEPTED);
  }

  @ApiOperation(value = "Create an object on specific index.")
  @PutMapping("/{index}")
  public ResponseEntity putOne(
      @PathVariable String index, @RequestBody SearcherDocument searcherDocument) {
    if (elasticClient.putOne(index, searcherDocument)) {
      return new ResponseEntity(HttpStatus.CREATED);
    } else {
      return new ResponseEntity(HttpStatus.CONFLICT);
    }
  }

  @ApiOperation(value = "Create multiple objects on specific index.")
  @PutMapping("/bulk/{index}")
  public ResponseEntity putAll(
      @PathVariable String index, @RequestBody List<SearcherDocument> searcherDocumentList) {
    if (elasticClient.putAll(index, searcherDocumentList)) {
      return new ResponseEntity(HttpStatus.CREATED);
    } else {
      return new ResponseEntity(HttpStatus.CONFLICT);
    }
  }

  @ApiOperation(value = "Delete an object on specific index.")
  @DeleteMapping("/{index}/{documentId}")
  public ResponseEntity deleteOne(@PathVariable String index, @PathVariable String documentId) {
    if (elasticClient.deleteOne(index, documentId)) {
      return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity(HttpStatus.NOT_FOUND);
  }

  @ApiOperation(value = "Delete index.")
  @DeleteMapping("/{index}")
  public ResponseEntity deleteIndex(@PathVariable String index) {
    if (elasticClient.deleteIndex(index)) {
      return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity(HttpStatus.CONFLICT);
  }
}
