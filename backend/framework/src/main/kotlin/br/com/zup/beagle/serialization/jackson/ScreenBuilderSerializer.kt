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

import br.com.zup.beagle.annotation.ContextObject
import br.com.zup.beagle.annotation.GlobalObject
import br.com.zup.beagle.widget.context.Context
import br.com.zup.beagle.widget.layout.ScreenBuilder
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.lang.reflect.Field

class ScreenBuilderSerializer : JsonSerializer<ScreenBuilder>() {
    override fun serialize(value: ScreenBuilder, gen: JsonGenerator, serializers: SerializerProvider) {
        value::class.java.declaredFields.forEach {
            it.isAccessible = true
            if (it.type.isAnnotationPresent(ContextObject::class.java))
                normalize(it, (it.get(this) as Context).id)
            else if (it.type.isAnnotationPresent(GlobalObject::class.java))
                normalize(it)
        }
        gen.writeObject(value.build())
    }

    private fun normalize(field: Field, id: String? = null) {
        field.isAccessible = true
        val value = field.get(this)
        val normalizerClass = Class.forName("${field.type.typeName}NormalizerKt")
        val normalizeMethodName = "normalize"

        id?.let {
            val normalizeMethod = normalizerClass.getDeclaredMethod(
                normalizeMethodName,
                field.type,
                Class.forName("java.lang.String"))

            val normalizedContext = normalizeMethod.invoke(normalizerClass, value, it)
            field.set(this, normalizedContext)
        } ?: run {
            val normalizeMethod = normalizerClass.getDeclaredMethod(
                normalizeMethodName,
                field.type
            )

            val normalizedContext = normalizeMethod.invoke(normalizerClass, value)
            field.set(this, normalizedContext)
        }
    }
}