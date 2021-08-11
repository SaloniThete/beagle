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

package br.com.zup.beagle.context.operations.builtin

import br.com.zup.beagle.context.Bind
import br.com.zup.beagle.context.expressionOf

/** Number **/
fun sum(vararg inputs: Bind<Number>): Bind.Expression<Number> = createOperation("sum", *inputs)
fun subtract(vararg inputs: Bind<Number>): Bind.Expression<Number> = createOperation("subtract", *inputs)
fun divide(vararg inputs: Bind<Number>): Bind.Expression<Number> = createOperation("divide", *inputs)
fun multiply(vararg inputs: Bind<Number>): Bind.Expression<Number> = createOperation("multiply", *inputs)

/** String **/
fun capitalize(input: Bind<String>): Bind.Expression<String> = createOperation("capitalize", input)
fun concat(vararg inputs: Bind<String>): Bind.Expression<String> = createOperation("concat", *inputs)

fun lowercase(vararg inputs: Bind<String>): Bind.Expression<String> = createOperation("lowercase", *inputs)
fun Bind<String>.toLowerCase(): Bind.Expression<String> = createOperation("lowercase", this)

fun uppercase(vararg inputs: Bind<String>): Bind.Expression<String> = createOperation("uppercase", *inputs)
fun <I> substring(vararg inputs: Bind<I>): Bind.Expression<String> = createOperation("substr", *inputs)

/** comparison **/
fun eq(vararg inputs: Bind<Number>): Bind.Expression<Boolean> = createOperation("eq", *inputs)
fun gt(vararg inputs: Bind<Number>): Bind.Expression<Boolean> = createOperation("gt", *inputs)
fun gte(vararg inputs: Bind<Number>): Bind.Expression<Boolean> = createOperation("gte", *inputs)
fun lt(vararg inputs: Bind<Number>): Bind.Expression<Boolean> = createOperation("lt", *inputs)
fun lte(vararg inputs: Bind<Number>): Bind.Expression<Boolean> = createOperation("lte", *inputs)

/** logic **/
fun and(vararg inputs: Bind<Boolean>): Bind.Expression<Boolean> = createOperation("and", *inputs)
fun condition(vararg inputs: Bind<Boolean>): Bind.Expression<Boolean> = createOperation("condition", *inputs)
fun not(vararg inputs: Bind<Boolean>): Bind.Expression<Boolean> = createOperation("not", *inputs)
fun or(vararg inputs: Bind<Boolean>): Bind.Expression<Boolean> = createOperation("or", *inputs)

/** Array **/
fun <I> contains(vararg inputs: Bind<I>): Bind.Expression<Boolean> = createOperation("contains", *inputs)
fun <I> insert(vararg inputs: Bind<I>): Bind.Expression<Array<I>> = createOperation("insert", *inputs)
fun <I> remove(vararg inputs: Bind<I>): Bind.Expression<Array<I>> = createOperation("remove", *inputs)
fun <I> removeIndex(vararg inputs: Bind<I>): Bind.Expression<Array<I>> = createOperation("removeIndex", *inputs)
fun union(vararg inputs: Bind.Expression<Array<*>>): Bind.Expression<Array<*>> = createOperation("union", *inputs)

/** other **/
fun isEmpty(vararg inputs: Bind<Array<*>>): Bind.Expression<Boolean> = createOperation("isEmpty", *inputs)
fun isNull(vararg inputs: Bind<Array<*>>): Bind.Expression<Boolean> = createOperation("isNull", *inputs)
fun length(vararg inputs: Bind<Array<*>>): Bind.Expression<Number> = createOperation("length", *inputs)

private fun <I, O> createOperation(operationType: String, vararg inputs: Bind<I>): Bind.Expression<O> {
    val values = inputs.map {
        if (it is Bind.Expression && it.value.isNotEmpty()) {
            it.value.drop(2).dropLast(1)
        } else {
            val resultValue = (it as Bind.Value).value
            if (resultValue is String) {
                "'${resultValue}'"
            } else {
                resultValue
            }
        }
    }
    return expressionOf("@{${operationType}(${values.joinToString(",")})}")
}

fun <T> Bind.Expression<T>.convertToString(): Bind<String> = expressionOf(this.value)