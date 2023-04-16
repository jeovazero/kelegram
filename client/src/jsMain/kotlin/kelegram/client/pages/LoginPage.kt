package kelegram.client.pages

import androidx.compose.runtime.*
import kelegram.client.service.KelegramServer
import kelegram.client.state.Action
import kelegram.client.state.MState
import kelegram.client.state.dispatch
import kelegram.client.tokens.Token
import kelegram.client.ui.*
import kotlinx.browser.window
import kotlinx.datetime.Clock
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.url.URL

object LoginStylesheet : StyleSheet() {
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

val windowFeatures = "popup"

@Composable
fun OAuth(url: String, onAuth: () -> Unit, content:  @Composable () -> Unit) {
     fun pop() {
        try {
            val popup = window.open(url, "oauth", windowFeatures)
            var timer = 0
            if (popup != null) {
                timer = window.setInterval({
                    val localurl = URL(popup.location.href)
                    val path = localurl.pathname
                    if (path == "/app") {
                        onAuth()
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

data class LoginRequests(val defineMe: Boolean, val createAccount: String?)

@Composable
fun LoginPage(mstate: MState) {
    Style(LoginStylesheet)
    val (requests, setRequests) = remember { mutableStateOf(LoginRequests(false, null)) }

    LaunchedEffect(requests) {
        val state = requests
        if (state.defineMe) {
            dispatch(mstate, Action.DefineMe)
            setRequests(requests.copy(defineMe = false))
        }
        if (state.createAccount != null) {
            dispatch(mstate, Action.CreateAccount(state.createAccount))
            setRequests(requests.copy(createAccount = null))
        }
    }
    Stack(className = LoginStylesheet.wrapper) {
        Div (attrs =  { classes(LoginStylesheet.headerBox) }) {
            Logo()
        }
        Div(attrs = { classes(LoginStylesheet.box) }) {
            Stack (Spacing.Medium) {
                Inline(className = LoginStylesheet.signin) {
                    H3(attrs = {
                        classes(LoginStylesheet.title)
                    }) { Text("Sign In") }
                    OAuth(url = "${KelegramServer.BASE}/login", onAuth = {
                        setRequests(requests.copy(defineMe = true))
                    }) {
                        Button(fullWidth = true) {
                            Text("Login")
                        }
                    }
                }
                if (mstate.value.env == "development") {
                    Button(fullWidth = true, onClick = {
                        val tmp = Clock.System.now().toEpochMilliseconds()
                        val nickname = "guest-${tmp}"
                        setRequests(requests.copy(createAccount = nickname))
                    }) {
                        Text("Guest Account")
                    }
                }
            }
        }
    }
}