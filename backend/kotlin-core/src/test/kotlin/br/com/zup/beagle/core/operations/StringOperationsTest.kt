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

package br.com.zup.beagle.core.operations

import br.com.zup.beagle.context.Bind
import br.com.zup.beagle.context.constant
import br.com.zup.beagle.context.operations.builtin.capitalize
import br.com.zup.beagle.context.operations.builtin.concat
import br.com.zup.beagle.context.operations.builtin.lowercase
import br.com.zup.beagle.context.operations.builtin.substring
import br.com.zup.beagle.context.operations.builtin.uppercase
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
            val result = capitalize(constant(STRING_TEST))
            val expected = Bind.expression<String>("@{capitalize(\'$STRING_TEST\')}")

            Assertions.assertEquals(result, expected)
        }

        @DisplayName("Then should return the bind of empty string with capitalize operation")
        @Test
        fun test_capitalize_operation_with_empty_input() = run {
            val result = capitalize(constant(EMPTY_STRING_TEST))
            val expected = Bind.expression<String>("@{capitalize(\'$EMPTY_STRING_TEST\')}")

            Assertions.assertEquals(result, expected)
        }
    }

    @DisplayName("When use concat operation")
    @Nested
    inner class ConcatOperationTest {

        @DisplayName("Then should return the bind of one string with concat operation")
        @Test
        fun test_concat_operation_with_one_parameter() = run {
            val result = concat(constant(STRING_TEST))
            val expected = Bind.expression<String>("@{concat(\'$STRING_TEST\')}")

            Assertions.assertEquals(result, expected)
        }

        @DisplayName("Then should return the bind of two strings with concat operation")
        @Test
        fun test_concat_operation_with_two_parameters() = run {
            val result = concat(constant(STRING_TEST), constant(STRING_TEST))
            val expected = Bind.expression<String>("@{concat(\'$STRING_TEST\','$STRING_TEST')}")

            Assertions.assertEquals(result, expected)
        }
    }

    @DisplayName("When use lowercase operation")
    @Nested
    inner class LowerCaseOperationTest {

        @DisplayName("Then should return the bind of string with lowerCase operation")
        @Test
        fun test_lowercase_operation() = run {
            val result = lowercase(constant(STRING_TEST))
            val expected = Bind.expression<String>("@{lowercase(\'$STRING_TEST\')}")

            Assertions.assertEquals(result, expected)
        }

        @DisplayName("Then should return the bind of empty string with lowerCase operation")
        @Test
        fun test_lowercase_operation_with_empty_input() = run {
            val result = lowercase(constant(EMPTY_STRING_TEST))
            val expected = Bind.expression<String>("@{lowercase(\'$EMPTY_STRING_TEST\')}")

            Assertions.assertEquals(result, expected)
        }
    }

    @DisplayName("When use uppercase operation")
    @Nested
    inner class UpperCaseOperationTest {

        @DisplayName("Then should return the bind of string with uppercase operation")
        @Test
        fun test_uppercase_operation() = run {
            val result = uppercase(constant(STRING_TEST))
            val expected = Bind.expression<String>("@{uppercase(\'$STRING_TEST\')}")

            Assertions.assertEquals(result, expected)
        }

        @DisplayName("Then should return the bind of empty string with uppercase operation")
        @Test
        fun test_uppercase_operation_with_empty_input() = run {
            val result = uppercase(constant(EMPTY_STRING_TEST))
            val expected = Bind.expression<String>("@{uppercase(\'$EMPTY_STRING_TEST\')}")

            Assertions.assertEquals(result, expected)
        }
    }

    @DisplayName("When use substring operation")
    @Nested
    inner class SubstringOperationTest {

        @DisplayName("Then should return the bind of string with substring operation")
        @Test
        fun test_uppercase_operation() = run {
            val startIndex = 2
            val result = substring(constant(STRING_TEST), constant(startIndex))
            val expected = Bind.expression<String>("@{substr(\'$STRING_TEST\',${startIndex})}")

            Assertions.assertEquals(result, expected)
        }
    }
}
