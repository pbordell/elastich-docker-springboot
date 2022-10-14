package com.pbs.searcher.rest.entity;

import java.io.Serializable;

@lombok.Data
public class ResponsePage<E> implements Serializable {

  private static final long serialVersionUID = -7766018149936610764L;

  private Data<E> data = new Data();

  public ResponsePage() {
  }

  public ResponsePage(Data<E> data) {
    this.data = data;
  }

}
