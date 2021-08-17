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
import br.com.zup.beagle.widget.action.SetContext
import br.com.zup.beagle.widget.context.Bind
import br.com.zup.beagle.widget.context.ContextObject
import br.com.zup.beagle.widget.context.expressionOf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Context
data class Person(
    override val contextId: String = "",
    val name: String,
    val age: Int,
    val orders: List<Order>,
    val address: Address
) : ContextObject {
    constructor(contextId: String) : this(contextId, "", 12, listOf(), Address(""))
}

@Context
data class Order(
    override val contextId: String = "",
    val products: List<String>,
    val value: Double
) : ContextObject {
    constructor(contextId: String) : this(contextId, listOf(), 0.0)
}

@Context
data class Address(
    override val contextId: String = "",
    val street: String,
    val zupCode: String,
    val contact: Contact
) : ContextObject {
    constructor(contextId: String) : this(contextId, "", "", Contact(""))
}


@Context
data class Contact(
    override val contextId: String = "",
    val email: String,
    val number: String
) : ContextObject {
    constructor(contextId: String) : this(contextId, "", "")
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

        Assertions.assertEquals(contextId, person.contextId)
        Assertions.assertEquals(expectedAddressContextId, person.address.contextId)
        Assertions.assertEquals(expectedContactContextId, person.address.contact.contextId)
        Assertions.assertEquals(expectedOrderContextId, person.orders[0].contextId)
    }

    @Test
    fun test_generated_root_expression() {
        val expectedPersonExpression: Bind<Int> = expressionOf("@{${person.contextId}}")
        val expectedAddressExpression: Bind<String> = expressionOf("@{${person.address.contextId}}")
        val expectedContactExpression: Bind<String> = expressionOf("@{${person.address.contact.contextId}}")
        val expectedOrdersExpression: Bind<List<String>> = expressionOf("@{$contextId.${Person::orders.name}}")
        val expectedOrderElementExpression: Bind<Order> = expressionOf("@{${person.orders[0].contextId}}")

        Assertions.assertEquals(expectedPersonExpression, person.expression)
        Assertions.assertEquals(expectedAddressExpression, person.address.expression)
        Assertions.assertEquals(expectedAddressExpression, person.addressExpression)
        Assertions.assertEquals(expectedContactExpression, person.address.contact.expression)
        Assertions.assertEquals(expectedContactExpression, person.address.contactExpression)
        Assertions.assertEquals(expectedOrdersExpression, person.ordersExpression)
        Assertions.assertEquals(expectedOrderElementExpression, person.orders[0].expression)
    }

    @Test
    fun test_generated_expressions() {
        val expectedAgeExpression: Bind<Int> = expressionOf("@{${person.contextId}.${Person::age.name}}")
        val expectedStreetExpression: Bind<String> = expressionOf("@{${person.address.contextId}.${Address::street.name}}")
        val expectedEmailExpression: Bind<String> = expressionOf("@{${person.address.contact.contextId}.${Contact::email.name}}")
        val expectedProductsExpression: Bind<List<String>> = expressionOf("@{${person.orders[0].contextId}.${Order::products.name}}")

        Assertions.assertEquals(expectedAgeExpression, person.ageExpression)
        Assertions.assertEquals(expectedStreetExpression, person.address.streetExpression)
        Assertions.assertEquals(expectedEmailExpression, person.address.contact.emailExpression)
        Assertions.assertEquals(expectedProductsExpression, person.orders[0].productsExpression)
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

    @Test
    fun test_generated_change_functions_bind() {
        val newName = expressionOf<String>("context.name")
        val newStreet = expressionOf<String>("context.street")
        val newEmail = expressionOf<String>("context.email")
        val newValue = expressionOf<Double>("context.value")

        assertChange(newName, Person::name.name, person.changeName(newName))
        assertChange(newStreet, "${Person::address.name}.${Address::street.name}", person.address.changeStreet(newStreet))
        assertChange(newEmail, "${Person::address.name}.${Address::contact.name}.${Contact::email.name}", person.address.contact.changeEmail(newEmail))
        assertChange(newValue, "${Person::orders.name}[0].${Order::value.name}", person.orders[0].changeValue(newValue))
    }

    @Test
    fun test_generated_get_element_functions() {
        val index = 40
        val expectedOrderContextId = "contextId.orders[$index]"

        Assertions.assertEquals(expectedOrderContextId, person.ordersGetElementAt(index).contextId)
        Assertions.assertEquals(person.orders[0], person.ordersGetElementAt(0), "When element exist it must return it")
    }

    @Test
    fun test_generated_change_element_functions() {
        val index = 30
        val newProduct = "newProduct"
        val newOrder = Order(contextId = "contextId", products = listOf("newProducts"), value = 30.00)

        assertChange(newProduct, "orders[0].products[$index]", person.orders[0].changeProductsElement(newProduct, index))
        assertChange(newOrder, "orders[$index]", person.changeOrdersElement(newOrder, index))
    }

    @Test
    fun test_generated_change_element_functions_bind() {
        val index = 30
        val newProduct = expressionOf<String>("context.product")
        val newOrder = expressionOf<Order>("context.order")

        assertChange(newProduct, "orders[0].products[$index]", person.orders[0].changeProductsElement(newProduct, index))
        assertChange(newOrder, "orders[$index]", person.changeOrdersElement(newOrder, index))
    }

    private fun assertChange(value: Any, path: String?, setContext: SetContext) {
        val expectedSetContact = SetContext(contextId = contextId, value = value, path = path)
        Assertions.assertEquals(expectedSetContact, setContext)
    }
}