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

import com.altran.galileu.rest.entity.ResponsePage
import spock.lang.Specification

class WsUtilSpec extends Specification {

    def 'test exception when creating instance - KO'() {
        when: 'Creating instance'
        new WsUtil()

        then: 'Expected results'
        thrown(UnsupportedOperationException)
    }

    def 'test getResponsePage'() {
        given: 'Initialize prerequisites'
        def included = []

        when: 'Invoking method'
        def result = WsUtil.getResponsePage(included, 0, 20, 10)

        then: 'Expected results'
        result in ResponsePage

    }

}
