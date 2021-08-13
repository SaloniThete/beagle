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

import br.com.zup.beagle.annotation.Context
import br.com.zup.beagle.compiler.elementType
import br.com.zup.beagle.widget.action.SetContext
import br.com.zup.beagle.widget.context.Bind
import br.com.zup.beagle.widget.context.ContextObject
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import java.io.File
import java.lang.IndexOutOfBoundsException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class ContextObjectProcessor: AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Context::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(Context::class.java)
//        (it.enclosedElements.filter { it.kind == ElementKind.CONSTRUCTOR })[1]
        elements
            .forEach {
                if (!isAnnotationValid(it)) {
                    return true
                }

                processAnnotation(it)
            }
        return false
    }

    private fun isAnnotationValid(element: Element): Boolean {
        if (element.kind != ElementKind.CLASS) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated with @Context")
            return false
        }

        val typeElement = processingEnv.elementUtils.getTypeElement(element.asType().toString())
        val inheritFromContextObject = typeElement.interfaces.any { int ->
            int.asTypeName().toString() == ContextObject::class.java.name
        }

        if (!inheritFromContextObject) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes that inherit from ContextObject can be annotated with @Context", element)
            return false
        }

        return true
    }

    private fun processAnnotation(element: Element) {
        val className = element.simpleName.toString()
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()
        val fileName = "${className}Normalizer"
        val fileBuilder= FileSpec.builder(pack, fileName)
        val fields = element.enclosedElements.filter { it.kind == ElementKind.FIELD }
        val classTypeName = element.asType().asTypeName()

        fileBuilder.addImport("br.com.zup.beagle.widget.context", "Bind", "expressionOf", "splitContextId")

        fileBuilder.addFunction(buildNormalizerFun(classTypeName, fields))

        fileBuilder.addProperty(buildRootExpression(classTypeName))
        fileBuilder.addFunction(buildRootChangeFun(element, true))
        fileBuilder.addFunction(buildRootChangeFun(element, false))

        fields.forEach { enclosed ->
            if (enclosed.simpleName.toString() != "contextId") {
                val propertyName = enclosed.simpleName.toString()
                val propertyType = enclosed.asType().asTypeName().convertToKotlinStringIfNeeded()

                findListRegexMatch(enclosed.asType().toString())?.let { match ->
                    val typeElement = processingEnv.elementUtils.getTypeElement(match)
                    val isContextObject = typeElement?.getAnnotation(Context::class.java) != null
                    val isNullable = enclosed.getAnnotation(org.jetbrains.annotations.Nullable::class.java) != null

                    if (isContextObject) {
                        fileBuilder.addFunction(buildListAccessFun(
                            enclosed.simpleName.toString(),
                            typeElement.asType().asTypeName(),
                            classTypeName,
                            isNullable
                        ))
                    }
                }
                fileBuilder.addProperty(buildExpressionPropertyFor(enclosed, classTypeName))
                fileBuilder.addFunction(buildChangeFunFor(propertyName, propertyType, classTypeName))
                fileBuilder.addFunction(buildChangeFunFor(propertyName, propertyType.asBindType(), classTypeName))
            }
        }

        val file = fileBuilder.build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir))
    }

    private fun buildNormalizerFun(classTypeName: TypeName, classFields: List<Element>): FunSpec {
        val contextObjects = getContextObjectsFields(classFields)
        val statement = buildNormalizeFuncStatementWith(contextObjects)

        return FunSpec.builder("normalize")
                    .receiver(classTypeName)
                    .addParameter("contextId", String::class)
                    .addCode(statement)
                    .returns(classTypeName)
                    .build()
    }

    private fun buildNormalizeFuncStatementWith(contextObjectsFields: List<Element>): String {
        if (contextObjectsFields.isNotEmpty()) {
            val str = contextObjectsFields.fold("") { acc, contextObject ->
                val name = contextObject.simpleName.toString()
                val isNullableProperty = contextObject.getAnnotation(org.jetbrains.annotations.Nullable::class.java) != null
                val propertyName = if (isNullableProperty) "$name?" else name

                if (findListRegexMatch(contextObject.asType().toString()) != null) {
                    "$acc,\n    $name = $propertyName.mapIndexed { index, contextObject ->\n" +
                        "        contextObject.normalize(contextId = \"\${contextId}.$name[\$index]\")\n" +
                        "    }"
                } else {
                    "$acc,\n    $name = $propertyName.normalize(contextId = \"\${contextId}.$name\")"
                }
            }

            return "return this.copy(\n    contextId = contextId$str\n)"
        }
        return "return this.copy(contextId = contextId)"
    }

    private fun buildListAccessFun(parameterName: String, elementType: TypeName, classTypeName: TypeName, isNullable: Boolean): FunSpec {
        val tryCodeBlock = if (isNullable) "$parameterName?.get(index) ?: model" else "$parameterName[index]"

        return FunSpec.builder("${parameterName}GetElementAt")
            .receiver(classTypeName)
            .addParameter("index", Int::class)
            .returns(elementType)
            .addStatement("val model = ${elementType}(contextId = \"\$contextId.parameterName[\$index]\")")
            .addCode("return try { $tryCodeBlock } catch (e: IndexOutOfBoundsException) { model }")
            .build()
    }

    private fun getContextObjectsFields(parameters: List<Element>): List<Element> {
        fun isElementContextAnnotated(element: Element): Boolean {
            val elementTypeName = element.asType().toString()
            val match = findListRegexMatch(elementTypeName)

            val typeElement = processingEnv.elementUtils.getTypeElement(match ?: elementTypeName)
            return typeElement?.getAnnotation(Context::class.java) != null
        }

        return parameters.filter { isElementContextAnnotated(it) }
    }

    private fun findListRegexMatch(input: String): String? {
        val listRegularExpression = "(?<=java.util.List\\<).+?(?=\\>)".toRegex()
        val match = listRegularExpression.find(input)

        return match?.value
    }

    private fun buildRootExpression(classTypeName: TypeName): PropertySpec {
        return PropertySpec.builder("expression", classTypeName.asBindType(), KModifier.PUBLIC)
            .getter(
                FunSpec.getterBuilder()
                    .addStatement("return expressionOf<$classTypeName>(\"@{\$contextId}\")")
                    .build()
            )
            .receiver(classTypeName)
            .build()
    }


    private fun buildExpressionPropertyFor(element: Element, classTypeName: TypeName): PropertySpec {
        val propertyName = element.simpleName.toString()
        val elementType = element.asType().asTypeName().convertToKotlinStringIfNeeded()

        return PropertySpec.builder("${propertyName}Expression", elementType.asBindType(), KModifier.PUBLIC)
            .getter(
                FunSpec.getterBuilder()
                    .addStatement("return expressionOf(\"@{\$contextId.$propertyName}\")")
                    .build()
            )
            .receiver(classTypeName)
            .build()
    }

    private fun buildRootChangeFun(element: Element, isBind: Boolean): FunSpec {
        val type = element.asType().asTypeName()
        val parameterName = element.simpleName.toString().decapitalize()
        val parameterType = if (isBind) type.asBindType() else type

        return FunSpec.builder("change")
            .receiver(type)
            .addParameter(parameterName, parameterType)
            .addStatement("val contextIdSplit = splitContextId(contextId)")
            .addStatement("return SetContext(contextId = contextIdSplit.first, value = $parameterName, path = contextIdSplit.second)")
            .returns(SetContext::class)
            .build()
    }

    private fun buildChangeFunFor(parameterName: String, parameterType: TypeName, receiver: TypeName): FunSpec {
        return FunSpec.builder("change${parameterName.capitalize()}")
            .receiver(receiver)
            .addParameter(parameterName, parameterType)
            .addStatement("val contextIdSplit = splitContextId(contextId)")
            .addStatement("return SetContext(contextId = contextIdSplit.first, path = \"\${if (contextIdSplit.second != null) \"\${contextIdSplit.second}.\" else \"\"}$parameterName\", value = $parameterName)")
            .returns(SetContext::class)
            .build()
    }

    private fun TypeName.asBindType() = Bind.Expression::class.asTypeName().parameterizedBy(listOf(this.convertToKotlinStringIfNeeded()))

    private fun TypeName.convertToKotlinStringIfNeeded(): TypeName {
        if (this == java.lang.String::class.java.asTypeName()) {
            return String::class.asTypeName()
        }
        return this
    }
}