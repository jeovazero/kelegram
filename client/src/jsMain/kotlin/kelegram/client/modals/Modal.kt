package kelegram.client.modals

import androidx.compose.runtime.Composable
import kelegram.client.AppCSSVariables
import kelegram.client.KelegramStylesheet
import kelegram.client.tokens.Token
import kelegram.client.ui.Button
import kelegram.client.ui.Spacing
import kelegram.client.ui.Stack
import kelegram.client.ui.Variant
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Text

object ModalStylesheet : StyleSheet() {
    val wrapper by style {
        position(Position.Absolute)
        height(100.vh)
        width(100.vw)
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.Center)
        alignItems(AlignItems.Center)
        property("z-index", "2")
    }
    val content by style { // container is a class
        padding(2.cssRem)
        backgroundColor(Token.pallete.neutralLighten)
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        borderRadius(16.px)
    }
    val contentWrapper by style {
        padding(0.5.cssRem, 0.px)
        fontSize(1.2.cssRem)
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
        paddingBottom(1.cssRem)
        color(AppCSSVariables.neutralDark.value())
    }
}

data class ModalAction(
    val labelText: String,
    val variant: Variant = Variant.Primary,
    val onClick: (() -> Unit)? = null,
)

@Composable
fun Modal(
    actions: List<ModalAction> = listOf(),
    content: @Composable () -> Unit,
    title: String,
    onClose: (() -> Unit)? = null,
) {
    Style(ModalStylesheet)
    Div(attrs = {
        classes(ModalStylesheet.wrapper, KelegramStylesheet.bgPrimaryOpacity)
        onClick {
            console.info("Close modal $title")
            onClose?.invoke()
        }
    }) {
        Div(attrs = {
            classes(ModalStylesheet.content)
            onClick {
                it.stopPropagation()
            }
        }) {
            H3(attrs = { classes(ModalStylesheet.title) }) { Text(title) }
            Div(attrs = {
                classes(ModalStylesheet.contentWrapper)
            }) {
                content()
            }
            Stack(spacing = Spacing.Small) {
                actions.forEach { action ->
                    Button(fullWidth = false,
                        onClick = action.onClick,
                        variant = action.variant) {
                        Text(action.labelText)
                    }
                }
            }
        }
    }
}