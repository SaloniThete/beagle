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

import br.com.zup.beagle.annotation.ContextObject
import br.com.zup.beagle.annotation.GlobalObject
import br.com.zup.beagle.compiler.ContextObjectExtensionsFileBuilder
import br.com.zup.beagle.widget.context.Context
import com.google.auto.service.AutoService
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
class ContextObjectProcessor: AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ContextObject::class.java.name, GlobalObject::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(ContextObject::class.java)
//        (it.enclosedElements.filter { it.kind == ElementKind.CONSTRUCTOR })[1]
        elements
            .forEach {
                if (!isAnnotationValid(it)) {
                    return true
                }

                processAnnotation(it)
            }
        roundEnv.getElementsAnnotatedWith(GlobalObject::class.java).forEach { processAnnotation(it, true) }
        return false
    }

    private fun isAnnotationValid(element: Element): Boolean {
        if (element.kind != ElementKind.CLASS) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Only classes can be annotated with @ContextObject"
            )
            return false
        }

        val typeElement = processingEnv.elementUtils.getTypeElement(element.asType().toString())
        val inheritFromContextObject = typeElement.interfaces.any { int ->
            int.asTypeName().toString() == Context::class.java.name
        }

        if (!inheritFromContextObject) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Only classes that inherit from ContextObject can be annotated with @ContextObject",
                element
            )
            return false
        }

        return true
    }

    private fun processAnnotation(element: Element, isGlobal: Boolean = false) {
        val builder = ContextObjectExtensionsFileBuilder(element, processingEnv, isGlobal)

        val file = builder.build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir))
    }
}