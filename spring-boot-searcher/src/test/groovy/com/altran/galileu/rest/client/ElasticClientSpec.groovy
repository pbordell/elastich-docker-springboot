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

package com.altran.galileu.rest.client

import com.altran.galileu.rest.entity.ResponsePage
import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.client.RestHighLevelClient
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
class ElasticClientSpec extends Specification {

    def elasticClient = new ElasticClient()
    def mapper = Mock(ObjectMapper)

    def 'setup'() {
        RestHighLevelClient clientRest = GroovyMock() {
            internalPerformRequest(_, _, _, _, _, _, _) >> Object
        }
        elasticClient.client = clientRest
        elasticClient.mapper = mapper
    }

    def 'test findOne'() {
        given: 'Initialize prerequisites'
        def response = Mock(GetResponse)


        when: 'Invoking constructor'
        def result = elasticClient.findOne("index", "type", "documentId", null)

        then: 'Expected results'
        result in GetResponse
    }

    def 'test findAll'() {
        given: 'Initialize prerequisites'
        def response = Mock(ResponsePage)
        client.search(_, _) >> response


        when: 'Invoking constructor'
        def result = elasticClient.search("index", null)

        then: 'Expected results'
        result in ResponsePage
    }
}
