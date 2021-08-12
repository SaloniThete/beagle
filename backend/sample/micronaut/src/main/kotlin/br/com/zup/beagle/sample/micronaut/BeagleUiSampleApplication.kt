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

package br.com.zup.beagle.sample.micronaut

import br.com.zup.beagle.sample.Model
import br.com.zup.beagle.sample.Model2
import br.com.zup.beagle.sample.Model3
import br.com.zup.beagle.sample.childListGetElementAt
import br.com.zup.beagle.sample.counterExpression
import br.com.zup.beagle.sample.namesExpression
import br.com.zup.beagle.sample.normalize
import io.micronaut.runtime.Micronaut

object BeagleUiSampleApplication {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(BeagleUiSampleApplication::class.java)
        var a = Model(
            contextId = "",
            counter = listOf(12),
            post = "as",
            child = Model2(
                contextId = "",
                title = "title",
                child = Model3("",names = listOf("name"))
            ),
            child2 = Model3("",names = listOf("name")),
            childList = listOf(Model3("",names = listOf("name"))),
            childList2 = listOf()
        )
        a = a.normalize("contextId")

//        println(a.expression)
//        println(a.counterExpression)
//        println(a.postExpression)
//        println(a.childExpression)
//        println(a.child.expression)
//        println(a.child.child.namesExpression)
        println(a.counterExpression)
        println(a.childListGetElementAt(50).namesExpression)
    }
}