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

package com.altran.galileu.rest.entity

import com.altran.galileu.FilterTargetClasses
import com.openpojo.reflection.filters.FilterChain
import com.openpojo.reflection.filters.FilterNonConcrete
import com.openpojo.reflection.impl.PojoClassFactory
import com.openpojo.validation.ValidatorBuilder
import com.openpojo.validation.rule.impl.EqualsAndHashCodeMatchRule
import com.openpojo.validation.rule.impl.GetterMustExistRule
import com.openpojo.validation.rule.impl.SerializableMustHaveSerialVersionUIDRule
import com.openpojo.validation.rule.impl.SetterMustExistRule
import com.openpojo.validation.test.impl.GetterTester
import com.openpojo.validation.test.impl.SetterTester
import spock.lang.Specification

class EntitySpec extends Specification {

    def EXPECTED_CLASS_COUNT = 2
    def POJO_PACKAGE = "com.altran.galileu.rest.entity"
    def filterChain = new FilterChain(new FilterNonConcrete(), new FilterTargetClasses())

    def 'test count pojos'() {
        given: 'Instantiate target'
        def pojoClasses = PojoClassFactory.getPojoClasses(POJO_PACKAGE, filterChain)

        when: 'Count pojos'
        def result = pojoClasses.size()

        then: 'Results as expected'
        result == EXPECTED_CLASS_COUNT
    }

    def 'test pojos'() {
        given: 'Instantiate target'
        def validator =
                ValidatorBuilder.create()
                        .with(new GetterMustExistRule())
                        .with(new SetterMustExistRule())
                        .with(new EqualsAndHashCodeMatchRule())
                        .with(new SerializableMustHaveSerialVersionUIDRule())
                        .with(new SetterTester())
                        .with(new GetterTester())
                        .build();

        when: 'test pojos'
        validator.validate(POJO_PACKAGE, filterChain)

        then: 'Results as expected'
        noExceptionThrown()
    }

}
