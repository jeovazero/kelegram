package kelegram.client.pages

import androidx.compose.runtime.*
import kelegram.client.*
import kelegram.client.tokens.Token
import kelegram.client.ui.Button
import kelegram.client.ui.Inline
import kelegram.client.ui.Logo
import kelegram.client.ui.Stack
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Window
import org.w3c.dom.url.URL
import org.w3c.dom.url.URLSearchParams

object SignUpStylesheet : StyleSheet() {
    val wrapper by style {
        alignSelf(AlignSelf.Center)
    }
    val box by style { // container is a class
        padding(3.cssRem)
        paddingTop(0.cssRem)
        backgroundColor(Token.pallete.primaryText)
        alignSelf(AlignSelf.Center)
        display(DisplayStyle.Flex)
        textAlign("center")
        justifyContent(JustifyContent.Center)
        borderRadius(0.px,0.px,16.px,16.px)
    }
    val headerBox by style {
        padding(1.cssRem)
        property("box-shadow",Token.shadow.header)
        backgroundColor(Token.pallete.primaryText)
        textAlign("center")
        width(100.percent)
        boxSizing("border-box")
        property("z-index","1")
        borderRadius(16.px, 16.px, 0.px, 0.px)
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
    val title by style {
        fontSize(1.5.cssRem)
        color(AppCSSVariables.neutralDark.value())
    }
    val signin by style {
        alignItems(AlignItems.Center)
        paddingTop(1.5.cssRem)
        child(self, sibling(universal, universal)) style {
            marginLeft(12.px)
        }
    }
}

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

val windowFeatures = "popup"

@Composable
fun OAuth(url: String, onAuth: (String) -> Unit, content:  @Composable () -> Unit) {
     fun pop() {
        try {
            val popup = window.open(url, "oauth", windowFeatures)
            var timer = 0
            if (popup != null) {
                timer = window.setInterval({
                    val url = URL(popup.location.href)
                    val token = url.searchParams.get("id")
                    if (token != null) {
                        onAuth(token)
                        popup.close()
                        window.clearInterval(timer)
                    }
                }, 500)
            }

        } catch (e: Error) {
            console.log("Error", e.message)
        }
    }
    Div(attrs = {
        onClick { pop() }
    }) {
        content()
    }
}

@Composable
fun LoginPage(mstate: MState) {
    Style(SignUpStylesheet)
    val scope = rememberCoroutineScope()
    val requestMe = remember { mutableStateOf(false) }

    LaunchedEffect(requestMe.value) {
        scope.launch {
            dispatch(mstate, Action.DefineMe)
        }
    }

    Stack(className = SignUpStylesheet.wrapper) {
        Div (attrs =  { classes(SignUpStylesheet.headerBox) }) {
            Logo()
        }
        Div(attrs = { classes(SignUpStylesheet.box) }) {
            Inline (className = SignUpStylesheet.signin) {
                H3(attrs = {
                    classes(SignUpStylesheet.title)
                }){ Text("Sign In") }
                OAuth(url = "http://localhost:8000/login", onAuth = {
                    requestMe.value = true
                }) {
                    Button (fullWidth = true) {
                        Text("Login")
                    }
                }
            }
        }
        RegisterForm(mstate)
        Button(fullWidth = true) {
            Text("Random")
        }
    }
}