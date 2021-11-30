import androidx.compose.runtime.Composable
import kelegram.client.AppCSSVariables
import kelegram.client.MState
import kelegram.client.tokens.Token
import kelegram.client.ui.Logo
import kelegram.client.ui.Stack
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text

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
        paddingTop(3.cssRem)
        paddingBottom(3.cssRem)
        color(AppCSSVariables.neutralDark.value())
    }
}

@Composable
fun RegisterForm(mstate: MState) {
    Div {
        Stack {
            Input(type = InputType.Text,attrs = {
                placeholder("Your name...")
                classes(SignUpStylesheet.input)
                name("nickname")
            })
        }
        Button(
            onClick = {
                mainScope.launch {
                    createAccount()
                    setMe(mstate)
                }
            }
        ) {
            Text("Sign Up")
        }
    }
}

@Composable
fun Signup(state: MState) {
    Style(SignUpStylesheet)
    Stack(className = SignUpStylesheet.wrapper) {
        Div (attrs =  { classes(SignUpStylesheet.headerBox) }) {
            Logo()
        }
        Div(attrs = { classes(SignUpStylesheet.box) }) {
            Stack {
                H3(attrs = {
                    classes(SignUpStylesheet.title)
                }){ Text("Create Account") }
                RegisterForm(state)
            }
        }
    }
}