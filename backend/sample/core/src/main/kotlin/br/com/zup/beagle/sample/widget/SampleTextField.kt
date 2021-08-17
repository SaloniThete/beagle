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

package br.com.zup.beagle.sample.widget

import br.com.zup.beagle.annotation.ImplicitContext
import br.com.zup.beagle.annotation.RegisterWidget
import br.com.zup.beagle.widget.action.Action
import br.com.zup.beagle.widget.context.Bind
import br.com.zup.beagle.widget.context.Context
import br.com.zup.beagle.widget.context.expressionOf
import br.com.zup.beagle.widget.form.InputWidget

@RegisterWidget
class SampleTextField(
    val placeholder: String,
    @ImplicitContext
    val onChange: ((SampleOnChange) -> List<Action>)? = null)
    : InputWidget()

data class SampleOnChange(
    val value: String? = null,
    override val id: String)
    : Context

//todo generate
val SampleOnChange.valueExpression: Bind.Expression<String>
    get() = expressionOf("@{${this.id}.value}")