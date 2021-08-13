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
import br.com.zup.beagle.expression.SetContext_
import br.com.zup.beagle.widget.action.SetContext
import br.com.zup.beagle.widget.context.Bind
import br.com.zup.beagle.widget.context.ContextObject
import br.com.zup.beagle.widget.context.expressionOf
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Context
data class Person(
    override val contextId: String = "",
    val name: String,
    val age: Int,
    val orders: List<Order>,
    val address: Address
): ContextObject {
    constructor(contextId: String): this(contextId, "", 12, listOf(), Address(""))
}

@Context
data class Order(
    override val contextId: String = "",
    val products: List<String>,
    val value: Double
): ContextObject {
    constructor(contextId: String): this(contextId, listOf(), 0.0)
}

@Context
data class Address(
    override val contextId: String = "",
    val street: String,
    val zupCode: String,
    val contact: Contact
): ContextObject {
    constructor(contextId: String): this(contextId, "", "", Contact(""))
}


@Context
data class Contact(
    override val contextId: String = "",
    val email: String,
    val number: String
): ContextObject {
    constructor(contextId: String): this(contextId, "", "")
}


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
            ),
            orders = listOf(
                Order(
                    products = listOf(),
                    value = 10.000
                )
            )
        ).normalize(contextId)
    }

    @Test
    fun test_generated_normalize() {
        val expectedAddressContextId = "$contextId.${Person::address.name}"
        val expectedContactContextId = "$contextId.${Person::address.name}.${Address::contact.name}"
        val expectedOrderContextId = "$contextId.${Person::orders.name}[0]"

        assertEquals(contextId, person.contextId)
        assertEquals(expectedAddressContextId, person.address.contextId)
        assertEquals(expectedContactContextId, person.address.contact.contextId)
        assertEquals(expectedOrderContextId, person.orders[0].contextId)
    }

    @Test
    fun test_generated_root_expression() {
        val expectedPersonExpression: Bind<Int> = expressionOf("@{${person.contextId}}")
        val expectedAddressExpression: Bind<String> = expressionOf("@{${person.address.contextId}}")
        val expectedContactExpression: Bind<String> = expressionOf("@{${person.address.contact.contextId}}")
        val expectedOrdersExpression: Bind<List<String>> = expressionOf("@{$contextId.${Person::orders.name}}")
        val expectedOrderElementExpression: Bind<Order> = expressionOf("@{${person.orders[0].contextId}}")

        assertEquals(expectedPersonExpression, person.expression)
        assertEquals(expectedAddressExpression, person.address.expression)
        assertEquals(expectedAddressExpression, person.addressExpression)
        assertEquals(expectedContactExpression, person.address.contact.expression)
        assertEquals(expectedContactExpression, person.address.contactExpression)
        assertEquals(expectedOrdersExpression, person.ordersExpression)
        assertEquals(expectedOrderElementExpression, person.orders[0].expression)
    }

    @Test
    fun test_generated_expressions() {
        val expectedAgeExpression: Bind<Int> = expressionOf("@{${person.contextId}.${Person::age.name}}")
        val expectedStreetExpression: Bind<String> = expressionOf("@{${person.address.contextId}.${Address::street.name}}")
        val expectedEmailExpression: Bind<String> = expressionOf("@{${person.address.contact.contextId}.${Contact::email.name}}")
        val expectedProductsExpression: Bind<List<String>> = expressionOf("@{${person.orders[0].contextId}.${Order::products.name}}")

        assertEquals(expectedAgeExpression, person.ageExpression)
        assertEquals(expectedStreetExpression, person.address.streetExpression)
        assertEquals(expectedEmailExpression, person.address.contact.emailExpression)
        assertEquals(expectedProductsExpression, person.orders[0].productsExpression)
    }

    @Test
    fun test_generated_root_change_functions() {
        val newAddress = Address(contextId = contextId)
        val newPerson = Person(contextId = contextId)
        val newContact = Contact(contextId = contextId)
        val newOrder = Order(contextId = contextId)

        assertChange(newPerson, null, person.change(newPerson))
        assertChange(newAddress, Person::address.name, person.address.change(newAddress))
        assertChange(newContact, "${Person::address.name}.${Address::contact.name}", person.address.contact.change(newContact))
        assertChange(newOrder, Person::orders.name + "[0]", person.orders[0].change(newOrder))
    }

    @Test
    fun test_generated_root_change_functions_bind() {
        val newAddressBind = expressionOf<Address>("@{context.address}")
        val newPersonBind = expressionOf<Person>("@{context.person}")
        val newContactBind = expressionOf<Contact>("@{context.contact}")
        val newOrderBind = expressionOf<Order>("@{context.order}")

        assertChange(newPersonBind, null, person.change(newPersonBind))
        assertChange(newAddressBind, Person::address.name, person.address.change(newAddressBind))
        assertChange(newContactBind, "${Person::address.name}.${Address::contact.name}", person.address.contact.change(newContactBind))
        assertChange(newOrderBind, Person::orders.name + "[0]", person.orders[0].change(newOrderBind))
    }

    @Test
    fun test_generated_change_functions() {
        val newName = "pocas"
        val newStreet = "Av Joao Naves"
        val newEmail = "pocas@mail.com"
        val newValue = 15.000

        assertChange(newName, Person::name.name, person.changeName(newName))
        assertChange(newStreet, "${Person::address.name}.${Address::street.name}", person.address.changeStreet(newStreet))
        assertChange(newEmail, "${Person::address.name}.${Address::contact.name}.${Contact::email.name}", person.address.contact.changeEmail(newEmail))
        assertChange(newValue, "${Person::orders.name}[0].${Order::value.name}", person.orders[0].changeValue(newValue))
    }

    private fun assertChange(value: Any, path: String?, setContext: SetContext) {
        val expectedSetContact = SetContext(contextId = contextId, value = value, path = path)
        assertEquals(expectedSetContact, setContext)
    }
}