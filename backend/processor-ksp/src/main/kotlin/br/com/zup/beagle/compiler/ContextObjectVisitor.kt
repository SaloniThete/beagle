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

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import java.io.OutputStream

class ContextObjectVisitor(private val codeGenerator: CodeGenerator) : KSVisitorVoid() {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        classDeclaration.primaryConstructor!!.accept(this, data)
    }

    //TODO: refac this function to generate code with kotlin poet
    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        val parent = function.parentDeclaration as KSClassDeclaration
        val packageName = parent.containingFile!!.packageName.asString()
        val className = parent.simpleName.asString()
        val fileName = "${className}Normalizer"
        val file = codeGenerator.createNewFile(Dependencies(true, function.containingFile!!), packageName , fileName)
        val contextObjects = getContextObjects(function.parameters)

        file.appendText("package $packageName\n\n")
        file.appendText("fun $className.normalize(contextId: String): $className {\n")

        if (contextObjects.isNotEmpty()) {
            val str = contextObjects.fold("") { acc, name ->
                "$acc, $name = $name.normalize(contextId = \"\${contextId}.$name\")"
            }
            file.appendText("    return this.copy(contextId = contextId$str)")
        } else {
            file.appendText("    return this.copy(contextId = contextId)")
        }

        file.appendText("\n}\n")
        file.appendText("\n")
        file.appendText("val $className.expression: Bind<$className> get() = expressionOf<$className>(\"@{\$contextId}\")\n")
        file.appendText("fun $className.change(${className.decapitalize()}: Bind<$className>) = SetContext(contextId = contextId, value = ${className.decapitalize()})\n")
        file.appendText("fun $className.change(${className.decapitalize()}: $className) = SetContext(contextId = contextId, value = ${className.decapitalize()})\n")
        file.appendText("\n")

        function.parameters.forEach {
            val propertyName = it.name!!.asString()
            val propertyType = it.type.toString()

            if (propertyName != "contextId") {
                file.appendText("val $className.${propertyName}Expression: Bind<$propertyType> get() = expressionOf<${propertyType}>(\"@{\$contextId.${propertyName}}\")\n")
                file.appendText("fun $className.change${propertyName.capitalize()}($propertyName: Bind<$propertyType>) = SetContext(contextId = contextId, path = \"$propertyName\", value = $propertyName)\n")
                file.appendText("fun $className.change${propertyName.capitalize()}($propertyName: $propertyType) = SetContext(contextId = contextId, path = \"$propertyName\", value = $propertyName)\n")
                file.appendText("\n")
            }
        }

        file.close()
    }

    private fun getContextObjects(parameters: List<KSValueParameter>): List<String> {
        return parameters.filter {
            val parameterTypeClassAnnotations = it.type.resolve().declaration.annotations
            parameterTypeClassAnnotations.count() != 0 && parameterTypeClassAnnotations.any { c -> c.shortName.asString() == "Builder" }
        }.map { it.name!!.asString() }
    }

    private fun OutputStream.appendText(str: String) {
        this.write(str.toByteArray())
    }
}