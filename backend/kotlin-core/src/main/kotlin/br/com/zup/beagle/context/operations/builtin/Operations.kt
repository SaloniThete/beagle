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
fun sum(vararg params: Bind<Number>): Bind.Expression<Number> = createOperation("sum", params)
fun subtract(vararg params: Bind<Number>): Bind.Expression<Number> = createOperation("subtract", params)
fun divide(vararg params: Bind<Number>): Bind.Expression<Number> = createOperation("divide", params)
fun multiply(vararg params: Bind<Number>): Bind.Expression<Number> = createOperation("multiply", params)

/** String **/
fun capitalize(param: Bind<String>): Bind.Expression<String> = createOperation("capitalize", arrayOf(param))

@JvmName("BindCapitalize")
fun Bind<String>.capitalize(): Bind.Expression<String> = createOperation("capitalize", arrayOf(this))

fun concat(vararg params: Bind<String>): Bind.Expression<String> = createOperation("concat", params)

fun lowercase(param: Bind<String>): Bind.Expression<String> = createOperation("lowercase", arrayOf(param))
fun Bind<String>.toLowerCase(): Bind.Expression<String> = createOperation("lowercase", arrayOf(this))

fun uppercase(param: Bind<String>): Bind.Expression<String> = createOperation("uppercase", arrayOf(param))
fun Bind<String>.toUpperCase(): Bind.Expression<String> = createOperation("uppercase", arrayOf(this))

fun substring(param: Bind<String>, startIndex: Bind<Number>): Bind.Expression<String> =
    createOperation("substr", arrayOf(param, startIndex))

/** comparison **/
fun eq(vararg params: Bind<Number>): Bind.Expression<Boolean> = createOperation("eq", params)
fun gt(vararg params: Bind<Number>): Bind.Expression<Boolean> = createOperation("gt", params)
fun gte(vararg params: Bind<Number>): Bind.Expression<Boolean> = createOperation("gte", params)
fun lt(vararg params: Bind<Number>): Bind.Expression<Boolean> = createOperation("lt", params)
fun lte(vararg params: Bind<Number>): Bind.Expression<Boolean> = createOperation("lte", params)

/** logic **/
fun and(vararg params: Bind<Boolean>): Bind.Expression<Boolean> = createOperation("and", params)
fun condition(vararg params: Bind<Boolean>): Bind.Expression<Boolean> = createOperation("condition", params)
fun not(vararg params: Bind<Boolean>): Bind.Expression<Boolean> = createOperation("not", params)
fun or(vararg params: Bind<Boolean>): Bind.Expression<Boolean> = createOperation("or", params)

/** Array **/
fun <I> contains(vararg params: Bind<I>): Bind.Expression<Boolean> = createOperation("contains", params)

fun <I> insert(array: Bind<Array<I>>, element: Bind<I>, index: Bind<Number>? = null): Bind.Expression<Array<I>> =
    createOperation("insert", arrayOf(array, element, index))

fun <I> remove(array: Bind<Array<I>>, element: Bind<I>): Bind.Expression<Array<I>> =
    createOperation("remove", arrayOf(array, element))

fun <I> removeIndex(array: Bind<Array<I>>, index: Bind<Number>): Bind.Expression<Array<I>> =
    createOperation("removeIndex", arrayOf(array, index))

fun <I> union(firstArray: Bind<Array<I>>, secondArray: Bind<Array<I>>): Bind.Expression<Array<I>> =
    createOperation("union", arrayOf(firstArray, secondArray))

/** other **/
fun isEmpty(vararg params: Bind<Array<*>>): Bind.Expression<Boolean> = createOperation("isEmpty", params)
fun isNull(vararg params: Bind<Array<*>>): Bind.Expression<Boolean> = createOperation("isNull", params)
fun length(vararg params: Bind<Array<*>>): Bind.Expression<Number> = createOperation("length", params)

private fun <O> createOperation(operationType: String, params: Array<out Any?>): Bind.Expression<O> {
    val values = params.filterNotNull().map {
        resolveParam(it as Bind<*>)
    }
    return expressionOf("@{${operationType}(${values.joinToString(",")})}")
}

private fun <I> resolveParam(param: Bind<I>?): Any? {
    return param?.let {
        if (param is Bind.Expression && param.value.isNotEmpty()) {
            param.value.drop(2).dropLast(1)
        } else {
            val resultValue = (param as Bind.Value).value
            if (resultValue is String) {
                "'${resultValue}'"
            } else {
                resultValue
            }
        }
    } ?: run {
        param
    }

}

fun <T> Bind.Expression<T>.toBindString(): Bind<String> = expressionOf(this.value)