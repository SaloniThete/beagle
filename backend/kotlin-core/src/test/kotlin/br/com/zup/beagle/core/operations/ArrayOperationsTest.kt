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
import br.com.zup.beagle.context.operations.builtin.contains
import br.com.zup.beagle.context.operations.builtin.insert
import br.com.zup.beagle.context.operations.builtin.remove
import br.com.zup.beagle.context.operations.builtin.removeIndex
import br.com.zup.beagle.context.operations.builtin.union
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given a Array Operations")
internal class ArrayOperationsTest {

    companion object {
        private val ARRAY_TEST = arrayOf(1, 2, 3)
        private val EMPTY_ARRAY_TEST = arrayOf<Any>()
    }

    @DisplayName("When use contains operation")
    @Nested
    inner class ContainsOperationTest {

        @DisplayName("Then should return expression with contains operation")
        @Test
        fun test_contains_operation() = run {
            val result = contains(constant(ARRAY_TEST))
            val expected = Bind.expression<String>("@{contains($ARRAY_TEST)}")

            Assertions.assertEquals(result, expected)
        }

        @DisplayName("Then should return expression with contains operation")
        @Test
        fun test_contains_operation_empty_list() = run {
            val result = contains(constant(EMPTY_ARRAY_TEST))
            val expected = Bind.expression<String>("@{contains($EMPTY_ARRAY_TEST)}")

            Assertions.assertEquals(result, expected)
        }
    }

    @DisplayName("When use insert operation")
    @Nested
    inner class InsertOperationTest {

        @DisplayName("Then should return expression with insert operation")
        @Test
        fun test_insert_operation_with_one_parameter() = run {
            val result = insert(constant(ARRAY_TEST), constant(5), constant(0))
            val expected = Bind.expression<String>("@{insert($ARRAY_TEST,5,0)}")

            Assertions.assertEquals(result, expected)
        }

        @DisplayName("Then should return expression with insert operation")
        @Test
        fun test_insert_operation_without_index() = run {
            val result = insert(constant(ARRAY_TEST), constant(5))
            val expected = Bind.expression<String>("@{insert($ARRAY_TEST,5)}")

            Assertions.assertEquals(result, expected)
        }
    }

    @DisplayName("When use remove operation")
    @Nested
    inner class RemoveOperationTest {

        @DisplayName("Then should return expression with remove operation")
        @Test
        fun test_remove_operation_with_one_parameter() = run {
            val result = remove(constant(ARRAY_TEST), constant(5))
            val expected = Bind.expression<String>("@{remove($ARRAY_TEST,5)}")

            Assertions.assertEquals(result, expected)
        }
    }

    @DisplayName("When use removeIndex operation")
    @Nested
    inner class RemoveIndexOperationTest {

        @DisplayName("Then should return expression with removeIndex operation")
        @Test
        fun test_removeIndex_operation_with_one_parameter() = run {
            val result = removeIndex(constant(ARRAY_TEST), constant(5))
            val expected = Bind.expression<String>("@{removeIndex($ARRAY_TEST,5)}")

            Assertions.assertEquals(result, expected)
        }
    }

    @DisplayName("When use union operation")
    @Nested
    inner class UnionOperationTest {

        @DisplayName("Then should return expression with union operation")
        @Test
        fun test_union_operation_with_one_parameter() = run {
            val result = union(constant(ARRAY_TEST), constant(ARRAY_TEST))
            val expected = Bind.expression<String>("@{union($ARRAY_TEST,$ARRAY_TEST)}")

            Assertions.assertEquals(result, expected)
        }
    }
}
