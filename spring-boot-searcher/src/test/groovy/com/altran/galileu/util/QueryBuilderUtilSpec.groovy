/*
 * Copyright (c) 2016 Generalitat de Catalunya.
 *
 * The contents of this file may be used under the terms of the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence. You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/european-union-public-licence-eupl-v.1.1
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on
 * an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 *
 * Original authors: Centre de Suport Canigó Contact: oficina-tecnica.canigo.ctti@gencat.cat
 */

package com.altran.galileu.util

import com.altran.galileu.dto.enums.Operator
import com.altran.galileu.dto.searcher.*
import org.elasticsearch.index.query.*
import spock.lang.Specification

class QueryBuilderUtilSpec extends Specification {

    def 'test exception when creating instance - KO'() {
        when: 'Creating instance'
        new QueryBuilderUtil()

        then: 'Expected results'
        thrown(UnsupportedOperationException)
    }

    def 'test getNestedQueryBuilder'() {
        given: 'Initialize prerequisites'
        def nestedQuery = Mock(NestedQuery)
        def matchQuery = Mock(MatchQuery)
        nestedQuery.getMatchQuery() >> matchQuery
        nestedQuery.getPath() >> "path"
        matchQuery.getField() >> "field"
        matchQuery.getOperator() >> Operator.AND
        matchQuery.getQuery() >> "query"


        when: 'Invoking method'
        def result = QueryBuilderUtil.getNestedQueryBuilder(nestedQuery)

        then: 'Expected results'
        result in NestedQueryBuilder

    }

    def 'test getRangeQueryBuilder'() {
        given: 'Initialize prerequisites'
        def rangeQuery = Mock(RangeQuery)
        rangeQuery.getField() >> "field"
        rangeQuery.getGte() >> new Date()
        rangeQuery.getLte() >> new Date()


        when: 'Invoking method'
        def result = QueryBuilderUtil.getRangeQueryBuilder(rangeQuery)

        then: 'Expected results'
        result in RangeQueryBuilder

    }

    def 'test getMatchQueryBuilder'() {
        given: 'Initialize prerequisites'
        def matchQuery = Mock(MatchQuery)
        matchQuery.getField() >> "field"
        matchQuery.getOperator() >> Operator.OR
        matchQuery.getQuery() >> "query"


        when: 'Invoking method'
        def result = QueryBuilderUtil.getMatchQueryBuilder(matchQuery)

        then: 'Expected results'
        result in MatchQueryBuilder

    }

    def 'test getTermQueryBuilder'() {
        given: 'Initialize prerequisites'
        def termQuery = Mock(TermQuery)
        termQuery.getField() >> "field"
        termQuery.getValue() >> "query"


        when: 'Invoking method'
        def result = QueryBuilderUtil.getTermQueryBuilder(termQuery)

        then: 'Expected results'
        result in TermQueryBuilder

    }

    def 'test getBooleanQueryBuilder idQuery'() {
        given: 'Initialize prerequisites'
        def searcherQuery = Mock(SearcherQuery)
        def idQuery = Mock(IdQuery)
        searcherQuery.getIdQuery() >> idQuery
        idQuery.getIds() >> []


        when: 'Invoking method'
        def result = QueryBuilderUtil.getBooleanQueryBuilder(searcherQuery)

        then: 'Expected results'
        result in BoolQueryBuilder

    }

    def 'test getBooleanQueryBuilder not idQuery'() {
        given: 'Initialize prerequisites'
        def searcherQuery = Mock(SearcherQuery)
        searcherQuery.getTermQueryList() >> []
        searcherQuery.getRangeQueryList() >> []
        searcherQuery.getMatchQueryList() >> []


        when: 'Invoking method'
        def result = QueryBuilderUtil.getBooleanQueryBuilder(searcherQuery)

        then: 'Expected results'
        result in BoolQueryBuilder

    }


}
