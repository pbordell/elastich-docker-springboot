package com.pbs.searcher.elasticQuery;

import com.pbs.searcher.util.Operator;
import lombok.Data;

@Data
public class MatchQuery {

  private String field;

  private String query;

  private Operator operator;

}
