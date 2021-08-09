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
import br.com.zup.beagle.widget.action.SetContext
import br.com.zup.beagle.widget.context.Bind
import br.com.zup.beagle.widget.context.ContextObject
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class AnnotationProcessor: AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Context::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(Context::class.java)

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
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes that inherit from ContextObject can be annotated with @Context")
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

        fileBuilder.addImport("br.com.zup.beagle.widget.context", "Bind", "expressionOf")

        fileBuilder.addFunction(buildNormalizerFun(classTypeName, fields))

        fileBuilder.addProperty(buildRootExpression(classTypeName))
        fileBuilder.addFunction(buildRootChangeFun(element, true))
        fileBuilder.addFunction(buildRootChangeFun(element, false))

        fields.forEach { enclosed ->
            if (enclosed.simpleName.toString() != "contextId") {
                val propertyName = enclosed.simpleName.toString()

                fileBuilder.addProperty(buildExpressionPropertyFor(enclosed, classTypeName))
                fileBuilder.addFunction(buildChangeFunFor(propertyName, enclosed.asType().asTypeName(), classTypeName))
                fileBuilder.addFunction(
                    buildChangeFunFor(
                        propertyName,
                        buildBindTypeFor(enclosed.asType().asTypeName()),
                        classTypeName
                    )
                )
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
                    .addStatement(statement)
                    .returns(classTypeName)
                    .build()
    }

    private fun buildNormalizeFuncStatementWith(contextObjectsFields: List<Element>): String {
        if (contextObjectsFields.isNotEmpty()) {
            val contextObjectsNames = contextObjectsFields.map { it.simpleName.toString() }
            val str = contextObjectsNames.fold("") { acc, name ->
                "$acc, $name = $name.normalize(contextId = \"\${contextId}.$name\")"
            }

            return "return this.copy(contextId = contextId$str)"
        }
        return "return this.copy(contextId = contextId)"
    }

    private fun getContextObjectsFields(parameters: List<Element>): List<Element> {
        fun isElementContextAnnotated(element: Element): Boolean {
            val typeElement = processingEnv.elementUtils.getTypeElement(element.asType().toString())
            return typeElement?.getAnnotation(Context::class.java) != null
        }

        return parameters.filter { isElementContextAnnotated(it) }
    }

    private fun buildRootExpression(classTypeName: TypeName): PropertySpec {
        return PropertySpec.builder("expression", buildBindTypeFor(classTypeName), KModifier.PUBLIC)
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
        val elementType = element.asType().asTypeName()

        return PropertySpec.builder("${propertyName}Expression", buildBindTypeFor(elementType), KModifier.PUBLIC)
            .getter(
                FunSpec.getterBuilder()
                    .addStatement("return expressionOf<$elementType>(\"@{\$contextId.$propertyName}\")")
                    .build()
            )
            .receiver(classTypeName)
            .build()
    }

    private fun buildRootChangeFun(element: Element, isBind: Boolean): FunSpec {
        val type = element.asType().asTypeName()
        val parameterName = element.simpleName.toString().decapitalize()
        val parameterType = if (isBind) buildBindTypeFor(type) else type

        return FunSpec.builder("change")
            .receiver(type)
            .addParameter(parameterName, parameterType)
            .addStatement("return SetContext(contextId = contextId, value = $parameterName)")
            .returns(SetContext::class)
            .build()
    }

    private fun buildChangeFunFor(parameterName: String, parameterType: TypeName, receiver: TypeName): FunSpec {
        return FunSpec.builder("change${parameterName.capitalize()}")
            .receiver(receiver)
            .addParameter(parameterName, parameterType)
            .addStatement("return SetContext(contextId = contextId, path = \"$parameterName\", value = $parameterName)")
            .returns(SetContext::class)
            .build()
    }

    private fun buildBindTypeFor(type: TypeName) = Bind::class.asTypeName().parameterizedBy(listOf(type))
}