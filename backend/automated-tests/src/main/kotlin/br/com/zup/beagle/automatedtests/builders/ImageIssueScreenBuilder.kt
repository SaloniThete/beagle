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

package br.com.zup.beagle.automatedtests.builders

import br.com.zup.beagle.core.Style
import br.com.zup.beagle.ext.applyStyle
import br.com.zup.beagle.ext.unitPercent
import br.com.zup.beagle.ext.unitReal
import br.com.zup.beagle.widget.core.EdgeValue
import br.com.zup.beagle.widget.core.ImageContentMode
import br.com.zup.beagle.widget.core.ScrollAxis
import br.com.zup.beagle.widget.core.Size
import br.com.zup.beagle.widget.layout.Container
import br.com.zup.beagle.widget.layout.Screen
import br.com.zup.beagle.widget.layout.ScrollView
import br.com.zup.beagle.widget.ui.Image
import br.com.zup.beagle.widget.ui.ImagePath
import br.com.zup.beagle.widget.ui.ImagePath.Local
import br.com.zup.beagle.widget.ui.Text

val FIT_CENTER = ImageContentMode.FIT_CENTER
val CENTER_CROP = ImageContentMode.CENTER_CROP
val CENTER = ImageContentMode.CENTER
val FIT_XY = ImageContentMode.FIT_XY
val mode = null

const val TEXT_VIEW_BACKGROUND_COLOR = "#A9A9A9"
const val IMAGE_VIEW_BACKGROUND_COLOR = "#D5D5D5"
const val CONTAINER_BACKGROUND_COLOR = "#D5FFD5"
const val IMAGE_REMOTE_URL = "https://i.pinimg.com/564x/8a/2d/80/8a2d80b70dfd531befe563db81017331.jpg"

//NOTICE
//All remote images have a local image rendered just below then got from the front view to validate
// the image behaviour with styles and rule any style issue out of image issues

object ImageIssueScreenBuilder {
    fun build() = Screen(
        child = ScrollView(
            scrollDirection = ScrollAxis.VERTICAL,
            children = listOf(
                Text(text = "Image Issue Screen"),
                imagePathRemote(
                    title= "with HEIGHT and WIDTH set",
                    Size(width = 100.unitReal(),height = 100.unitReal()),
                    mode = mode
                ),
                imagePathRemote(
                    title= "with NO SIZE set",
                    mode = mode
                ),
                imagePathRemote(
                    title= "with ONLY WIDTH set and width is smaller than image size",
                    Size(width = 100.unitReal()),
                    mode = mode
                ),
                imagePathRemote(
                    title= "with ONLY WIDTH set and width is bigger than image size",
                    Size(width = 600.unitReal()),
                    mode = mode
                ),
                imagePathRemote(
                    title= "with ONLY HEIGHT set and height is smaller than image size",
                    Size(height = 150.unitReal()),
                    mode = mode
                ),

                imagePathRemote(
                    title= "with ONLY HEIGHT set and height is bigger than image size",
                    Size(height = 600.unitReal()),
                    mode = mode
                ),
                imagePathRemote(
                    title= "with WIDTH BIGGER than parent view",
                    Size(width = 700.unitReal()),
                    mode = mode
                ),
                imagePathRemote(
                    title= "with WIDTH set as UnitPercent = 30%",
                    Size(width = 30.unitPercent()),
                    mode = mode
                ),
                imagePathRemote(
                    title= "with WIDTH set as UnitPercent = 100%",
                    Size(width = 100.unitPercent()),
                    mode = mode
                )
            )
        )
    )

    private fun imagePathRemote(title: String, size:Size? = null, mode:ImageContentMode? = null) = Container(
        children = listOf(
            Text("Remote Image $title").applyStyle(Style(padding = EdgeValue(vertical = 5.unitReal()), backgroundColor = TEXT_VIEW_BACKGROUND_COLOR)),
            Image(mode = mode, path = ImagePath.Remote(IMAGE_REMOTE_URL)
            ).applyStyle(Style(
                backgroundColor = IMAGE_VIEW_BACKGROUND_COLOR,
                size = size)
            ),
            Text("Local Image $title as reference with the same styles to rule any faulty styles out"),
            Image(mode = mode, path = Local.justMobile("imageBeagle")
            ).applyStyle(Style(
                backgroundColor = IMAGE_VIEW_BACKGROUND_COLOR,
                size = size,
                margin = EdgeValue(bottom = 30.0.unitReal()))
            )
        )
    ).applyStyle(Style(backgroundColor = CONTAINER_BACKGROUND_COLOR))
}
