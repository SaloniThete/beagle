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
import br.com.zup.beagle.context.operations.builtin.eq
import br.com.zup.beagle.context.operations.builtin.gt
import br.com.zup.beagle.context.operations.builtin.gte
import br.com.zup.beagle.context.operations.builtin.lt
import br.com.zup.beagle.context.operations.builtin.lte
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("Given a Operation")
internal class ComparisonOperationTest {

    @DisplayName("When passing parameters to eq operation")
    @Nested
    inner class Eq {

        @Test
        @DisplayName("Then should return correct expression")
        fun checkEqExpression() {
            // GIVEN
            val expected = Bind.Expression<Boolean>(value = "@{eq(1,2)}")

            // WHEN
            val result = eq(constant(1), constant(2))

            // THEN
            assertEquals(expected, result)
        }

        @Test
        @DisplayName("Then should return correct expression")
        fun checkEqDoubleExpression() {
            // GIVEN
            val expected = Bind.Expression<Boolean>(value = "@{eq(2.5,2.5)}")

            // WHEN
            val result = eq(constant(2.5), constant(2.5))

            // THEN
            assertEquals(expected, result)
        }
    }

    @DisplayName("When passing parameters to gt operation")
    @Nested
    inner class GtOperationTest {

        @Test
        @DisplayName("Then should return correct expression")
        fun checkGtExpression() {
            // GIVEN
            val expected = Bind.Expression<Boolean>(value = "@{gt(10,5)}")

            // WHEN
            val result = gt(constant(10), constant(5))

            // THEN
            assertEquals(expected, result)
        }

        @DisplayName("When passing parameters to gte operation")
        @Nested
        inner class GteOperationTest {

            @Test
            @DisplayName("Then should return correct expression")
            fun checkGteExpression() {
                // GIVEN
                val expected = Bind.Expression<Boolean>(value = "@{gte(10,2)}")

                // WHEN
                val result = gte(constant(10), constant(2))

                // THEN
                assertEquals(expected, result)
            }
        }

        @DisplayName("When passing parameters to lt operation")
        @Nested
        inner class LtOperationTest {
            @Test
            @DisplayName("Then should return correct expression")
            fun checkDivideExpression() {
                // GIVEN
                val expected = Bind.Expression<Boolean>(value = "@{lt(5,2)}")

                // WHEN
                val result = lt(constant(5), constant(2))

                // THEN
                assertEquals(expected, result)
            }
        }

        @DisplayName("When passing parameters to lte operation")
        @Nested
        inner class LteOperationTest {
            @Test
            @DisplayName("Then should return correct expression")
            fun checkDivideExpression() {
                // GIVEN
                val expected = Bind.Expression<Boolean>(value = "@{lte(2,2)}")

                // WHEN
                val result = lte(constant(2), constant(2))

                // THEN
                assertEquals(expected, result)
            }
        }
    }
}

