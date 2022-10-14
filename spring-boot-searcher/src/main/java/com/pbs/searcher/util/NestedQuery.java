package com.pbs.searcher.util;

import lombok.Data;


@Data
public class NestedQuery {

  private String path;

  private MatchQuery matchQuery;

  private float boost;

  private Boolean caseInsensitive;

}
