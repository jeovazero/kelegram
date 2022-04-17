package kelegram.client.ui

import androidx.compose.runtime.Composable
import kelegram.client.KelegramStylesheet
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Main

object CommonStylesheet : StyleSheet() {
    val wrapper by style { // container is a class
        display(DisplayStyle.Flex)
        height(100.vh)
        boxSizing("border-box")
        justifyContent(JustifyContent.Center)
        overflow("auto")
    }
}

@Composable
fun AppWrapper(content: @Composable () -> Unit) {
    Style(CommonStylesheet)
    Main(
        attrs = {
            classes(KelegramStylesheet.kTheme,KelegramStylesheet.bgPrimary,CommonStylesheet.wrapper)
        }
    ) {
        content()
    }
}