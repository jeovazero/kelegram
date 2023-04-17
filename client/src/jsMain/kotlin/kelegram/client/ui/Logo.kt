package kelegram.client.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Img

@Composable
fun Logo() {
    Img(src="/assets/logo.png",
        attrs = {
            style {
                width(auto)
                height(56.px)
            }
    })
}