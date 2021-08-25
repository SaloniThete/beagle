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

import br.com.zup.beagle.widget.context.Context
import br.com.zup.beagle.widget.context.ContextData
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import kotlin.reflect.full.memberProperties

abstract class BaseContextObjectSerializer<T> : JsonSerializer<T>() {

    protected fun writeContext(value: Any, gen: JsonGenerator) {
        gen.writeStartObject()
        value::class.memberProperties.filter {
            it.name != ContextData::id.name
        }.associate {
            it.name to it.getter.call(value)
        }.forEach { property ->
            val propertyValue = property.value
            if (propertyValue is Context) {
                gen.writeFieldName(property.key)
                writeContext(propertyValue, gen)
            } else if (propertyValue is List<*> && propertyValue.isNotEmpty() && propertyValue.first() is Context) {
                writeContextList(property.key, propertyValue, gen)
            } else {
                gen.writeObjectField(property.key, property.value)
            }
        }
        gen.writeEndObject()
    }

    private fun writeContextList(propertyName: String, value: List<Any?>, gen: JsonGenerator) {
        gen.writeFieldName(propertyName)
        gen.writeStartArray()
        value.forEach {
            writeContext(it as Context, gen)
        }
        gen.writeEndArray()
    }
}