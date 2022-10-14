

package com.pbs.searcher.rest.entity;

import java.io.Serializable;

@lombok.Data
public class ResponsePageScroll<E> implements Serializable {

  private ResponsePage<E> responsePage = new ResponsePage<>();

  private String scrollId;

}
