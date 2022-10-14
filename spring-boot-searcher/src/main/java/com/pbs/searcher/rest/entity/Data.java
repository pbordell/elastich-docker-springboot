package com.pbs.searcher.rest.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@lombok.Data
public class Data<E> implements Serializable {

  private static final long serialVersionUID = -7766018149936610764L;
  private long total;
  private long limit;
  private long numberOfElements;
  private long offset;
  private transient List<E> included = new ArrayList(0);

  public Data() {
  }

  public Data(long total, long limit, long numberOfElements, long offset, List<E> included) {
    this.total = total;
    this.limit = limit;
    this.numberOfElements = numberOfElements;
    this.offset = offset;
    this.included = included;
  }

}
