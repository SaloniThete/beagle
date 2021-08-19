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
    private val processingEnvironment: ProcessingEnvironment,
    private val isGlobal: Boolean
) {
    private val className = element.simpleName.toString()
    private val pack = processingEnvironment.elementUtils.getPackageOf(element).toString()
    private val fileName = "${className}Normalizer"
    private val fileBuilder = FileSpec.builder(pack, fileName)
    private val classTypeName = element.asType().asTypeName()

    private val elementUtils = processingEnvironment.elementUtils
    private val typeUtils = processingEnvironment.typeUtils

    fun build(): FileSpec {
        val fields = element.enclosedElements.filter { it.kind == ElementKind.FIELD }

        fileBuilder.addImport("br.com.zup.beagle.widget.context", "Bind", "expressionOf", "splitContextId")

        fileBuilder.addFunction(buildNormalizerFun(fields))

        fileBuilder.addProperty(buildRootExpression())
        fileBuilder.addFunction(buildRootChangeFun(true))
        fileBuilder.addFunction(buildRootChangeFun(false))

        fields.forEach { enclosed ->
            if (enclosed.simpleName.toString() != "id") {
                addExtensionsTo(enclosed)
            }
        }

        return fileBuilder.build()
    }

    private fun addExtensionsTo(property: Element) {
        if (property.simpleName.toString() != "id") {
            val propertyName = property.simpleName.toString()
            val propertyType = property.asType().asTypeName().javaToKotlinType()

            findListRegexMatch(property.asType().toString())?.let {
                val typeElement = processingEnvironment.elementUtils.getTypeElement(it)
                val isContextObject = typeElement?.getAnnotation(ContextObject::class.java) != null
                val isNullable = property.getAnnotation(org.jetbrains.annotations.Nullable::class.java) != null
                val typeElementTypeName = typeElement.asType().asTypeName().javaToKotlinType()

                if (isContextObject) {
                    fileBuilder.addFunction(buildListAccessFun(
                        property.simpleName.toString(),
                        typeElementTypeName,
                        classTypeName,
                        isNullable
                    ))
                }

                fileBuilder.addFunction(buildChangeListElementFunFor(propertyName, typeElementTypeName, classTypeName))
                fileBuilder.addFunction(buildChangeListElementFunFor(propertyName, typeElementTypeName.asBindType(), classTypeName))
            }

            fileBuilder.addProperty(buildExpressionPropertyFor(property, propertyType, classTypeName))
            fileBuilder.addFunction(buildChangeFunFor(propertyName, propertyType, classTypeName))
            fileBuilder.addFunction(buildChangeFunFor(propertyName, propertyType.asBindType(), classTypeName))
        }
    }

    private fun buildNormalizerFun(classFields: List<Element>): FunSpec {
        val contextObjects = getContextObjectsFields(classFields)
        val normalizingCode = buildNormalizeFuncCodeWith(contextObjects)
        val builder = FunSpec.builder("normalize")
            .receiver(classTypeName)
            .addCode(normalizingCode)
            .returns(classTypeName)

        if (!isGlobal) {
            builder.addParameter("id", String::class)
        }

        return builder.build()
    }

    private fun buildNormalizeFuncCodeWith(contextObjectsFields: List<Element>): String {
        if (contextObjectsFields.isNotEmpty()) {
            val str = contextObjectsFields.fold("") { acc, contextObject ->
                val name = contextObject.simpleName.toString()
                val isNullableProperty = contextObject.getAnnotation(org.jetbrains.annotations.Nullable::class.java) != null
                val propertyName = if (isNullableProperty) "$name?" else name
                val contextIdStatement = if (isGlobal) "global" else "\${id}"

                if (findListRegexMatch(contextObject.asType().toString()) != null) {
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

    private fun buildListAccessFun(parameterName: String, elementType: TypeName, classTypeName: TypeName, isNullable: Boolean): FunSpec {
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
            val elementTypeName = element.asType().toString()
            val match = findListRegexMatch(elementTypeName)

            val typeElement = processingEnvironment.elementUtils.getTypeElement(match ?: elementTypeName)
            return typeElement?.getAnnotation(ContextObject::class.java) != null
        }

        return parameters.filter { isElementContextAnnotated(it) }
    }

    private fun findListRegexMatch(input: String): String? {
        val listRegularExpression = "(?<=java.util.List\\<).+?(?=\\>)".toRegex()
        val match = listRegularExpression.find(input)

        return match?.value
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


    private fun buildExpressionPropertyFor(element: Element, elementType: TypeName, classTypeName: TypeName): PropertySpec {
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

    private fun buildChangeFunFor(parameterName: String, parameterType: TypeName, receiver: TypeName): FunSpec {
        val builder = FunSpec.builder("change${parameterName.capitalize()}")
            .receiver(receiver)
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

    private fun buildChangeListElementFunFor(parameterName: String, parameterType: TypeName, receiver: TypeName): FunSpec {
        val builder = FunSpec.builder("change${parameterName.capitalize()}Element")
            .receiver(receiver)
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
}