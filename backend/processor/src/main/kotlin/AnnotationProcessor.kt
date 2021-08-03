import br.com.zup.beagle.annotation.Context
import br.com.zup.beagle.compiler.elementType
import br.com.zup.beagle.widget.context.Bind
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
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

@AutoService(Processor::class)
class AnnotationProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Context::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(Context::class.java)
            .forEach {
                if (it.kind != ElementKind.CLASS) {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated")
                    return true
                }
                processAnnotation(it)
            }
        return false
    }

    private fun processAnnotation(element: Element) {
        val className = element.simpleName.toString()
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()

        val fileName = "${className}Normalizer"
        val fileBuilder= FileSpec.builder(pack, fileName)
        fileBuilder.addImport("br.com.zup.beagle.widget.context","Bind","expressionOf")
        val classBuilder = TypeSpec.classBuilder(fileName)

        fileBuilder.addProperty(
            PropertySpec.builder("test", Bind::class.asTypeName().parameterizedBy(listOf(String::class.asTypeName())), KModifier.PUBLIC)
                .getter(
                    FunSpec.getterBuilder()
                        .addStatement("return expressionOf<String>(\"@{\$contextId}\")\n")
                        .build()
                )
                .receiver(element.asType().asTypeName())
                .build()
        )

        for (enclosed in element.enclosedElements) {
            if (enclosed.kind == ElementKind.FIELD) {
                val parameterTypeClassAnnotations = enclosed.kind.declaringClass.annotations
                if (parameterTypeClassAnnotations.isNotEmpty() && parameterTypeClassAnnotations.any { c -> c.toString() == "Context" }) {

                }
                val propertyName = enclosed.simpleName.toString()
                fileBuilder.addProperty(
                    PropertySpec.builder(propertyName, enclosed.asType().asTypeName(), KModifier.PUBLIC)
                        .initializer("null")
                        .build()
                )
                fileBuilder.addFunction(
                    FunSpec.builder("get${enclosed.simpleName}")
                        .returns(enclosed.asType().asTypeName())
                        .addStatement("return ${enclosed.simpleName}")
                        .build()
                )
                fileBuilder.addFunction(
                    FunSpec.builder("set${enclosed.simpleName}")
                        .addParameter(ParameterSpec.builder("${enclosed.simpleName}", enclosed.asType().asTypeName()).build())
                        .addStatement("this.${enclosed.simpleName} = ${enclosed.simpleName}")
                        .build()
                )
            }
        }
        val file = fileBuilder.build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir))
    }
}