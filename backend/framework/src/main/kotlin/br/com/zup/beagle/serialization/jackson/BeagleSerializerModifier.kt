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
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase
import java.util.stream.Collectors

internal class BeagleSerializerModifier(
    private val classLoader: ClassLoader
) : BeanSerializerModifier() {
    override fun modifySerializer(
        config: SerializationConfig,
        description: BeanDescription,
        serializer: JsonSerializer<*>
    ) =
        if (serializer is BeanSerializerBase && getBeagleType(description.beanClass, this.classLoader) != null) {
            BeagleTypeSerializer(serializer, this.classLoader)
        } else {
            serializer
        }

    override fun changeProperties(config: SerializationConfig?, beanDesc: BeanDescription?, beanProperties: MutableList<BeanPropertyWriter>?): MutableList<BeanPropertyWriter> {
        return beanProperties?.stream()
            ?.filter { property -> property.getAnnotation(ImplicitContext::class.java) == null }
            ?.collect(Collectors.toList()) ?: super.changeProperties(config, beanDesc, beanProperties)
    }
}
