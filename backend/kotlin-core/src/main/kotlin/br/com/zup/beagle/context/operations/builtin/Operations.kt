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

@JvmName("BindCapitalize")
fun Bind<String>.capitalize(): Bind.Expression<String> = createOperation("capitalize", this)

fun concat(vararg inputs: Bind<String>): Bind.Expression<String> = createOperation("concat", *inputs)

fun lowercase(input: Bind<String>): Bind.Expression<String> = createOperation("lowercase", input)
fun Bind<String>.toLowerCase(): Bind.Expression<String> = createOperation("lowercase", this)

fun uppercase(input: Bind<String>): Bind.Expression<String> = createOperation("uppercase", input)
fun Bind<String>.toUpperCase(): Bind.Expression<String> = createOperation("uppercase", this)

fun substring(input: Bind<String>, startIndex: Bind<Number>): Bind.Expression<String> =
    createExpression("substr", listOf(resolveInput(input), resolveInput(startIndex)))

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
fun <I> insert(array: Bind<Array<I>>, element: Bind<I>, index: Bind<Number>? = null): Bind.Expression<Array<I>> =
    createExpression("insert", listOf(resolveInput(array), resolveInput(element),
        resolveInput(index)))

fun <I> remove(array: Bind<Array<I>>, element: Bind<I>): Bind.Expression<Array<I>> =
    createExpression("remove", listOf(resolveInput(array), resolveInput(element)))

fun <I> removeIndex(array: Bind<Array<I>>, index: Bind<Number>): Bind.Expression<Array<I>> =
    createExpression("removeIndex", listOf(resolveInput(array), resolveInput(index)))

fun <I> union(firstArray: Bind<Array<I>>, secondArray: Bind<Array<I>>): Bind.Expression<Array<*>> =
    createExpression("union", listOf(resolveInput(firstArray), resolveInput(secondArray)))

/** other **/
fun isEmpty(vararg inputs: Bind<Array<*>>): Bind.Expression<Boolean> = createOperation("isEmpty", *inputs)
fun isNull(vararg inputs: Bind<Array<*>>): Bind.Expression<Boolean> = createOperation("isNull", *inputs)
fun length(vararg inputs: Bind<Array<*>>): Bind.Expression<Number> = createOperation("length", *inputs)

private fun <I, O> createOperation(operationType: String, vararg inputs: Bind<I>): Bind.Expression<O> {
    val values = inputs.map {
        resolveInput(it)
    }
    return createExpression(operationType, values)
}

private fun <I> resolveInput(input: Bind<I>?): Any? {
    return input?.let {
        if (input is Bind.Expression && input.value.isNotEmpty()) {
            input.value.drop(2).dropLast(1)
        } else {
            val resultValue = (input as Bind.Value).value
            if (resultValue is String) {
                "'${resultValue}'"
            } else {
                resultValue
            }
        }
    } ?: run {
        input
    }

}

private fun <O> createExpression(operationType: String, values: List<Any?>): Bind.Expression<O> {
    return expressionOf("@{${operationType}(${values.filterNotNull().joinToString(",")})}")
}

fun <T> Bind.Expression<T>.toBindString(): Bind<String> = expressionOf(this.value)