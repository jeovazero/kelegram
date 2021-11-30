import androidx.compose.runtime.Composable
import kelegram.client.KelegramStylesheet
import kelegram.client.ui.BorderRadiusValue
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button as ButtonDom

object ButtonStyle : StyleSheet() {
    val button by style {
        cursor("pointer")
        padding(0.75.cssRem)
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

@Composable
fun Button(
    onClick: (() -> Unit)? = null,
    fullWidth: Boolean = true,
    content: @Composable () -> Unit
) {
    val click = onClick
    Style(ButtonStyle)
    ButtonDom(attrs = {
        classes(
            KelegramStylesheet.bgPrimary,
            KelegramStylesheet.primaryText,
            BorderRadiusValue.Large.style,
            ButtonStyle.button
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