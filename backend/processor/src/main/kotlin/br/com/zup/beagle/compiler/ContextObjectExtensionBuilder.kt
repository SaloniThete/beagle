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

package br.com.zup.beagle.compiler

import br.com.zup.beagle.annotation.ContextObject
import br.com.zup.beagle.widget.action.SetContext
import br.com.zup.beagle.widget.context.Bind
import br.com.zup.beagle.widget.context.Context
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

class ContextObjectExtensionsFileBuilder(
    private val element: Element,
    processingEnvironment: ProcessingEnvironment,
    private val isGlobal: Boolean
) {
    private val elementUtils = processingEnvironment.elementUtils
    private val typeUtils = processingEnvironment.typeUtils

    private val className = element.simpleName.toString()
    private val pack = elementUtils.getPackageOf(element).toString()
    private val fileName = "${className}Normalizer"
    private val fileBuilder = FileSpec.builder(pack, fileName)
    private val classTypeName = element.asType().asTypeName()

    fun build(): FileSpec {
        val fields = element.enclosedElements.filter {
            it.kind == ElementKind.FIELD && it.simpleName.toString() != Context::id.name
        }

        fileBuilder.addImport("br.com.zup.beagle.widget.context", "Bind", "expressionOf", "splitContextId")

        fileBuilder.addFunction(buildNormalizerFun(fields))

        fileBuilder.addProperty(buildRootExpression())
        fileBuilder.addFunction(buildRootChangeFun(true))
        fileBuilder.addFunction(buildRootChangeFun(false))

        fields.forEach { enclosed ->
            addExtensionsTo(enclosed)
        }

        return fileBuilder.build()
    }

    private fun addExtensionsTo(property: Element) {
        val propertyName = property.simpleName.toString()
        val propertyType = property.asType().asTypeName().javaToKotlinType()

        if (typeUtils.isIterable(property.asType())) {
            addListExtensionTo(property)
        }

        fileBuilder.addProperty(buildExpressionPropertyFor(property, propertyType))
        fileBuilder.addFunction(buildChangeFunFor(propertyName, propertyType))
        fileBuilder.addFunction(buildChangeFunFor(propertyName, propertyType.asBindType()))
    }

    private fun addListExtensionTo(property: Element) {
        val propertyName = property.simpleName.toString()
        val finalElementType = typeUtils.getFinalElementType(property.asType())
        val typeElement = elementUtils.getTypeElement(finalElementType.toString())
        val isContextObject = typeElement?.getAnnotation(ContextObject::class.java) != null
        val typeElementTypeName = typeElement.asType().asTypeName().javaToKotlinType()

        if (isContextObject) {
            fileBuilder.addFunction(buildListAccessFun(propertyName, typeElementTypeName, property.isNullable()))
        }

        fileBuilder.addFunction(buildChangeListElementFunFor(propertyName, typeElementTypeName))
        fileBuilder.addFunction(buildChangeListElementFunFor(propertyName, typeElementTypeName.asBindType()))
    }

    private fun buildNormalizerFun(classFields: List<Element>): FunSpec {
        val contextObjects = getContextObjectsFields(classFields)
        val normalizingCode = buildNormalizeFunCodeWith(contextObjects)
        val builder = FunSpec.builder("normalize")
            .receiver(classTypeName)
            .addCode(normalizingCode)
            .returns(classTypeName)

        if (!isGlobal) {
            builder.addParameter("id", String::class)
        }

        return builder.build()
    }

    private fun buildNormalizeFunCodeWith(contextObjectsFields: List<Element>): String {
        if (contextObjectsFields.isNotEmpty()) {
            val str = contextObjectsFields.fold("") { acc, contextObject ->
                val name = contextObject.simpleName.toString()
                val propertyName = if (contextObject.isNullable()) "$name?" else name
                val contextIdStatement = if (isGlobal) "global" else "\${id}"

                if (typeUtils.isIterable(contextObject.asType())) {
                    "$acc,\n    $name = $propertyName.mapIndexed { index, contextObject ->\n" +
                        "        contextObject.normalize(id = \"$contextIdStatement.$name[\$index]\")\n" +
                        "    }"
                } else {
                    "$acc,\n    $name = $propertyName.normalize(id = \"$contextIdStatement.$name\")"
                }
            }

            return if (isGlobal) {
                "return this.copy(    ${str.drop(1)}\n)"
            } else {
                "return this.copy(\n    id = id$str\n)"
            }
        }

        return if (isGlobal) {
            "return this"
        } else {
            "return this.copy(id = id)"
        }
    }

    private fun buildListAccessFun(parameterName: String, elementType: TypeName, isNullable: Boolean): FunSpec {
        val tryCodeBlock = if (isNullable) "$parameterName?.get(index) ?: model" else "$parameterName[index]"
        val contextIdStatement = if (isGlobal) "global" else "\$id"

        return FunSpec.builder("${parameterName}GetElementAt")
            .receiver(classTypeName)
            .addParameter("index", Int::class)
            .returns(elementType)
            .addStatement("val model = ${elementType}(\"$contextIdStatement.$parameterName[\$index]\")")
            .addCode("return try { $tryCodeBlock } catch (e: IndexOutOfBoundsException) { model }")
            .build()
    }

    private fun getContextObjectsFields(parameters: List<Element>): List<Element> {
        fun isElementContextAnnotated(element: Element): Boolean {
            return if (typeUtils.isLeaf(element.asType())) {
                false
            } else {
                val elementTypeName =
                    if(typeUtils.isIterable(element.asType()))
                        typeUtils.getFinalElementType(element.asType()).toString()
                    else
                        element.asType().toString()
                val typeElement = elementUtils.getTypeElement(elementTypeName)

                typeElement?.getAnnotation(ContextObject::class.java) != null
            }
        }

        return parameters.filter { isElementContextAnnotated(it) }
    }

    private fun buildRootExpression(): PropertySpec {
        val contextIdStatement = if (isGlobal) "global" else "\$id"
        return PropertySpec.builder("expression", classTypeName.asBindType(), KModifier.PUBLIC)
            .getter(
                FunSpec.getterBuilder()
                    .addStatement("return expressionOf<$classTypeName>(\"@{$contextIdStatement}\")")
                    .build()
            )
            .receiver(classTypeName)
            .build()
    }

    private fun buildExpressionPropertyFor(element: Element, elementType: TypeName): PropertySpec {
        val propertyName = element.simpleName.toString()
        val contextIdStatement = if (isGlobal) "global" else "\$id"

        return PropertySpec.builder("${propertyName}Expression", elementType.asBindType(), KModifier.PUBLIC)
            .getter(FunSpec.getterBuilder()
                .addStatement("return expressionOf(\"@{$contextIdStatement.$propertyName}\")")
                .build()
            )
            .receiver(classTypeName)
            .build()
    }

    private fun buildRootChangeFun(isBind: Boolean): FunSpec {
        val type = element.asType().asTypeName()
        val parameterName = element.simpleName.toString().decapitalize()
        val parameterType = if (isBind) type.asBindType() else type

        val builder = FunSpec.builder("change")
            .receiver(type)
            .addParameter(parameterName, parameterType)
            .returns(SetContext::class)

        if (isGlobal) {
            builder
                .addStatement("return SetContext(contextId = \"global\", value = $parameterName)")
        } else {
            builder
                .addStatement("val contextIdSplit = splitContextId(id)")
                .addStatement("return SetContext(contextId = contextIdSplit.first, value = $parameterName, path = contextIdSplit.second)")
        }

        return builder.build()
    }

    private fun buildChangeFunFor(parameterName: String, parameterType: TypeName): FunSpec {
        val builder = FunSpec.builder("change${parameterName.capitalize()}")
            .receiver(classTypeName)
            .addParameter(parameterName, parameterType)
            .returns(SetContext::class)

        if (isGlobal) {
            builder
                .addCode("return SetContext(\n" +
                    "   contextId = \"global\",\n" +
                    "   path = \"$parameterName\",\n" +
                    "   value = $parameterName\n" +
                    ")"
                )
        } else {
            builder
                .addStatement("val contextIdSplit = splitContextId(id)")
                .addCode("return SetContext(\n" +
                    "   contextId = contextIdSplit.first,\n" +
                    "   path = \"\${if (contextIdSplit.second != null) \"\${contextIdSplit.second}.\" else \"\"}$parameterName\",\n" +
                    "   value = $parameterName\n" +
                    ")"
                )
        }

        return builder.build()
    }

    private fun buildChangeListElementFunFor(parameterName: String, parameterType: TypeName): FunSpec {
        val builder = FunSpec.builder("change${parameterName.capitalize()}Element")
            .receiver(classTypeName)
            .addParameter(parameterName, parameterType)
            .addParameter("index", Int::class)

            .returns(SetContext::class)

        if (isGlobal) {
            builder
                .addCode("return SetContext(\n" +
                    "   contextId = \"global\",\n" +
                    "   path = \"$parameterName[\$index]\",\n" +
                    "   value = $parameterName\n" +
                    ")"
                )
        } else {
            builder
                .addStatement("val contextIdSplit = splitContextId(id)")
                .addCode("return SetContext(\n" +
                    "   contextId = contextIdSplit.first,\n" +
                    "   path = \"\${if (contextIdSplit.second != null) \"\${contextIdSplit.second}.\" else \"\"}$parameterName[\$index]\",\n" +
                    "   value = $parameterName\n" +
                    ")"
                )
        }

        return builder.build()
    }

    private fun TypeName.asBindType() = Bind.Expression::class.asTypeName().parameterizedBy(listOf(this))

    private fun TypeName.javaToKotlinType(): TypeName {
        return if (this is ParameterizedTypeName) {
            (rawType.javaToKotlinType() as ClassName).parameterizedBy(*typeArguments.map { it.javaToKotlinType() }.toTypedArray())
        } else {
            val className =
                JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(toString()))
                    ?.asSingleFqName()?.asString()

            return if (className == null) {
                this
            } else {
                ClassName.bestGuess(className)
            }
        }
    }

    private fun Element.isNullable(): Boolean = getAnnotation(org.jetbrains.annotations.Nullable::class.java) != null
}