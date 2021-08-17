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

package br.com.zup.beagle.android.compiler.registrar

import br.com.zup.beagle.android.compiler.BeagleSetupProcessor.Companion.REGISTERED_ACTIONS_GENERATED
import br.com.zup.beagle.android.compiler.extensions.compile
import br.com.zup.beagle.android.compiler.mocks.BEAGLE_CONFIG_IMPORTS
import br.com.zup.beagle.android.compiler.mocks.INTERNAL_ACTION_REGISTRAR_EXPECTED
import br.com.zup.beagle.android.compiler.mocks.INTERNAL_ACTION_REGISTRAR_NO_ACTION_EXPECTED
import br.com.zup.beagle.android.compiler.mocks.SIMPLE_BEAGLE_CONFIG
import br.com.zup.beagle.android.compiler.mocks.VALID_LIST_ACTION
import br.com.zup.beagle.android.compiler.processor.BeagleAnnotationProcessor
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("Given Beagle Annotation Processor")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ActionRegistrarGeneratorTest : RegistrarGeneratorBaseTest() {

    private val registrarClassName = "$REGISTERED_ACTIONS_GENERATED$REGISTRAR_SUFFIX"

    @DisplayName("When register action")
    @Nested
    inner class Actions {
        @Test
        @DisplayName("Then should create Registrar class with list of actions")
        fun testGenerateListOfActionsCorrect() {
            // GIVEN
            val kotlinSource = SourceFile.kotlin(FILE_NAME,
                BEAGLE_CONFIG_IMPORTS + VALID_LIST_ACTION + SIMPLE_BEAGLE_CONFIG)

            // WHEN
            val compilationResult = compile(kotlinSource, BeagleAnnotationProcessor(), tempPath)

            // THEN
            val file = compilationResult.generatedFiles.find { file -> file.name.startsWith(registrarClassName) }!!
            val fileGeneratedInString = file.readText().replace(REGEX_REMOVE_SPACE, "")
            val fileExpectedInString = INTERNAL_ACTION_REGISTRAR_EXPECTED.replace(REGEX_REMOVE_SPACE, "")

            Assertions.assertEquals(fileExpectedInString, fileGeneratedInString)
            Assertions.assertEquals(KotlinCompilation.ExitCode.OK, compilationResult.exitCode)
        }

        @Test
        @DisplayName("Then should create Registrar class with empty list of actions")
        fun testGenerateEmptyListOfActionsCorrect() {
            // GIVEN
            val kotlinSource = SourceFile.kotlin(FILE_NAME,
                BEAGLE_CONFIG_IMPORTS + SIMPLE_BEAGLE_CONFIG)

            // WHEN
            val compilationResult = compile(kotlinSource, BeagleAnnotationProcessor(), tempPath)

            // THEN
            val file = compilationResult.generatedFiles.find { file -> file.name.startsWith(registrarClassName) }!!
            val fileGeneratedInString = file.readText().replace(REGEX_REMOVE_SPACE, "")
            val fileExpectedInString = INTERNAL_ACTION_REGISTRAR_NO_ACTION_EXPECTED.replace(REGEX_REMOVE_SPACE, "")

            Assertions.assertEquals(fileExpectedInString, fileGeneratedInString)
            Assertions.assertEquals(KotlinCompilation.ExitCode.OK, compilationResult.exitCode)
        }
    }

    @DisplayName("When build application with beagle.moduleName kapt argument")
    @Nested
    inner class KaptArgument {

        @Test
        @DisplayName("Then should generate RegisteredActionsRegistrar class name with correct suffix")
        fun testGenerateClassNameWithCorrectSuffix() {
            //GIVEN
            val kotlinSource = SourceFile.kotlin(FILE_NAME,
                BEAGLE_CONFIG_IMPORTS + VALID_LIST_ACTION + SIMPLE_BEAGLE_CONFIG)

            val testModuleName = "MyTestModule"

            val expectedRegistrarClassName = "${REGISTERED_ACTIONS_GENERATED}Registrar$testModuleName.kt"

            val kaptArguments = mutableMapOf(KAPT_OPTION_NAME to testModuleName)

            // WHEN
            val compilationResult = compile(kotlinSource, BeagleAnnotationProcessor(), tempPath, kaptArguments)

            // THEN
            val file = compilationResult.generatedFiles.find { file ->
                file.name.startsWith(expectedRegistrarClassName)
            }
            Assertions.assertEquals(KotlinCompilation.ExitCode.OK, compilationResult.exitCode)
            Assertions.assertNotNull(file)
        }
    }
}