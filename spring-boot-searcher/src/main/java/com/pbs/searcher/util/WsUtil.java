package com.pbs.searcher.util;

import com.pbs.searcher.rest.entity.Data;
import com.pbs.searcher.rest.entity.ResponsePage;

import java.util.List;

public final class WsUtil {

  private WsUtil() {
    throw new UnsupportedOperationException();
  }

  public static ResponsePage getResponsePage(List<Object> included, int limit, int total,
                                             int numberOfElements) {
    ResponsePage result = new ResponsePage();
    Data data = new Data();
    data.setIncluded(included);
    data.setLimit(limit);
    data.setTotal(total);
    data.setNumberOfElements(numberOfElements);
    return result;
  }
}
