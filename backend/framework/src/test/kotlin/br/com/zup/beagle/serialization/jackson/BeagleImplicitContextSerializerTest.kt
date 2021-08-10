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

package br.com.zup.beagle.serialization.jackson

import br.com.zup.beagle.annotation.ImplicitContext
import br.com.zup.beagle.annotation.RegisterWidget
import br.com.zup.beagle.widget.Widget
import br.com.zup.beagle.widget.action.Action
import br.com.zup.beagle.widget.context.ContextObject
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class BeagleImplicitContextSerializerTest {

    private val implicitContextTest = ImplicitContextTest { listOf() }

    @DisplayName("When call serialization with implicit context")
    @Test
    fun serialize_beagle_implicit_context_should_call_serializeImplicitContexts() = testSerialize(implicitContextTest) {
        verify(exactly = 1) { serializeImplicitContexts(findImplicitContexts(implicitContextTest), it) }
    }

    @Test
    fun findImplicitContexts_with_implicit_context_should_return_list() = run {
        val result = findImplicitContexts(implicitContextTest)
        Assertions.assertEquals(result.isNotEmpty(), true)
    }

    @Test
    fun findImplicitContexts_without_implicit_context_should_return_empty_list() = run {
        val result = findImplicitContexts(ContextObjectTest(contextId = "id"))
        Assertions.assertEquals(result.isEmpty(), true)
    }

    private fun testSerialize(bean: Any, verify: (JsonGenerator) -> Unit) {
        val generator = mockk<JsonGenerator>(relaxUnitFun = true)
        val provider = mockk<SerializerProvider>()

        every { provider.activeView } returns Any::class.java

        BeagleTypeSerializer(mockk(relaxed = true), arrayOf(), arrayOf()).serialize(bean, generator, provider)

        verify(generator)
    }

    @RegisterWidget
    private class ImplicitContextTest(
        @ImplicitContext
        val implicitContext: ((ContextObjectTest) -> List<Action>)? = null)
        : Widget()

    private data class ContextObjectTest(
        val value: String? = null,
        override val contextId: String)
        : ContextObject
}

