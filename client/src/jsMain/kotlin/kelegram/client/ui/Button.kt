package kelegram.client.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button as ButtonDom

// TODO: create many variants (primary, secondary, neutral)
object ButtonStyle : StyleSheet() {
    val button by style {
        cursor("pointer")
        padding(0.75.cssRem)
        paddingLeft(1.cssRem)
        paddingRight(1.cssRem)
        boxSizing("border-box")
        fontWeight("bolder")
        fontSize(1.5.cssRem)
        border {
            width(0.px)
            style(LineStyle.None)
            color(Color.currentColor)
        }
    }
    val fullWidth by style {
        width(100.percent)
    }
}

enum class Variant(val style: String) {
    Primary(KelegramStylesheet.bgPrimary),
    Secondary(KelegramStylesheet.bgSecondary),
    Neutral(KelegramStylesheet.bgNeutral),
}

@Composable
fun Button(
    onClick: (() -> Unit)? = null,
    fullWidth: Boolean = false,
    variant: Variant = Variant.Primary,
    borderRadius: BorderRadiusValue = BorderRadiusValue.Large,
    content: @Composable () -> Unit
) {
    val click = onClick
    ButtonDom(attrs = {
        classes(
            KelegramStylesheet.primaryText,
            borderRadius.style,
            ButtonStyle.button,
            variant.style
        )
        if (click != null) {
            onClick { click() }
        }
        if (fullWidth) {
            classes(ButtonStyle.fullWidth)
        }
    }) {
        content()
    }
}