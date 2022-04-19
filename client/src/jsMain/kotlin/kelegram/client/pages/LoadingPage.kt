package kelegram.client.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kelegram.client.Action
import kelegram.client.AppCSSVariables
import kelegram.client.MState
import kelegram.client.dispatch
import kelegram.client.tokens.Token
import kelegram.client.ui.Logo
import kelegram.client.ui.Stack
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Text

object LoadingStylesheet : StyleSheet() {
    val wrapper by style {
        alignSelf(AlignSelf.Center)
        borderRadius(16.px)
        padding(2.5.cssRem)
        backgroundColor(Token.pallete.neutralLighten)
        textAlign("center")
    }
    val title by style {
        fontSize(1.5.cssRem)
        paddingTop(0.5.cssRem)
        color(AppCSSVariables.neutralDark.value())
    }
}

@Composable
fun LoadingPage() {
    Style(LoadingStylesheet)
    Div(attrs = { classes(LoadingStylesheet.wrapper) }) {
        Stack {
            Logo()
            H3(attrs = {
                classes(LoadingStylesheet.title)
            }) { Text("Loading...") }
        }
    }
}