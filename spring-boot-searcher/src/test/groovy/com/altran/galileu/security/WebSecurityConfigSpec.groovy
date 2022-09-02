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

package com.altran.galileu.security


import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer
import spock.lang.Specification

class WebSecurityConfigSpec extends Specification {

    def webConfig = new WebSecurityConfig()

    def 'test configure'() {
        given: 'Initialize prerequisites'
        def httpSecurity = GroovyMock(HttpSecurity)
        def configurer = GroovyMock(ExpressionUrlAuthorizationConfigurer)
        httpSecurity.authorizeRequests() >> configurer


        when: 'Invoking constructor'
        webConfig.configure(GroovyMock(HttpSecurity))

        then: 'Expected results'
        thrown(NullPointerException)
    }
}