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
import br.com.zup.beagle.context.operations.builtin.and
import br.com.zup.beagle.context.operations.builtin.condition
import br.com.zup.beagle.context.operations.builtin.not
import br.com.zup.beagle.context.operations.builtin.or
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("Given a Operation")
internal class LogicOperationTest {

    @DisplayName("When passing parameters to and operation")
    @Nested
    inner class AndOperationTest {

        @Test
        @DisplayName("Then should return correct expression")
        fun checkAndExpression() {
            // GIVEN
            val expected = Bind.Expression<Boolean>(value = "@{and(true,true,false)}")

            // WHEN
            val result = and(constant(true), constant(true), constant(false))

            // THEN
            assertEquals(expected, result)
        }
    }

    @DisplayName("When passing parameters to condition operation")
    @Nested
    inner class ConditionOperationTest {

        @Test
        @DisplayName("Then should return correct expression")
        fun checkConditionExpression() {
            // GIVEN
            val expected = Bind.Expression<Boolean>(value = "@{condition(true,true,false)}")

            // WHEN
            val result = condition(constant(true), constant(true), constant(false))

            // THEN
            assertEquals(expected, result)
        }

        @DisplayName("When passing parameters to not operation")
        @Nested
        inner class NotOperationTest {

            @Test
            @DisplayName("Then should return correct expression")
            fun checkNotExpression() {
                // GIVEN
                val expected = Bind.Expression<Boolean>(value = "@{not(false,false)}")

                // WHEN
                val result = not(constant(false), constant(false))

                // THEN
                assertEquals(expected, result)
            }
        }

        @DisplayName("When passing parameters to or operation")
        @Nested
        inner class OrOperationTest {
            @Test
            @DisplayName("Then should return correct expression")
            fun checkOrExpression() {
                // GIVEN
                val expected = Bind.Expression<Boolean>(value = "@{or(true,false)}")

                // WHEN
                val result = or(constant(true), constant(false))

                // THEN
                assertEquals(expected, result)
            }
        }
    }
}

