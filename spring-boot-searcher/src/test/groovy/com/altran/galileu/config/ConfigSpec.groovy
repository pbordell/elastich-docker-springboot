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

package com.altran.galileu.config

import org.elasticsearch.client.RestHighLevelClient
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class ConfigSpec extends Specification {

    def config = new Config()


    def 'test RestHighLevelClient'() {
        setup: "Set the properties"
        config.elasticsearchHost = "localhost"
        config.elasticsearchPort = 8002
        config.elasticsearchUser = "user"
        config.elasticsearchPassword = "password"

        when: 'Invoking constructor'
        def result = config.client()

        then: 'Expected results'
        result in RestHighLevelClient
    }

    def 'test ElasticsearchRestTemplate'() {
        setup: "Set the properties"
        config.elasticsearchHost = "localhost"
        config.elasticsearchPort = 8002

        when: 'Invoking constructor'
        def result = config.elasticsearchTemplate()

        then: 'Expected results'
        result in ElasticsearchRestTemplate
    }
}
