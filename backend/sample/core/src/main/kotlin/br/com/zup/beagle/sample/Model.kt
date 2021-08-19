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

package br.com.zup.beagle.sample

import br.com.zup.beagle.annotation.Context
import br.com.zup.beagle.annotation.GlobalContext
import br.com.zup.beagle.widget.context.Bind
import br.com.zup.beagle.widget.context.ContextObject
import br.com.zup.beagle.widget.context.expressionOf

@Context
data class Model(
    override val contextId: String,
    val counter: List<Int>?,
    val post : String?,
    val child: Model2?,
    val child2: Model3?,
    val childList: List<Model3>?,
    val childList2: List<Model2>
): ContextObject {
    constructor(contextId: String): this(
        contextId = contextId,
        counter = null,
        post = null,
        child = null,
        child2 = null,
        childList = listOf(),
        childList2 = listOf()
    )
}

@Context
data class Model2(
    override val contextId: String,
    val title: String?,
    val child: Model3?
): ContextObject {
    constructor(contextId: String): this(contextId = contextId, title = null, child = null)
}

@Context
data class Model3(
    override val contextId: String,
    val names: List<String>?
): ContextObject {
    constructor(contextId: String): this(contextId = contextId, names = null)
}

@GlobalContext
data class Global(
    val name: String,
    val age: Int,
    val orders: List<String>,
    val child: Model3
)