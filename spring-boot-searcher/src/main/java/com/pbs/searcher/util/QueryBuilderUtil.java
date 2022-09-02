/*
 *
 *  * Copyright (c) 2016 Generalitat de Catalunya.
 *  *
 *  * The contents of this file may be used under the terms of the EUPL, Version 1.1 or - as soon they will be approved by
 *  * the European Commission - subsequent versions of the EUPL (the "Licence");
 *  *
 *  * You may not use this work except in compliance with the Licence. You may obtain a copy of the Licence at:
 *  * http://www.osor.eu/eupl/european-union-public-licence-eupl-v.1.1
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on
 *  * an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *
 *  * See the Licence for the specific language governing permissions and limitations under the Licence.
 *  *
 *  * Original authors: Centre de Suport Canigó Contact: oficina-tecnica.canigo.ctti@gencat.cat
 *
 *
 */

package com.altran.galileu.util;


import com.altran.galileu.dto.enums.Operator;
import com.altran.galileu.dto.searcher.IdQuery;
import com.altran.galileu.dto.searcher.MatchQuery;
import com.altran.galileu.dto.searcher.NestedQuery;
import com.altran.galileu.dto.searcher.RangeQuery;
import com.altran.galileu.dto.searcher.SearcherQuery;
import com.altran.galileu.dto.searcher.TermQuery;
import java.util.Optional;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

public final class QueryBuilderUtil {

  private QueryBuilderUtil() {
    throw new UnsupportedOperationException();
  }

  public static BoolQueryBuilder getBooleanQueryBuilder(SearcherQuery searcherQuery) {
    BoolQueryBuilder boolQuery = new BoolQueryBuilder();
    Optional<IdQuery> idQuery = Optional.ofNullable(searcherQuery.getIdQuery());
    if (idQuery.isPresent() && idQuery.get().getIds() != null) {

      boolQuery.must(
          QueryBuilders.idsQuery().addIds(idQuery.get().getIds().stream().toArray(String[]::new)));

    } else {

      if (searcherQuery.getTermQueryList() != null && !searcherQuery.getTermQueryList().isEmpty()) {
        searcherQuery.getTermQueryList()
            .forEach(termQuery -> boolQuery.must(getTermQueryBuilder(termQuery)));
      }
      if (searcherQuery.getRangeQueryList() != null && !searcherQuery.getRangeQueryList()
          .isEmpty()) {
        searcherQuery.getRangeQueryList()
            .forEach(rangeQuery -> boolQuery.must(getRangeQueryBuilder(rangeQuery)));
      }
      if (searcherQuery.getNestedQuery() != null) {
        boolQuery.must(getNestedQueryBuilder(searcherQuery.getNestedQuery()));
      }

      if (searcherQuery.getMatchQueryList() != null && !searcherQuery.getMatchQueryList()
          .isEmpty()) {
        searcherQuery.getMatchQueryList()
            .forEach(matchQuery -> boolQuery.filter(getMatchQueryBuilder(matchQuery)));
      }
    }
    return boolQuery;
  }

  public static TermQueryBuilder getTermQueryBuilder(TermQuery termQuery) {
    TermQueryBuilder termQueryBuilder = null;
    if (null != termQuery) {
      termQueryBuilder = QueryBuilders.termQuery(termQuery.getField(), termQuery.getValue());
    }
    return termQueryBuilder;
  }

  public static MatchQueryBuilder getMatchQueryBuilder(MatchQuery matchQuery) {
    MatchQueryBuilder matchQueryBuilder = null;
    if (null != matchQuery) {
      matchQueryBuilder = QueryBuilders
          .matchQuery(matchQuery.getField(), matchQuery.getQuery());
      if (Operator.AND.equals(matchQuery.getOperator())) {
        matchQueryBuilder.operator(org.elasticsearch.index.query.Operator.AND);
      }

    }
    return matchQueryBuilder;
  }

  public static RangeQueryBuilder getRangeQueryBuilder(RangeQuery rangeQuery) {
    RangeQueryBuilder rangeQueryBuilder = null;
    if (null != rangeQuery) {
      rangeQueryBuilder = QueryBuilders.rangeQuery(rangeQuery.getField());
      rangeQueryBuilder.gte(rangeQuery.getGte());
      rangeQueryBuilder.lte(rangeQuery.getLte());
    }
    return rangeQueryBuilder;
  }

  public static NestedQueryBuilder getNestedQueryBuilder(NestedQuery nestedQuery) {
    NestedQueryBuilder nestedQueryBuilder = null;
    if (null != nestedQuery && null != nestedQuery.getMatchQuery()) {
      nestedQueryBuilder = QueryBuilders
          .nestedQuery(nestedQuery.getPath(), getMatchQueryBuilder(nestedQuery.getMatchQuery()),
              ScoreMode.None);
    }
    return nestedQueryBuilder;
  }
}
