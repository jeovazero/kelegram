package kelegram.client

import kelegram.client.tokens.Token
import org.jetbrains.compose.web.css.*

object AppCSSVariables {
    val primary by variable<CSSColorValue>()
    val primaryPair by variable<CSSColorValue>()
    val primaryText by variable<CSSColorValue>()
    val neutralLight by variable<CSSColorValue>()
    val neutralLighten by variable<CSSColorValue>()
    val neutralDark by variable<CSSColorValue>()
}

object ResetStylesheet : StyleSheet() {
    init {
        "body,h1,h2,h3,h4,h5,h6,div,span,p" style {
            margin(0.px)
            padding(0.px)
            borderWidth(0.px);
            fontSize(100.percent)
            font("inhehit")
        }
    }
}

object KelegramStylesheet : StyleSheet() {
    init {
        "p,span,button,label,h1,h2,h3,h4,h5,h6" style {
            fontFamily(*Token.fontFamily)
        }
    }
    val kTheme by style {
        AppCSSVariables.primary(Token.pallete.primary)
        AppCSSVariables.primaryPair(Token.pallete.primaryPair)
        AppCSSVariables.primaryText(Token.pallete.primaryText)
        AppCSSVariables.neutralLight(Token.pallete.neutralLight)
        AppCSSVariables.neutralLighten(Token.pallete.neutralLighten)
        AppCSSVariables.neutralDark(Token.pallete.neutralDark)
    }
    val primaryText by style {
        color(AppCSSVariables.primaryText.value())
    }
    val bgPrimary by style {
        background(
            "linear-gradient(115deg, ${AppCSSVariables.primary.value()}" +
                    " 10.95%, ${AppCSSVariables.primaryPair.value()} 94.7%)"
        )
    }
    val borderRadiusSmall by style {
        borderRadius(Token.borderRadius.small)
    }
    val borderRadiusMedium by style {
        borderRadius(Token.borderRadius.medium)
    }
    val borderRadiusLarge by style {
        borderRadius(Token.borderRadius.large)
    }
    val borderRadiusXLarge by style {
        borderRadius(Token.borderRadius.xLarge)
    }
}
