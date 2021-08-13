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
import br.com.zup.beagle.context.constant
import br.com.zup.beagle.context.expressionOf

/** Number **/
fun sum(vararg params: Bind<Number>): Bind.Expression<Number> = createOperation("sum", *params)
fun subtract(vararg params: Bind<Number>): Bind.Expression<Number> = createOperation("subtract", *params)
fun divide(vararg params: Bind<Number>): Bind.Expression<Number> = createOperation("divide", *params)
fun multiply(vararg params: Bind<Number>): Bind.Expression<Number> = createOperation("multiply", *params)

/** String **/
fun capitalize(param: Bind<String>): Bind.Expression<String> = createOperation("capitalize", param)

@JvmName("BindCapitalize")
fun Bind<String>.capitalize(): Bind.Expression<String> = createOperation("capitalize", this)

fun concat(vararg params: Bind<String>): Bind.Expression<String> = createOperation("concat", *params)

fun lowercase(param: Bind<String>): Bind.Expression<String> = createOperation("lowercase", param)
fun Bind<String>.toLowerCase(): Bind.Expression<String> = createOperation("lowercase", this)

fun uppercase(param: Bind<String>): Bind.Expression<String> = createOperation("uppercase", param)
fun Bind<String>.toUpperCase(): Bind.Expression<String> = createOperation("uppercase", this)

fun substring(param: Bind<String>, startIndex: Bind<Number>): Bind.Expression<String> =
    createExpression("substr", listOf(resolveInput(param), resolveInput(startIndex)))

/** comparison **/
fun eq(vararg params: Bind<Number>): Bind.Expression<Boolean> = createOperation("eq", *params)
fun gt(vararg params: Bind<Number>): Bind.Expression<Boolean> = createOperation("gt", *params)
fun gte(vararg params: Bind<Number>): Bind.Expression<Boolean> = createOperation("gte", *params)
fun lt(vararg params: Bind<Number>): Bind.Expression<Boolean> = createOperation("lt", *params)
fun lte(vararg params: Bind<Number>): Bind.Expression<Boolean> = createOperation("lte", *params)

/** logic **/
fun and(vararg params: Bind<Boolean>): Bind.Expression<Boolean> = createOperation("and", *params)
fun condition(vararg params: Bind<Boolean>): Bind.Expression<Boolean> = createOperation("condition", *params)
fun not(vararg params: Bind<Boolean>): Bind.Expression<Boolean> = createOperation("not", *params)
fun or(vararg params: Bind<Boolean>): Bind.Expression<Boolean> = createOperation("or", *params)

/** Array **/
fun <I> contains(vararg params: Bind<I>): Bind.Expression<Boolean> = createOperation("contains", *params)
fun <I> insert(vararg params: Bind<I>): Bind.Expression<Array<I>> = createOperation("insert", *params)
fun <I> remove(vararg params: Bind<I>): Bind.Expression<Array<I>> = createOperation("remove", *params)
fun <I> removeIndex(vararg params: Bind<I>): Bind.Expression<Array<I>> = createOperation("removeIndex", *params)
fun union(vararg params: Bind.Expression<Array<*>>): Bind.Expression<Array<*>> = createOperation("union", *params)

/** other **/
fun isEmpty(vararg params: Bind<Array<*>>): Bind.Expression<Boolean> = createOperation("isEmpty", *params)
fun isNull(vararg params: Bind<Array<*>>): Bind.Expression<Boolean> = createOperation("isNull", *params)
fun length(vararg params: Bind<Array<*>>): Bind.Expression<Number> = createOperation("length", *params)

private fun <I, O> createOperation(operationType: String, vararg params: Bind<I>): Bind.Expression<O> {
    val values = params.map {
        resolveInput(it)
    }
    return createExpression(operationType, values)
}

private fun <O> createExpression(operationType: String, values: List<Any?>): Bind.Expression<O> {
    return expressionOf("@{${operationType}(${values.joinToString(",")})}")
}

private fun <I> resolveInput(param: Bind<I>): Any? {
    return if (param is Bind.Expression && param.value.isNotEmpty()) {
        param.value.drop(2).dropLast(1)
    } else {
        val resultValue = (param as Bind.Value).value
        if (resultValue is String) {
            "'${resultValue}'"
        } else {
            resultValue
        }
    }
}

fun <T> Bind.Expression<T>.toBindString(): Bind<String> = expressionOf(this.value)