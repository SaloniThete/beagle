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
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Context
data class ContextObject(override val contextId: String, val title: String): br.com.zup.beagle.widget.context.ContextObject

internal class AnnotationProcessorTest {
    @Test
    fun testNormalize() {
        val newContextId = "newId"
        var context = ContextObject(contextId = "id", title = "title")
        context = context.normalize(newContextId)

        assertEquals(newContextId, context.contextId)
    }

}