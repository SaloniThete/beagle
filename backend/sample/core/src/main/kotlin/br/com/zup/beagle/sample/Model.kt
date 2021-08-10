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
import br.com.zup.beagle.widget.context.ContextObject

@Context
data class Model(
    override val contextId: String,
    val counter: Int,
    val post : String,
    val child: Model2,
    val child2: Model3
): ContextObject

@Context
data class Model2(
    override val contextId: String,
    val title: String,
    val child: Model3
): ContextObject

@Context
data class Model3(
    override val contextId: String,
    val name: String
): ContextObject