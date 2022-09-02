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

package com.altran.galileu.rest.controller

import com.altran.galileu.dto.searcher.SearcherDocument
import com.altran.galileu.dto.searcher.SearcherQuery
import com.altran.galileu.rest.client.ElasticClient
import com.altran.galileu.rest.entity.ResponsePage
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import org.springframework.http.ResponseEntity
import spock.lang.Specification

class SearcherControllerSpec extends Specification {

    def searcherController = new SearcherController()
    def elasticClient = Mock(ElasticClient)

    def 'setup'() {
        searcherController.elasticClient = elasticClient
    }

    def 'test findOne'() {
        given: 'Initialize prerequisites'
        def response = Mock(GetResponse)
        elasticClient.findOne(_, _, _, _) >> response


        when: 'Invoking constructor'
        def result = searcherController.findOne("index", "documentID", 1)

        then: 'Expected results'
        result in ResponseEntity
    }

    def 'test search'() {
        given: 'Initialize prerequisites'
        def response = Mock(ResponsePage)
        def searcherQuery = Mock(SearcherQuery)
        elasticClient.search(_, _, _, _, _) >> response


        when: 'Invoking constructor'
        def result = searcherController.search("index", 20, 0, "sort", searcherQuery)

        then: 'Expected results'
        result in ResponsePage
    }

    def 'test putOne'() {
        given: 'Initialize prerequisites'
        def response = Mock(IndexResponse)
        def searcherDocument = Mock(SearcherDocument)
        elasticClient.putOne(_, _) >> response


        when: 'Invoking constructor'
        def result = searcherController.putOne("index", searcherDocument)

        then: 'Expected results'
        result in ResponseEntity
    }

    def 'test putAll'() {
        given: 'Initialize prerequisites'
        def response = Mock(BulkResponse)
        def searcherDocumentList = []
        elasticClient.putAll(_, _, _) >> response


        when: 'Invoking constructor'
        def result = searcherController.putAll("index", searcherDocumentList)

        then: 'Expected results'
        result in ResponseEntity
    }

    def 'test deleteOne'() {
        given: 'Initialize prerequisites'
        elasticClient.deleteOne(_, _, _) >> true


        when: 'Invoking constructor'
        def result = searcherController.deleteOne("index", "documentID")

        then: 'Expected results'
        result in ResponseEntity
    }

    def 'test reindex'() {
        given: 'Initialize prerequisites'

        when: 'Invoking constructor'
        def result = searcherController.deleteIndex("index")

        then: 'Expected results'
        result in ResponseEntity
    }
}
