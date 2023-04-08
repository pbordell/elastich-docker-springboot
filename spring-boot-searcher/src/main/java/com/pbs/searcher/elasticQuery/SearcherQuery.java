package com.pbs.searcher.elasticQuery;

import java.util.List;
import lombok.Data;


@Data
public class SearcherQuery {

  private IdQuery idQuery;

  private List<TermQuery> termQueryList;

  private List<MatchQuery> matchQueryList;

  private List<RangeQuery> rangeQueryList;

  private NestedQuery nestedQuery;

}
