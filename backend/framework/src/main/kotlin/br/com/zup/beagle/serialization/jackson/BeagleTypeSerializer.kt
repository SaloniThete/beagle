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
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase
import java.io.IOException
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaType

internal class BeagleTypeSerializer : BeanSerializerBase {

    private lateinit var classLoader: ClassLoader

    constructor(source: BeanSerializerBase) : super(source) {
        setup()
    }

    constructor(source: BeanSerializerBase, classLoader: ClassLoader) : super(source) {
        setup(classLoader)
    }

    constructor(
        source: BeagleTypeSerializer?,
        objectIdWriter: ObjectIdWriter?
    ) : super(source, objectIdWriter) {
        setup()
    }

    constructor(
        source: BeagleTypeSerializer?,
        toIgnore: MutableSet<String>?
    ) : super(source, toIgnore) {
        setup()
    }

    constructor(
        source: BeagleTypeSerializer?,
        objectIdWriter: ObjectIdWriter?,
        filterId: Any?
    ) : super(source, objectIdWriter, filterId) {
        setup()
    }

    constructor(
        source: BeanSerializerBase?,
        properties: Array<BeanPropertyWriter>,
        filteredProperties: Array<BeanPropertyWriter>
    ) : super(source, properties, filteredProperties) {
        setup()
    }

    override fun withObjectIdWriter(objectIdWriter: ObjectIdWriter?) = BeagleTypeSerializer(this, objectIdWriter)

    override fun withIgnorals(toIgnore: MutableSet<String>?) = BeagleTypeSerializer(this, toIgnore)

    override fun asArraySerializer() = BeagleTypeSerializer(this, this.classLoader)

    override fun withFilterId(filterId: Any?) = BeagleTypeSerializer(this, this.classLoader)

    override fun serialize(bean: Any, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeStartObject()
        getBeagleType(bean::class.java, this.classLoader)
            ?.also { (key, value) -> generator.writeStringField(key, value) }
        serializeFields(bean, generator, provider)
        generator.writeEndObject()
    }

    private fun setup(classLoader: ClassLoader = BeagleTypeSerializer::class.java.classLoader) {
        this.classLoader = classLoader
    }

    override fun serializeFields(bean: Any?, gen: JsonGenerator?, provider: SerializerProvider) {
        val implicitContexts = findImplicitContexts(bean)
        val props: Array<BeanPropertyWriter> = if (_filteredProps != null && provider.activeView != null) {
            _filteredProps
        } else {
            _props
        }
        var i = 0
        try {
            val len = props.size
            while (i < len) {
                val prop = props[i]
                if (prop != null && !implicitContexts.contains(prop.name)) { // can have nulls in filtered list
                    prop.serializeAsField(bean, gen, provider)
                } else {
                    val actions = implicitContexts[prop.name]
                    gen?.writeFieldName(prop.name)
                    gen?.writeObject(actions)
                }
                ++i
            }
            _anyGetterWriter?.getAndSerialize(bean, gen, provider)
        } catch (e: Exception) {
            val name = if (i == props.size) "[anySetter]" else props[i].name
            wrapAndThrow(provider, e, bean, name)
        } catch (e: StackOverflowError) {
            // 04-Sep-2009, tatu: Dealing with this is tricky, since we don't have many
            //   stack frames to spare... just one or two; can't make many calls.

            // 10-Dec-2015, tatu: and due to above, avoid "from" method, call ctor directly:
            //JsonMappingException mapE = JsonMappingException.from(gen, "Infinite recursion (StackOverflowError)", e);
            val mapE = JsonMappingException(gen, "Infinite recursion (StackOverflowError)", e)
            val name = if (i == props.size) "[anySetter]" else props[i].name
            mapE.prependPath(JsonMappingException.Reference(bean, name))
            throw mapE
        }
    }

    private fun findImplicitContexts(bean: Any?): HashMap<String, Any?> {
        val implicitContexts = HashMap<String, Any?>()
        bean?.let {
            for (property in bean::class.memberProperties) {
                property.isAccessible = true
                property.findAnnotation<ImplicitContext>()?.let {
                    implicitContexts.put(property.name, resolveMethod(bean, property))
                }
            }
        }

        return implicitContexts
    }

    private fun resolveMethod(bean: Any?, property: KProperty1<out Any, *>): Any? {
        val params = property.returnType.javaType as ParameterizedType
        val inputClass = (params.actualTypeArguments[0] as Class<*>)
        val fieldDefinition = property.javaField
        fieldDefinition?.isAccessible = true

        val fieldValue = fieldDefinition?.get(bean)
        val myMethod = fieldValue?.javaClass?.getDeclaredMethod("invoke", inputClass)
        myMethod?.isAccessible = true

        val id = generateId(property)
        val values = inputClass.kotlin.primaryConstructor
            ?.parameters
            ?.associate { it to (if (it.name == "contextId") id else null) }
            ?: error("parameters not found")

        val inputParam = inputClass.kotlin.primaryConstructor?.callBy(values)
        return myMethod?.invoke(fieldValue, inputParam)
    }

    private fun generateId(property: KProperty1<out Any, *>): String {
        return property.findAnnotation<ImplicitContext>()?.id?.let {
            if (it.isEmpty())
                property.name
            else
                it.replace(" ", "")
        } ?: run {
            property.name
        }
    }
}