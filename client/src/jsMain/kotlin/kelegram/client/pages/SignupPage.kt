package kelegram.client.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kelegram.client.Action
import kelegram.client.MState
import kelegram.client.dispatch
import kelegram.client.ui.Button
import kelegram.client.ui.Stack
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLInputElement

@Composable
fun RegisterForm(mstate: MState) {
    val scope = rememberCoroutineScope()
    Div {
        Stack {
            Input(type = InputType.Text,attrs = {
                placeholder("Your name...")
                classes(SignUpStylesheet.input)
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
    Stack{
        RegisterForm(mstate)
        Button(fullWidth = true) {
            Text("Random")
        }
    }
}
