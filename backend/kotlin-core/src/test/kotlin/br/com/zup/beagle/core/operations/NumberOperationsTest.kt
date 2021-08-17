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
import br.com.zup.beagle.context.operations.builtin.divide
import br.com.zup.beagle.context.operations.builtin.multiply
import br.com.zup.beagle.context.operations.builtin.subtract
import br.com.zup.beagle.context.operations.builtin.sum
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("Given a Operation")
internal class NumberOperationTest {

    @DisplayName("When passing parameters to sum operation")
    @Nested
    inner class SumOperationTest {

        @Test
        @DisplayName("Then should return correct expression")
        fun checkSumExpression() {
            // GIVEN
            val expected = Bind.Expression<Number>(value = "@{sum(1,2)}")

            // WHEN
            val result = sum(constant(1), constant(2))

            // THEN
            assertEquals(expected, result)
        }

        @Test
        @DisplayName("Then should return correct expression")
        fun checkSumDoubleExpression() {
            // GIVEN
            val expected = Bind.Expression<Number>(value = "@{sum(2.5,2.5)}")

            // WHEN
            val result = sum(constant(2.5), constant(2.5))

            // THEN
            assertEquals(expected, result)
        }
    }

    @DisplayName("When passing parameters to subtract operation")
    @Nested
    inner class SubtractOperationTest {

        @Test
        @DisplayName("Then should return correct expression")
        fun checkSubtractExpression() {
            // GIVEN
            val expected = Bind.Expression<Number>(value = "@{subtract(10,5)}")

            // WHEN
            val result = subtract(constant(10), constant(5))

            // THEN
            assertEquals(expected, result)
        }

        @Test
        @DisplayName("Then should return correct expression")
        fun checkSubtractDoubleExpression() {
            // GIVEN
            val expected = Bind.Expression<Number>(value = "@{subtract(10.5,5.5)}")

            // WHEN
            val result = subtract(constant(10.5), constant(5.5))

            // THEN
            assertEquals(expected, result)
        }

        @DisplayName("When passing parameters to multiply operation")
        @Nested
        inner class MultiplyOperationTest {

            @Test
            @DisplayName("Then should return correct expression")
            fun checkMultiplyExpression() {
                // GIVEN
                val expected = Bind.Expression<Number>(value = "@{multiply(2,2)}")

                // WHEN
                val result = multiply(constant(2), constant(2))

                // THEN
                assertEquals(expected, result)
            }

            @Test
            @DisplayName("Then should return correct expression")
            fun checkMultiplyDoubleExpression() {
                // GIVEN
                val expected = Bind.Expression<Number>(value = "@{multiply(2.5,2.5)}")

                // WHEN
                val result = multiply(constant(2.5), constant(2.5))

                // THEN
                assertEquals(expected, result)
            }
        }

        @DisplayName("When passing parameters to divide operation")
        @Nested
        inner class DivideOperationTest {
            @Test
            @DisplayName("Then should return correct expression")
            fun checkDivideExpression() {
                // GIVEN
                val expected = Bind.Expression<Number>(value = "@{divide(10,5)}")

                // WHEN
                val result = divide(constant(10), constant(5))

                // THEN
                assertEquals(expected, result)
            }

            @Test
            @DisplayName("Then should return correct expression")
            fun checkDivideDoubleExpression() {
                // GIVEN
                val expected = Bind.Expression<Number>(value = "@{divide(10.5,5.25)}")

                // WHEN
                val result = divide(constant(10.5), constant(5.25))

                // THEN
                assertEquals(expected, result)
            }
        }
    }
}

