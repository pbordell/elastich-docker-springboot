package com.pbs.searcher.rest.controller;

import com.pbs.searcher.config.Constants;
import com.pbs.searcher.rest.client.ElasticClient;
import com.pbs.searcher.rest.entity.Data;
import com.pbs.searcher.rest.entity.ResponsePage;
import com.pbs.searcher.rest.entity.ResponsePageScroll;
import com.pbs.searcher.rest.entity.ObjectElastic;
import com.pbs.searcher.elasticQuery.SearcherQuery;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

@RestController
@RequestMapping("/api/es")
public class SearcherController {

  @Autowired
  private ElasticClient elasticClient;

  @ApiOperation(value = "Read an object on specific index and ID")
  @GetMapping("/{index}/{objectElasticId}/{version}")
  public ResponseEntity<ResponsePage> findEleasticObject(
      @PathVariable String index,
      @PathVariable String objectElasticId,
      @PathVariable(required = false) Integer version) {
    GetResponse getResponse = elasticClient.findOne(index, objectElasticId, version);
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

  private ResponsePage getResponsePage(List<Object> included, int limit, int total, int numberOfElements) {
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
  public ResponsePage searchObjects(
      @PathVariable String index,
      @PathVariable Integer limit,
      @PathVariable Integer offset,
      @PathVariable(required = false) String sort,
      @RequestBody(required = false) SearcherQuery searcherQuery) {
    return elasticClient.search(index, limit, offset, sort, searcherQuery);
  }

  @ApiOperation(value = "Read multiple objects on specific index using Scroll")
  @PostMapping("/scroll/{index}/{limit}/{sort}")
  public ResponsePageScroll searchObjectsScroll(
      @PathVariable String index,
      @PathVariable Integer limit,
      @PathVariable(required = false) String sort,
      @RequestParam(required = false) String scrollId,
      @RequestBody(required = false) SearcherQuery searcherQuery) {
    return elasticClient.searchScroll(index, limit, sort, scrollId, searcherQuery);
  }

  @ApiOperation(value = "Delete scroll context")
  @DeleteMapping("/scroll")
  public ResponseEntity deleteScrollContext(@RequestParam(required = false) String scrollId) {
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
  public ResponseEntity putObjectElastic(
      @PathVariable String index, @RequestBody ObjectElastic objectElastic) {
    if (elasticClient.putOne(index, objectElastic)) {
      return new ResponseEntity(HttpStatus.CREATED);
    } else {
      return new ResponseEntity(HttpStatus.CONFLICT);
    }
  }

  @ApiOperation(value = "Create multiple objects on specific index.")
  @PutMapping("/bulk/{index}")
  public ResponseEntity putListObjectsElastic(
      @PathVariable String index, @RequestBody List<ObjectElastic> objectsElastic) {
    if (elasticClient.putAll(index, objectsElastic)) {
      return new ResponseEntity(HttpStatus.CREATED);
    } else {
      return new ResponseEntity(HttpStatus.CONFLICT);
    }
  }

  @ApiOperation(value = "Delete an object on specific index.")
  @DeleteMapping("/{nameIndex}/{objectId}")
  public ResponseEntity deleteObjectElastic(@PathVariable String nameIndex, @PathVariable String objectId) {
    if (elasticClient.deleteOne(nameIndex, objectId)) {
      return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity(HttpStatus.NOT_FOUND);
  }

  @ApiOperation(value = "Delete index.")
  @DeleteMapping("/{nameIndex}")
  public ResponseEntity deleteIndex(@PathVariable String nameIndex) {
    if (elasticClient.deleteIndex(nameIndex)) {
      return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity(HttpStatus.CONFLICT);
  }
}
