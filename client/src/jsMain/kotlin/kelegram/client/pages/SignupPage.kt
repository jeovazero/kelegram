package kelegram.client.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kelegram.client.state.Action
import kelegram.client.state.MState
import kelegram.client.state.dispatch
import kelegram.client.ui.AppCSSVariables
import kelegram.client.ui.Button
import kelegram.client.ui.Stack
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLInputElement

object SignupStylesheet: StyleSheet() {
    val wrapper by style {
        padding(1.cssRem)
        backgroundColor(AppCSSVariables.neutralLighten.value())
        borderRadius(8.px)
    }
    val input by style {
        width(342.px)
        padding(1.cssRem)
        fontSize(1.125.cssRem)
        borderRadius(12.px)
        backgroundColor(AppCSSVariables.neutralLight.value())
        border {
            width(0.px)
            style(LineStyle.None)
        }
        marginBottom(1.cssRem)
    }
}

@Composable
fun RegisterForm(mstate: MState) {
    val scope = rememberCoroutineScope()
    Div(attrs = {
        classes(SignupStylesheet.wrapper)
    }) {
        Stack {
            Input(type = InputType.Text,attrs = {
                placeholder("Your name...")
                classes(SignupStylesheet.input)
                name("nickname")
            })
        }
        Button(
            fullWidth = true,
            onClick = {
                val nickname =
                    document.querySelector("input[name=nickname]") as HTMLInputElement
                scope.launch {
                    dispatch(mstate, Action.CreateAccount(nickname.value))
                }
            }
        ) {
            Text("Sign Up")
        }
    }
}


@Composable
fun SignupPage(mstate: MState) {
    Style(SignupStylesheet)
    Stack {
        RegisterForm(mstate)
    }
}
