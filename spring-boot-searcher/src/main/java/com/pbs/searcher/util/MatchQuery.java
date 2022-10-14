package com.pbs.searcher.util;

import lombok.Data;

@Data
public class MatchQuery {

  private String field;

  private String query;

  private Operator operator;

}
