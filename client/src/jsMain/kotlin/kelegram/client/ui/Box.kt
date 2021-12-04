package kelegram.client.ui

import androidx.compose.runtime.Composable
import kelegram.client.KelegramStylesheet
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

enum class BorderRadiusValue(val style: String) {
    Small(KelegramStylesheet.borderRadiusSmall),
    Medium(KelegramStylesheet.borderRadiusMedium),
    Large(KelegramStylesheet.borderRadiusLarge),
}

@Composable
fun Box(
    borderRadius: BorderRadiusValue = BorderRadiusValue.Medium,
    className: String? = null,
    content: @Composable () -> Unit
) {
    Div(attrs = {
        classes(borderRadius.style)
        if (className != null) {
            classes(className)
        }
    }) {
        content()
    }
}