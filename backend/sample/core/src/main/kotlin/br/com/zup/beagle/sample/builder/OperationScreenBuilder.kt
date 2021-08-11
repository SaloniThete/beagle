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

package br.com.zup.beagle.sample.builder

import br.com.zup.beagle.context.constant
import br.com.zup.beagle.context.expressionOf
import br.com.zup.beagle.context.operations.builtin.and
import br.com.zup.beagle.context.operations.builtin.capitalize
import br.com.zup.beagle.context.operations.builtin.concat
import br.com.zup.beagle.context.operations.builtin.condition
import br.com.zup.beagle.context.operations.builtin.contains
import br.com.zup.beagle.context.operations.builtin.convertToString
import br.com.zup.beagle.context.operations.builtin.divide
import br.com.zup.beagle.context.operations.builtin.eq
import br.com.zup.beagle.context.operations.builtin.gt
import br.com.zup.beagle.context.operations.builtin.gte
import br.com.zup.beagle.context.operations.builtin.insert
import br.com.zup.beagle.context.operations.builtin.isEmpty
import br.com.zup.beagle.context.operations.builtin.isNull
import br.com.zup.beagle.context.operations.builtin.length
import br.com.zup.beagle.context.operations.builtin.lowercase
import br.com.zup.beagle.context.operations.builtin.lt
import br.com.zup.beagle.context.operations.builtin.lte
import br.com.zup.beagle.context.operations.builtin.multiply
import br.com.zup.beagle.context.operations.builtin.not
import br.com.zup.beagle.context.operations.builtin.or
import br.com.zup.beagle.context.operations.builtin.remove
import br.com.zup.beagle.context.operations.builtin.removeIndex
import br.com.zup.beagle.context.operations.builtin.substring
import br.com.zup.beagle.context.operations.builtin.subtract
import br.com.zup.beagle.context.operations.builtin.sum
import br.com.zup.beagle.context.operations.builtin.toLowerCase
import br.com.zup.beagle.context.operations.builtin.union
import br.com.zup.beagle.context.operations.builtin.uppercase
import br.com.zup.beagle.ext.setStyle
import br.com.zup.beagle.widget.context.ContextData
import br.com.zup.beagle.widget.core.EdgeValue
import br.com.zup.beagle.widget.core.UnitValue
import br.com.zup.beagle.widget.layout.Container
import br.com.zup.beagle.widget.layout.NavigationBar
import br.com.zup.beagle.widget.layout.Screen
import br.com.zup.beagle.widget.layout.ScreenBuilder
import br.com.zup.beagle.widget.ui.Text

data class ArrayTest(val array1: Any, val array2: Any)
data class ArrayTest2(val array1: Any, val array2: Any, val array3: Any, val array4: Any, val array5: Any, val array6: Any)

object OperationScreenBuilder : ScreenBuilder {
    override fun build() = Screen(
        navigationBar = NavigationBar(
            title = "Operations",
            showBackButton = true
        ),
        child = Container(
            context = ContextData("text", "tEsT"),
            children = listOf(
                Text("String", textColor = "#00c91b"),
                Text(capitalize(expressionOf("@{text}")).convertToString()),
                Text(concat(constant("aaa"), constant("bbb"), expressionOf("@{text}")).convertToString()),
                Text(lowercase(expressionOf("@{text}")).convertToString()),
                Text((constant("TeStINg".substring(3)).toLowerCase()).convertToString()),
                Text(uppercase(expressionOf("@{text}")).convertToString()),
                Text(substring(constant("testing"), constant(3)).convertToString()),

                Container(
                    context = ContextData("number", 4),
                    children = listOf(
                        Text("Number", textColor = "#00c91b"),
                        Text(sum(constant(1), constant(2), expressionOf("@{number}")).convertToString()),
                        Text(sum(expressionOf("@{number}"), expressionOf("@{number}")).convertToString()),
                        Text(sum(constant(1), constant(2)).convertToString()),
                        Text(sum(constant(1), sum(constant(2), expressionOf("@{number}"))).convertToString()),

                        Text(subtract(constant(1), constant(2), expressionOf("@{number}")).convertToString()),
                        Text(multiply(constant(1), constant(2), expressionOf("@{number}")).convertToString()),
                        Text(divide(constant(10.0), constant(2.0), expressionOf("@{number}")).convertToString())
                    )
                ).setStyle { margin = EdgeValue(top = UnitValue.Companion.real(10), bottom = UnitValue.Companion.real(10)) },
                Container(
                    context = ContextData("comparison", 3),
                    children = listOf(
                        Text("comparison", textColor = "#00c91b"),
                        Text(eq(constant(3), expressionOf("@{comparison}")).convertToString()),

                        Text(gt(expressionOf("@{comparison}"), constant(3.2)).convertToString()),
                        Text(gt(expressionOf("@{comparison}"), constant(4)).convertToString()),

                        Text(gte(expressionOf("@{comparison}"), constant(2)).convertToString()),

                        Text(lt(constant(2), expressionOf("@{comparison}")).convertToString()),

                        Text(lte(constant(2), expressionOf("@{comparison}")).convertToString()),
                        Text(lte(expressionOf("@{comparison}"), expressionOf("@{comparison}")).convertToString()),
                    )
                ).setStyle { margin = EdgeValue(bottom = UnitValue.Companion.real(10)) },
                Container(
                    context = ContextData("logic", false),
                    children = listOf(
                        Text("logic", textColor = "#00c91b"),
                        Text(and(constant(true), expressionOf("@{logic}")).convertToString()),
                        Text(condition(constant(true), expressionOf("@{logic}")).convertToString()),
                        Text(not(expressionOf("@{logic}"), constant(true)).convertToString()),
                        Text(or(constant(true), expressionOf("@{logic}")).convertToString())
                    )
                ).setStyle { margin = EdgeValue(bottom = UnitValue.Companion.real(10)) },
                Container(
                    context = ContextData(id = "numbersArray", ArrayTest(array1 = arrayOf(1, 2, 3), array2 = arrayOf(4, 5, 6))),
                    children = listOf(
                        Text("Array Test 1", textColor = "#00c91b"),
                        Text(contains(expressionOf("@{numbersArray.array1}"), constant(0)).convertToString()),
                        Text(remove(expressionOf("@{numbersArray.array2}"), constant(4)).convertToString()),
                        Text(removeIndex(expressionOf("@{numbersArray.array1}"), constant(0)).convertToString()),
                        Text(insert(expressionOf("@{numbersArray.array2}"), constant(7)).convertToString()),
                        Text(insert(expressionOf("@{numbersArray.array2}"), constant(8)).convertToString()),
                        Text(union(expressionOf("@{numbersArray.array1}"), expressionOf("@{numbersArray.array2}")).convertToString()),

                        Text(union(expressionOf("@{numbersArray.array1}")).convertToString()),
                    )
                ),
                Container(
                    context = ContextData(id = "array",
                        ArrayTest2(array1 = arrayOf(1, 2, 3), array2 = arrayOf(4, 5, 6), array3 = arrayOf(7, 8, 9),
                            array4 = arrayOf(10, 11, 12), array5 = arrayOf(13, 14, 15), array6 = arrayOf(16, 17, 18))),
                    children = listOf(
                        Text("Array Test 2", textColor = "#00c91b"),
                        Text(contains(expressionOf("@{array.array1}"), constant(0)).convertToString()),
                        Text(remove(expressionOf("@{array.array2}"), constant(4)).convertToString()),
                        Text(removeIndex(expressionOf("@{array.array3}"), constant(1)).convertToString()),
                        Text(insert(expressionOf("@{array.array4}"), constant(13)).convertToString()),
                        Text(union(expressionOf("@{array.array5}"), expressionOf("@{numbersArray.array6}")).convertToString()),
                    )
                ),
                Container(
                    context = ContextData("other", arrayOf(0, 1, 2, 3, 4)),
                    children = listOf(
                        Text("other", textColor = "#00c91b"),
                        Text(isEmpty(expressionOf("@{other}")).convertToString()),
                        Text(isNull(expressionOf("@{other}")).convertToString()),
                        Text(length(expressionOf("@{other}")).convertToString())
                    )
                )
            )
        ).setStyle {
            margin = EdgeValue(all = UnitValue.real(10))
        }
    )

}