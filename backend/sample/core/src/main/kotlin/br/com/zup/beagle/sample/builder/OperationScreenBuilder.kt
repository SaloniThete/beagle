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

import br.com.zup.beagle.context.Bind
import br.com.zup.beagle.context.constant
import br.com.zup.beagle.context.expressionOf
import br.com.zup.beagle.context.operations.builtin.*
import br.com.zup.beagle.ext.setStyle
import br.com.zup.beagle.widget.context.ContextData
import br.com.zup.beagle.widget.core.EdgeValue
import br.com.zup.beagle.widget.core.UnitValue
import br.com.zup.beagle.widget.layout.*
import br.com.zup.beagle.widget.ui.Text

data class ArrayTest(val array1: Any, val array2: Any)
data class ArrayTest2(
    val array1: Any,
    val array2: Any,
    val array3: Any,
    val array4: Any,
    val array5: Any,
    val array6: Any
)

data class Beagle(val array1: Any, val array2: Any)

object OperationScreenBuilder : ScreenBuilder {
    override fun build() = Screen(
        navigationBar = NavigationBar(
            title = "Operations",
            showBackButton = true
        ),
        child = ScrollView(
            children = listOf(
                Container(
                    context = ContextData("text", "tEsT"),
                    children = listOf(
                        stringOperations(),
                        numberOperations(),
                        comparisonOperations(),
                        logicOperations(),
                        otherOperations(),
                        arrayOperations()
                    )
                ).setStyle {
                    margin =
                        EdgeValue(all = UnitValue.Companion.real(10))
                }
            )
        )
    )

    private fun stringOperations() = Container(
        children = listOf(
            Text("String", textColor = "#00c91b"),
            Text(uppercase(expressionOf("@{text}")).capitalize().toBindString()),

            Text(concat(constant("aaa"), constant("bbb"), expressionOf("@{text}")).toBindString()),

            Text(lowercase(expressionOf("@{text}")).toBindString()),
            Text((constant("TeStINg".substring(3)).toLowerCase()).toBindString()),

            Text(uppercase(expressionOf("@{text}")).toBindString()),
            Text(uppercase(expressionOf("@{text}")).toUpperCase().toBindString()),

            Text(substring(constant("testing"), constant(3)).toBindString())
        )
    )

    private fun numberOperations() = Container(
        children = listOf(
            Container(
                context = ContextData("number", 4),
                children = listOf(
                    Text("Number", textColor = "#00c91b"),
                    Text(sum(constant(1), constant(2), expressionOf("@{number}")).toBindString()),
                    Text(sum(expressionOf("@{number}"), expressionOf("@{number}")).toBindString()),
                    Text(sum(constant(1), constant(2)).toBindString()),
                    Text(sum(constant(1), sum(constant(2), expressionOf("@{number}"))).toBindString()),

                    Text(subtract(constant(1), constant(2), expressionOf("@{number}")).toBindString()),
                    Text(multiply(constant(1), constant(2), expressionOf("@{number}")).toBindString()),
                    Text(divide(constant(10.0), constant(2.0), expressionOf("@{number}")).toBindString())
                )
            ).setStyle {
                margin =
                    EdgeValue(top = UnitValue.Companion.real(10), bottom = UnitValue.Companion.real(10))
            }
        )
    )

    private fun comparisonOperations() = Container(
        context = ContextData("comparison", 3),
        children = listOf(
            Text("comparison", textColor = "#00c91b"),
            Text(eq(constant(3), expressionOf("@{comparison}")).toBindString()),

            Text(gt(expressionOf("@{comparison}"), constant(3.2)).toBindString()),
            Text(gt(expressionOf("@{comparison}"), constant(4)).toBindString()),

            Text(gte(expressionOf("@{comparison}"), constant(2)).toBindString()),

            Text(lt(constant(2), expressionOf("@{comparison}")).toBindString()),

            Text(lte(constant(2), expressionOf("@{comparison}")).toBindString()),
            Text(lte(expressionOf("@{comparison}"), expressionOf("@{comparison}")).toBindString()),
        )
    ).setStyle { margin = EdgeValue(bottom = UnitValue.Companion.real(10)) }

    private fun logicOperations() = Container(
        context = ContextData("logic", false),
        children = listOf(
            Text("logic", textColor = "#00c91b"),
            Text(and(constant(true), expressionOf("@{logic}")).toBindString()),
            Text(
                condition<Bind<Boolean>>(
                    constant(true), expressionOf("@{logic}"),
                    expressionOf("@{logic}")
                ).toBindString()
            ),
            Text(not(expressionOf("@{logic}"), constant(true)).toBindString()),
            Text(or(constant(true), expressionOf("@{logic}")).toBindString())
        )
    ).setStyle { margin = EdgeValue(bottom = UnitValue.Companion.real(10)) }

    private fun otherOperations() = Container(
        context = ContextData("other", arrayOf(0, 1, 2, 3, 4)),
        children = listOf(
            Text("other", textColor = "#00c91b"),
            Text(isEmpty(expressionOf<Boolean>("@{other}")).toBindString()),
            Text(isNull(expressionOf<Boolean>("@{other}")).toBindString()),
            Text(length(expressionOf("@{other}")).toBindString())
        )
    ).setStyle { margin = EdgeValue(bottom = UnitValue.Companion.real(10)) }

    private fun arrayOperations() = Container(
        children = listOf(
            Text("Array", textColor = "#00c91b"),
            Container(
                context = ContextData(
                    id = "arrayA",
                    ArrayTest(array1 = arrayOf(1, 2, 3), array2 = arrayOf(4, 5, 6))
                ),
                children = listOf(
                    Text(contains(expressionOf("@{arrayA.array1}"), constant(0)).toBindString())
                )
            ),
            Container(
                context = ContextData(
                    id = "arrayB",
                    ArrayTest(array1 = arrayOf(1, 2, 3), array2 = arrayOf(4, 5, 6))
                ),
                children = listOf(
                    Text(remove(expressionOf("@{arrayB.array2}"), constant(4)).toBindString())
                )
            ),
            Container(
                context = ContextData(
                    id = "arrayC",
                    ArrayTest(array1 = arrayOf(1, 2, 3), array2 = arrayOf(4, 5, 6))
                ),
                children = listOf(
                    Text(removeIndex<Number>(expressionOf("@{arrayC.array1}"), constant(0)).toBindString())
                )
            ),
            Container(
                context = ContextData(
                    id = "arrayD",
                    ArrayTest(array1 = arrayOf(1, 2, 3), array2 = arrayOf(4, 5, 6))
                ),
                children = listOf(
                    Text(insert(expressionOf("@{arrayD.array2}"), constant(7)).toBindString())
                )
            ),
            Container(
                context = ContextData(
                    id = "arrayE",
                    ArrayTest(array1 = arrayOf(1, 2, 3), array2 = arrayOf(4, 5, 6))
                ),
                children = listOf(
                    Text(
                        union<Number>(
                            expressionOf("@{arrayD.array1}"),
                            expressionOf("@{arrayE.array2}")
                        ).toBindString()
                    )
                )
            ),
            Container(
                context = ContextData(
                    id = "numbersArray",
                    ArrayTest(array1 = arrayOf(1, 2, 3), array2 = arrayOf(4, 5, 6))
                ),
                children = listOf(
                    Text("Array Test 1", textColor = "#00c91b"),
                    Text(contains(expressionOf("@{numbersArray.array1}"), constant(0)).toBindString()),
                    Text(remove(expressionOf("@{numbersArray.array2}"), constant(4)).toBindString()),
                    Text(
                        removeIndex<Number>(
                            expressionOf("@{numbersArray.array1}"),
                            constant(0)
                        ).toBindString()
                    ),
                    Text(insert(expressionOf("@{numbersArray.array2}"), constant(7)).toBindString()),
                    Text(
                        union<Number>(
                            expressionOf("@{numbersArray.array1}"),
                            expressionOf("@{numbersArray.array2}")
                        ).toBindString()
                    )
                )
            ).setStyle { margin = EdgeValue(bottom = UnitValue.Companion.real(10)) },
            Container(
                context = ContextData(
                    id = "arrayTest",
                    ArrayTest2(
                        array1 = arrayOf(1, 2, 3),
                        array2 = arrayOf(4, 5, 6),
                        array3 = arrayOf(7, 8, 9),
                        array4 = arrayOf(10, 11, 12),
                        array5 = arrayOf(13, 14, 15),
                        array6 = arrayOf(16, 17, 18)
                    )
                ),
                children = listOf(
                    Text("Array Test 2", textColor = "#00c91b"),
                    Text(contains(expressionOf("@{arrayTest.array1}"), constant(0)).toBindString()),
                    Text(remove(expressionOf("@{arrayTest.array2}"), constant(4)).toBindString()),
                    Text(
                        removeIndex<Number>(
                            expressionOf("@{arrayTest.array3}"),
                            constant(1)
                        ).toBindString()
                    ),
                    Text(insert(expressionOf("@{arrayTest.array4}"), constant(13)).toBindString()),
                    Text(
                        union<Number>(
                            expressionOf("@{arrayTest.array5}"),
                            expressionOf("@{arrayTest.array6}")
                        ).toBindString()
                    ),
                )
            )
        )
    ).setStyle {
        margin = EdgeValue(top = UnitValue.real(10), bottom = UnitValue.real(10))
    }
}