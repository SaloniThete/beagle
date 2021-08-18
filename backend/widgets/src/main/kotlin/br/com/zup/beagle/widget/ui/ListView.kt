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

package br.com.zup.beagle.widget.ui

import br.com.zup.beagle.context.Bind
import br.com.zup.beagle.core.ServerDrivenComponent
import br.com.zup.beagle.widget.Widget
import br.com.zup.beagle.widget.action.Action
import br.com.zup.beagle.widget.context.ContextComponent
import br.com.zup.beagle.widget.context.Context
import br.com.zup.beagle.widget.core.ListDirection
import br.com.zup.beagle.widget.utils.BeagleConstants.DEPRECATED_LIST_VIEW

/**
 * ListView is a Layout component that will define a list of views natively.
 * These views could be any Server Driven Component.
 * @see ContextComponent
 */
data class ListView(
    @Deprecated(message = DEPRECATED_LIST_VIEW,
        replaceWith = ReplaceWith("Use dataSource and template instead children.")
    )
    val children: List<ServerDrivenComponent>? = null,
    val direction: ListDirection = ListDirection.VERTICAL,
    override val context: Context? = null,
    val onInit: List<Action>? = null,
    val dataSource: Bind<List<Any>>? = null,
    val template: ServerDrivenComponent? = null,
    val onScrollEnd: List<Action>? = null,
    val scrollEndThreshold: Int? = null,
    val iteratorName: String = "item",
    val isScrollIndicatorVisible: Boolean = false,
    val key: String? = null,
    val useParentScroll: Boolean? = null,
    val templates: List<Template>? = null,
) : Widget(), ContextComponent {

    /**
     * @param children define the items on the list view.
     * @param direction define the list direction.
     */
    @Deprecated(
        message = DEPRECATED_LIST_VIEW,
        replaceWith = ReplaceWith(
            "ListView(direction, context, onInit, dataSource, template, onScrollEnd, scrollThreshold," +
                "iteratorName, key)")
    )
    constructor(
        children: List<ServerDrivenComponent>? = null,
        direction: ListDirection
    ) : this(
        children = children,
        direction = direction,
        context = null
    )

    /**
     * @param direction define the list direction.
     * @param context define the context that be set to component.
     * @param onInit allows to define a list of actions to be performed when the Widget is displayed.
     * @param dataSource it's an expression that points to a list of values used to populate the Widget.
     * @param template represents each cell in the list through a ServerDrivenComponent.
     * @param onScrollEnd list of actions performed when the list is scrolled to the end.
     * @param scrollEndThreshold sets the scrolled percentage of the list to trigger onScrollEnd.
     * @param isScrollIndicatorVisible this attribute enables or disables the scroll bar.
     * @param iteratorName is the context identifier of each cell.
     * @param key points to a unique value present in each dataSource item
     * used as a suffix in the component ids within the Widget.
     */
    @Deprecated(message = "It was deprecated in version 1.7 and will be removed in a future version. " +
        "Use templates instead template",
        replaceWith = ReplaceWith(
            "ListView(direction, context, onInit, dataSource, onScrollEnd, scrollEndThreshold," +
                "iteratorName, key, useParentScroll, templates)"))
    constructor(
        direction: ListDirection,
        context: Context? = null,
        onInit: List<Action>? = null,
        dataSource: Bind<List<Any>>,
        template: ServerDrivenComponent,
        onScrollEnd: List<Action>? = null,
        scrollEndThreshold: Int? = null,
        isScrollIndicatorVisible: Boolean = false,
        iteratorName: String = "item",
        key: String? = null,
        useParentScroll: Boolean? = null
    ) : this(
        null,
        direction,
        context,
        onInit,
        dataSource,
        template,
        onScrollEnd,
        scrollEndThreshold,
        iteratorName,
        isScrollIndicatorVisible,
        key,
        useParentScroll
    )

    /**
     * @param direction define the list direction.
     * @param context define the context that be set to component.
     * @param onInit allows to define a list of actions to be performed when the Widget is displayed.
     * @param dataSource it's an expression that points to a list of values used to populate the Widget.
     * @param onScrollEnd list of actions performed when the list is scrolled to the end.
     * @param scrollEndThreshold sets the scrolled percentage of the list to trigger onScrollEnd.
     * @param isScrollIndicatorVisible this attribute enables or disables the scroll bar.
     * @param iteratorName is the context identifier of each cell.
     * @param key points to a unique value present in each dataSource item
     * used as a suffix in the component ids within the Widget.
     * @param templates Multiple templates support. The template to use will be decided according to the property `case`
     * of the template. The first template where `case` is `true` is the template chosen to render an item. If for every
     * template `case` is `false`, then, the first template where `case` is omitted (default template) is used.
     */
    constructor(
        direction: ListDirection,
        context: Context? = null,
        onInit: List<Action>? = null,
        dataSource: Bind<List<Any>>,
        onScrollEnd: List<Action>? = null,
        scrollEndThreshold: Int? = null,
        isScrollIndicatorVisible: Boolean = false,
        iteratorName: String = "item",
        key: String? = null,
        useParentScroll: Boolean? = null,
        templates: List<Template>,
    ) : this(
        null,
        direction,
        context,
        onInit,
        dataSource,
        null,
        onScrollEnd,
        scrollEndThreshold,
        iteratorName,
        isScrollIndicatorVisible,
        key,
        useParentScroll,
        templates,
    )

    companion object
}
