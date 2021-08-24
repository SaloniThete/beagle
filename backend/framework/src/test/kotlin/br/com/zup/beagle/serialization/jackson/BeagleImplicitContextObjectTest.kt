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
import br.com.zup.beagle.widget.context.Context
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


internal class BeagleImplicitContextObjectTest {

    private val implicitContextTest = ImplicitContextTest { listOf() }

    @Test
    fun findImplicitContexts_with_implicit_context_should_return_list() = run {
        val result = findImplicitContexts(implicitContextTest)
        Assertions.assertEquals(result.isNotEmpty(), true)
        Assertions.assertEquals(result[ImplicitContextTest::implicitContext.name], listOf<Any>())
    }

    @Test
    fun findImplicitContexts_without_implicit_context_should_return_empty_list() = run {
        val result = findImplicitContexts(ImplicitContextObjectTest(id = "id"))
        Assertions.assertEquals(result.isEmpty(), true)
    }
}

@RegisterWidget
internal class ImplicitContextTest(
    @ImplicitContext
    val implicitContext: ((ImplicitContextObjectTest) -> List<Any>)? = null)
    : Widget()

internal data class ImplicitContextObjectTest(
    val value: String? = null,
    override val id: String)
    : Context

