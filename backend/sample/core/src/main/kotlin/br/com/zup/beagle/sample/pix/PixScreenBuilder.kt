package br.com.zup.beagle.sample.pix

import br.com.zup.beagle.annotation.ContextObject
import br.com.zup.beagle.annotation.GlobalContext
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
import br.com.zup.beagle.widget.layout.ScrollView
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

@GlobalContext
data class GlobalContext(
    val street: String = "",
    val houseNumber: String = ""
)

object PixScreenBuilder : ScreenBuilder {
    private val pixContext = PixContext("").normalize("pix")
    private val globalContext = GlobalContext("").normalize()
    override fun build() = Screen(
        child = ScrollView(
            children = listOf(
                Container(
                    onInit = listOf(
                        globalContext.change(GlobalContext("Rua A", "5"))
                    ),
                    context = pixContext,
                    children = listOf(
                        header(),
                        center(),
                        footer()
                    )
                ).setStyle {
                    padding = EdgeValue.only(left = 20, right = 20)
                    flex = Flex(
                        justifyContent = JustifyContent.SPACE_AROUND,
                        grow = 1.0
                    )
                }
            )
        )
    )

    private fun header() = Container(
        children = listOf(
            Text(
                text = constant("Pix"), textColor = constant("#00c91b")
            ).setStyle {
                margin = EdgeValue.only(top = 10, bottom = 10)
            },
            containerValue(),
        )
    )

    private fun center() = Container(
        children = listOf(
            containerContact(),
            selectContact(),
            showName(),
            showAddress()
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

    private fun selectContact() = Button(
        text = constant("Selecionar Contato"),
        onPress = listOf(
            Navigate.PushStack(route = Route.Remote(url = BASE_URL + SELECT_CONTACT_ENDPOINT))
        )
    ).setStyle {
        margin = EdgeValue.only(bottom = 20)
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
                text = constant("Nome"), textColor = constant("#00c91b")
            ).setStyle {
                margin = EdgeValue.only(bottom = 20, top = 10)
            },
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
                margin = EdgeValue.only(bottom = 20)
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

    private fun showAddress() = Container(
        children = listOf(
            Text(
                text = constant("Endereço"), textColor = constant("#00c91b")
            ).setStyle {
                margin = EdgeValue.only(bottom = 20, top = 20)
            },
            Text(text = concat(constant("Rua: "), globalContext.streetExpression)).setStyle {
                margin = EdgeValue.only(bottom = 20)
            },
            Text(
                text = concat(constant("Número da casa: "), globalContext.houseNumberExpression)
            ).setStyle {
                margin = EdgeValue.only(bottom = 20)
            }
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