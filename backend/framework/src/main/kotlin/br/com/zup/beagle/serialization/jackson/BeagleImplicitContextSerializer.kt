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
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaType

internal fun findImplicitContexts(bean: Any?): HashMap<String, Any?> {
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

    val id = generateImplicitContextId(property)
    val values = inputClass.kotlin.primaryConstructor
        ?.parameters
        ?.associate { it to (if (it.name == "contextId") id else null) }
        ?: error("parameters not found")

    inputClass.kotlin.primaryConstructor?.isAccessible = true
    val inputParam = inputClass.kotlin.primaryConstructor?.callBy(values)
    return myMethod?.invoke(fieldValue, inputParam)
}

private fun generateImplicitContextId(property: KProperty1<out Any, *>): String {
    return property.findAnnotation<ImplicitContext>()?.id?.let {
        if (it.isEmpty())
            property.name
        else
            it.replace(" ", "")
    } ?: run {
        property.name
    }
}