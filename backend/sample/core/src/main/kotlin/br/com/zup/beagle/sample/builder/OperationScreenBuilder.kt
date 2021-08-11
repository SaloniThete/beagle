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

object OperationScreenBuilder : ScreenBuilder {
    override fun build() = Screen(
        navigationBar = NavigationBar(
            title = "Operations",
            showBackButton = true
        ),
        child = Container(
            context = ContextData("text", "tEsT"),
            children = listOf(

                /** String **/
                Text(capitalize(expressionOf("@{text}")).convertToString()),
                Text(concat(constant("aaa"), constant("bbb"), expressionOf("@{text}")).convertToString()),
                Text(lowercase(expressionOf("@{text}")).convertToString()),
                Text(uppercase(expressionOf("@{text}")).convertToString()),
                Text(substring(constant("testing"), constant(3)).convertToString()),

                Container(
                    context = ContextData("number", 4),
                    children = listOf(

                        /** Number **/
                        Text(sum(constant(1), constant(2), expressionOf("@{number}")).convertToString()),
                        Text(sum(expressionOf("@{number}"), expressionOf("@{number}")).convertToString()),
                        Text(sum(constant(1), constant(2)).convertToString()),
                        Text(sum(constant(1), sum(constant(2), expressionOf("@{number}"))).convertToString()),

                        Text(subtract(constant(1), constant(2), expressionOf("@{number}")).convertToString()),
                        Text(multiply(constant(1), constant(2), expressionOf("@{number}")).convertToString()),
                        Text(divide(constant(10.0), constant(2.0), expressionOf("@{number}")).convertToString())
                    )
                ),
                Container(
                    context = ContextData("comparison", 3),
                    children = listOf(

                        /** Comparison **/
                        Text(eq(constant(3), expressionOf("@{comparison}")).convertToString()),

                        Text(gt(expressionOf("@{comparison}"), constant(3.2)).convertToString()),
                        Text(gt(expressionOf("@{comparison}"), constant(4)).convertToString()),

                        Text(gte(expressionOf("@{comparison}"), constant(2)).convertToString()),

                        Text(lt(constant(2), expressionOf("@{comparison}")).convertToString()),

                        Text(lte(constant(2), expressionOf("@{comparison}")).convertToString()),
                        Text(lte(expressionOf("@{comparison}"), expressionOf("@{comparison}")).convertToString()),
                    )
                ),
                Container(
                    context = ContextData("logic", false),
                    children = listOf(

                        /** logic **/
                        Text(and(constant(true), expressionOf("@{logic}")).convertToString()),
                        Text(condition(constant(true), expressionOf("@{logic}")).convertToString()),
                        Text(not(expressionOf("@{logic}"), constant(true)).convertToString()),
                        Text(or(constant(true), expressionOf("@{logic}")).convertToString())
                    )
                ),
                Container(
                    context = ContextData(id = "numbersArray", ArrayTest(array1 = arrayOf(1, 2, 3), array2 = arrayOf(4, 5, 6))),
                    children = listOf(

                        /** Aray **/
                        Text(contains(expressionOf("@{numbersArray.array1}"), constant(0)).convertToString()),
                        Text(remove(expressionOf("@{numbersArray.array2}"), constant(4)).convertToString()),
                        Text(removeIndex(expressionOf("@{numbersArray.array1}"), constant(1)).convertToString()),
                        Text(insert(expressionOf("@{numbersArray.array2}"), constant(7)).convertToString()),
                        Text(union(expressionOf("@{numbersArray.array1}"), expressionOf("@{numbersArray.array2}")).convertToString())
                    )
                ),
                Container(
                    context = ContextData("stringArray", arrayOf("a,b,c,d")),
                    children = listOf(
                        Text(contains(expressionOf("@{stringArray}"), constant("a,b,c,d")).convertToString()),
                        Text(insert(expressionOf("@{stringArray}"), constant("e")).convertToString())
                    )
                ),
                Container(
                    context = ContextData("other", arrayOf(0, 1, 2, 3, 4)),
                    children = listOf(

                        /** other **/
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