package kelegram.client.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

object SpacingStyle : StyleSheet() {
    val small by style {
        child(self, not(universal + firstOfType)) style {
            marginTop(0.5.cssRem)
        }
    }
    val medium by style {
        child(self, not(universal + firstOfType)) style {
            marginTop(1.cssRem)
        }
    }
}

enum class Spacing(val style: String) {
    Small(SpacingStyle.small),
    Medium(SpacingStyle.medium)
}

// TODO: add classes prop and spacing-between
@Composable
fun Stack(spacing: Spacing? = null, className: String? = null, content: @Composable () -> Unit) {
    Div(attrs = {
        if (spacing != null) {
            classes(spacing.style)
        }
        if (className != null) {
            classes(className)
        }
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
        }
    }) {
        content()
    }
}