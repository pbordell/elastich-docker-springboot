package com.pbs.searcher.elasticQuery;

import lombok.Data;


@Data
public class NestedQuery {

  private String path;

  private MatchQuery matchQuery;

  private float boost;

  private Boolean caseInsensitive;

}
