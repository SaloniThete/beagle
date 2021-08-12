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

package br.com.zup.beagle.core

import br.com.zup.beagle.context.Bind
import br.com.zup.beagle.context.constant
import br.com.zup.beagle.context.operations.builtin.capitalize
import br.com.zup.beagle.context.operations.builtin.concat
import br.com.zup.beagle.context.operations.builtin.lowercase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given a String Operations")
internal class StringOperationsTest {

    companion object {
        private const val STRING_TEST = "test"
        private const val EMPTY_STRING_TEST = ""
    }

    @DisplayName("When use capitalize operation")
    @Nested
    inner class CapitalizeOperationTest {

        @DisplayName("Then should return the bind of string with capitalize operation")
        @Test
        fun test_capitalize_operation() = run {
            val result = capitalize(constant(Companion.STRING_TEST))
            val expected = Bind.expression<String>("@{capitalize(\'${Companion.STRING_TEST}\')}")

            Assertions.assertEquals(result, expected)
        }

        @DisplayName("Then should return the bind of empty string with capitalize operation")
        @Test
        fun test_capitalize_operation_with_empty_input() = run {
            val result = capitalize(constant(Companion.EMPTY_STRING_TEST))
            val expected = Bind.expression<String>("@{capitalize(\'${Companion.EMPTY_STRING_TEST}\')}")

            Assertions.assertEquals(result, expected)
        }
    }

    @DisplayName("When use concat operation")
    @Nested
    inner class ConcatOperationTest {

        @DisplayName("Then should return the bind of one string with concat operation")
        @Test
        fun test_concat_operation_with_one_parameter() = run {
            val result = concat(constant(Companion.STRING_TEST))
            val expected = Bind.expression<String>("@{concat(\'${Companion.STRING_TEST}\')}")

            Assertions.assertEquals(result, expected)
        }

        @DisplayName("Then should return the bind of two strings with concat operation")
        @Test
        fun test_concat_operation_with_two_parameters() = run {
            val result = concat(constant(Companion.STRING_TEST), constant(STRING_TEST))
            val expected = Bind.expression<String>("@{concat(\'${Companion.STRING_TEST}\','${Companion.STRING_TEST}')}")

            Assertions.assertEquals(result, expected)
        }
    }

    @DisplayName("When use lowercase operation")
    @Nested
    inner class LowerCaseOperationTest {

        @DisplayName("Then should return the bind of string with lowerCase operation")
        @Test
        fun test_lowerCase_operation() = run {
            val result = lowercase(constant(Companion.STRING_TEST))
            val expected = Bind.expression<String>("@{lowercase(\'${Companion.STRING_TEST}\')}")

            Assertions.assertEquals(result, expected)
        }

        @DisplayName("Then should return the bind of empty string with lowerCase operation")
        @Test
        fun test_lowerCase_operation_with_empty_input() = run {
            val result = lowercase(constant(Companion.EMPTY_STRING_TEST))
            val expected = Bind.expression<String>("@{lowercase(\'${Companion.EMPTY_STRING_TEST}\')}")

            Assertions.assertEquals(result, expected)
        }
    }
}
