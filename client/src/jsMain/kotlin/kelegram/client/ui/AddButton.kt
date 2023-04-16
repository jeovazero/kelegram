package kelegram.client.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

object AddButtonStyle : StyleSheet() {
    val wrapper by style {
        cursor("pointer")
        borderRadius(50.percent)
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.Center)
        alignItems(AlignItems.Center)
        backgroundColor(AppCSSVariables.neutralMedium.value())
        width(20.px)
        height(20.px)
    }
    val barVertical by style {
        position(Position.Absolute)
        width(2.px)
        backgroundColor(AppCSSVariables.neutralLighten.value())
        height(10.px)
    }
    val barHorizontal by style {
        position(Position.Absolute)
        backgroundColor(AppCSSVariables.neutralLighten.value())
        width(10.px)
        height(2.px)
    }
}

@Composable
fun AddButton(onClick: (() -> Unit)? = null) {
    Style(AddButtonStyle)
    Div(attrs = {
        classes(AddButtonStyle.wrapper)
        this.onClick {
            onClick?.invoke()
        }
    }) {
        Div(attrs = { classes(AddButtonStyle.barVertical) })
        Div(attrs = { classes(AddButtonStyle.barHorizontal) })
    }
}