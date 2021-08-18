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
import br.com.zup.beagle.context.expressionOf
import br.com.zup.beagle.context.operations.builtin.isEmpty
import br.com.zup.beagle.context.operations.builtin.isNull
import br.com.zup.beagle.context.operations.builtin.length
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("Given a Operation")
internal class OtherOperationsTest {

    @DisplayName("When passing parameters to isEmpty operation")
    @Nested
    inner class IsEmptyOperationTest {

        @Test
        @DisplayName("Then should return correct expression")
        fun checkIsEmptyExpression() {
            // GIVEN
            val expected = Bind.Expression<Boolean>(value = "@{isEmpty(myArray)}")

            // WHEN
            val result = isEmpty(expressionOf<Boolean>("@{myArray}"))

            // THEN
            assertEquals(expected, result)
        }
    }

    @DisplayName("When passing parameters to isNull operation")
    @Nested
    inner class IsNullOperationTest {

        @Test
        @DisplayName("Then should return correct expression")
        fun checkIsEmptyExpression() {
            // GIVEN
            val expected = Bind.Expression<Boolean>(value = "@{isNull(myArray)}")

            // WHEN
            val result = isNull(expressionOf<Boolean>("@{myArray}"))

            // THEN
            assertEquals(expected, result)
        }
    }

    @DisplayName("When passing parameters to length operation")
    @Nested
    inner class LengthOperationTest {

        @Test
        @DisplayName("Then should return correct expression")
        fun checkLengthExpression() {
            // GIVEN
            val expected = Bind.Expression<Number>(value = "@{length(myArray)}")

            // WHEN
            val result = length(expressionOf("@{myArray}"))

            // THEN
            assertEquals(expected, result)
        }
    }
}