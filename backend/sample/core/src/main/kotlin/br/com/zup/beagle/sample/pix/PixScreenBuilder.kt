package br.com.zup.beagle.sample.pix

import br.com.zup.beagle.annotation.ContextObject
import br.com.zup.beagle.context.constant
import br.com.zup.beagle.context.operations.builtin.*
import br.com.zup.beagle.core.Display
import br.com.zup.beagle.ext.setStyle
import br.com.zup.beagle.sample.constants.BASE_URL
import br.com.zup.beagle.sample.constants.SELECT_CONTACT_ENDPOINT
import br.com.zup.beagle.sample.widget.SampleTextField
import br.com.zup.beagle.sample.widget.valueExpression
import br.com.zup.beagle.widget.action.Navigate
import br.com.zup.beagle.widget.action.Route
import br.com.zup.beagle.widget.context.Context
import br.com.zup.beagle.widget.core.EdgeValue
import br.com.zup.beagle.widget.core.Flex
import br.com.zup.beagle.widget.core.JustifyContent
import br.com.zup.beagle.widget.layout.Container
import br.com.zup.beagle.widget.layout.Screen
import br.com.zup.beagle.widget.layout.ScreenBuilder
import br.com.zup.beagle.widget.ui.Button
import br.com.zup.beagle.widget.ui.Text

@ContextObject
data class PixContext(
    override val id: String,
    val myValue: String = "",
    val person: Person = Person("")
) : Context

@ContextObject
data class Person(
    override val id: String,
    val firstName: String = "",
    val lastName: String = ""
) : Context

object PixScreenBuilder : ScreenBuilder {
    private val pixContext = PixContext("").normalize("pix")
    override fun build() = Screen(
        child = Container(
            context = pixContext,
            children = listOf(
                header(),
                center(),
                footer()
            ),
        ).setStyle {
            padding = EdgeValue.only(left = 30, right = 30)
            flex = Flex(
                justifyContent = JustifyContent.SPACE_AROUND,
                grow = 1.0
            )
        }
    )

    private fun header() = Container(
        children = listOf(
            Text(
                text = constant("Pix")
            ).setStyle {
                margin = EdgeValue.only(bottom = 30)
            },
            containerValue(),
        )
    )

    private fun containerValue() = Container(
        children = listOf(
            Text(
                text = constant("Valor")
            ).setStyle {
                margin = EdgeValue.only(bottom = 10)
            },
            SampleTextField(
                placeholder = "",
                onChange = {
                    listOf(
                        pixContext.changeMyValue(it.valueExpression)
                    )
                }
            )
        )
    )

    private fun center() = Container(
        children = listOf(
            Text(
                text = constant("Contato")
            ).setStyle {
                margin = EdgeValue.only(bottom = 10)
            },
            containerContact(),
            selectContact(),
            showName()
        )
    )

    private fun selectContact() = Button(
        text = constant("Selecionar Contato"),
        onPress = listOf(
            Navigate.PushStack(route = Route.Remote(url = BASE_URL + SELECT_CONTACT_ENDPOINT))
        )
    ).setStyle {
        margin = EdgeValue.only(bottom = 30)
        display = condition(
            isNull(constant(true)),
            constant(Display.FLEX), constant(Display.NONE)
        )
    }

    private fun containerContact() = Container(
        children = listOf(
            Text(text = concat(constant("Valor: "), pixContext.myValueExpression)).setStyle {
                margin = EdgeValue.only(bottom = 10)
            },
            Button(
                text = constant("Editar"),
                onPress = listOf(
                    Navigate.PushStack(route = Route.Remote(url = BASE_URL + SELECT_CONTACT_ENDPOINT))
                )
            )
        )
    ).setStyle {
        display = condition(
            isNull(constant(true)),
            constant(Display.FLEX), constant(Display.NONE)
        )
    }

    private fun showName() = Container(
        children = listOf(
            Text(
                text = constant("Primeiro nome")
            ).setStyle {
                margin = EdgeValue.only(bottom = 10)
            },
            SampleTextField(
                placeholder = "",
                onChange = {
                    listOf(
                        pixContext.person.changeFirstName(it.valueExpression)
                    )
                }
            ),
            Text(text = concat(constant("Primeiro nome: "), pixContext.person.firstNameExpression)).setStyle {
                margin = EdgeValue.only(bottom = 30)
            },
            Text(
                text = constant("Último nome")
            ).setStyle {
                margin = EdgeValue.only(bottom = 10)
            },
            SampleTextField(
                placeholder = "",
                onChange = {
                    listOf(
                        pixContext.person.changeLastName(it.valueExpression)
                    )
                }
            ),
            Text(text = concat(constant("Último nome: "), pixContext.person.lastNameExpression)).setStyle {
                margin = EdgeValue.only(bottom = 10)
            },
        )
    ).setStyle {
        display = condition(
            isNull(constant(true)),
            constant(Display.FLEX), constant(Display.NONE)
        )
    }

    private fun footer() = Button(
        text = constant("Trasferir"),
        onPress = listOf(
        ),
        enabled = not(isEmpty(pixContext.myValueExpression))
    )
}