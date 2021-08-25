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

import br.com.zup.beagle.annotation.GlobalObject
import br.com.zup.beagle.widget.action.SetContext
import br.com.zup.beagle.widget.context.Context
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider

class SetContextObjectSerializer : BaseContextObjectSerializer<SetContext>() {

    override fun serialize(
        value: SetContext,
        gen: JsonGenerator,
        serializers: SerializerProvider
    ) {
        gen.writeStartObject()
        gen.writeStringField(ACTION_TYPE, "beagle:setContext")
        gen.writeStringField(SetContext::contextId.name, value.contextId)
        if (value.path != null) {
            gen.writeStringField(SetContext::path.name, value.path)
        }
        if (value.value::class.java.getAnnotation(GlobalObject::class.java) != null || value.value is Context) {
            gen.writeFieldName(SetContext::value.name)
            writeContext(value.value, gen)
        } else {
            gen.writeObjectField(SetContext::value.name, value.value)
        }
        gen.writeEndObject()
    }
}