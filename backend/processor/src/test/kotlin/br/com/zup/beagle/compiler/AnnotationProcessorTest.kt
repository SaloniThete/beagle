/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.beagle.compiler

import br.com.zup.beagle.annotation.Context
import br.com.zup.beagle.widget.context.Bind
import br.com.zup.beagle.widget.context.ContextObject
import br.com.zup.beagle.widget.context.expressionOf
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Context
data class Person(
    override val contextId: String = "",
    val name: String, val age: Int,
    val address: Address
): ContextObject

@Context
data class Address(
    override val contextId: String = "",
    val street: String,
    val zupCode: String,
    val contact: Contact
): ContextObject

@Context
data class Contact(
    override val contextId: String = "",
    val email: String,
    val number: String
): ContextObject

internal class AnnotationProcessorTest {

    companion object {
        const val contextId = "contextId"

        private var person = Person(
            name = "yan",
            age = 23,
            address = Address(
                street = "Av Rondon",
                zupCode = "000-000",
                contact = Contact(
                    email = "yan@mail.com",
                    number = "9999-9999"
                )
            )
        ).normalize(contextId)
    }

    @Test
    fun test_generated_normalize() {
        val expectedAddressContextId = "$contextId.${Person::address.name}"
        val expectedContactContextId = "$contextId.${Person::address.name}.${Address::contact.name}"

        assertEquals(contextId, person.contextId)
        assertEquals(expectedAddressContextId, person.address.contextId)
        assertEquals(expectedContactContextId, person.address.contact.contextId)
    }

    @Test
    fun test_generated_root_expression() {
        val expectedPersonExpression: Bind<Int> = expressionOf("@{${person.contextId}}")
        val expectedAddressExpression: Bind<String> = expressionOf("@{${person.address.contextId}}")
        val expectedContactExpression: Bind<String> = expressionOf("@{${person.address.contact.contextId}}")

        assertEquals(expectedPersonExpression, person.expression)
        assertEquals(expectedAddressExpression, person.address.expression)
        assertEquals(expectedAddressExpression, person.addressExpression)
        assertEquals(expectedContactExpression, person.address.contact.expression)
        assertEquals(expectedContactExpression, person.address.contactExpression)
    }

    @Test
    fun test_generated_expressions() {
        val expectedAgeExpression: Bind<Int> = expressionOf("@{${person.contextId}.${Person::age.name}}")
        val expectedStreetExpression: Bind<String> = expressionOf("@{${person.address.contextId}.${Address::street.name}}")
        val expectedEmailExpression: Bind<String> = expressionOf("@{${person.address.contact.contextId}.${Contact::email.name}}")

        assertEquals(expectedAgeExpression, person.ageExpression)
        assertEquals(expectedStreetExpression, person.address.streetExpression)
        assertEquals(expectedEmailExpression, person.address.contact.emailExpression)
    }

    @Test
    fun test_generated_changes() {

    }
}