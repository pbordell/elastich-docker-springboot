package com.pbs.searcher.util;

import lombok.Data;


@Data
public class TermQuery {

  private String field;

  private Object value;

  private float boost;

  private Boolean caseInsensitive;

}
