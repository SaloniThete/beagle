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

package br.com.zup.beagle.sample.widgets

import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.components.form.InputWidget
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.utils.handleEvent
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.annotation.RegisterWidget

private const val VALUE_KEY = "value"

@RegisterWidget("sampleTextField")
data class SampleTextField(
    val placeholder: String = "",
    val onChange: List<Action>? = null
) : InputWidget() {

    @Transient
    private lateinit var textFieldView: EditText

    override fun getValue() = textFieldView.text.toString()

    override fun onErrorMessage(message: String) {
        textFieldView.error = message
    }

    override fun buildView(rootView: RootView) = EditText(rootView.getContext()).apply {
        textFieldView = this

        textFieldView.isSingleLine = true

        doOnTextChanged { newText, _, _, _ ->
            notifyChanges()
            onChange?.let {
                this@SampleTextField.handleEvent(
                    rootView,
                    this,
                    onChange,
                    ContextData(
                        id = "onChange",
                        value = mapOf(VALUE_KEY to newText.toString())
                    ),
                    analyticsValue = "onChange"
                )
            }
        }
    }
}
