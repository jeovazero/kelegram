package kelegram.client

import kelegram.client.tokens.Token
import org.jetbrains.compose.web.css.*

object AppCSSVariables {
    val primary by variable<CSSColorValue>()
    val primaryPair by variable<CSSColorValue>()
    val primaryOpacity by variable<CSSColorValue>()
    val primaryPairOpacity by variable<CSSColorValue>()
    val primaryText by variable<CSSColorValue>()
    val secondary by variable<CSSColorValue>()
    val secondaryPair by variable<CSSColorValue>()
    val tertiary by variable<CSSColorValue>()
    val neutralLight by variable<CSSColorValue>()
    val neutralLighten by variable<CSSColorValue>()
    val neutralDark by variable<CSSColorValue>()
    val neutralMedium by variable<CSSColorValue>()
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

fun CSSBuilder.createBG(
    color1: CSSColorValue,
    color2: CSSColorValue,
) {
   background(
       "linear-gradient(115deg, $color1 10.95%, $color2 94.7%)"
   )
}

object KelegramStylesheet : StyleSheet() {
    init {
        "p,span,button,label,h1,h2,h3,h4,h5,h6,input" style {
            fontFamily(*Token.fontFamily)
        }
    }

    val kTheme by style {
        AppCSSVariables.primary(Token.pallete.primary)
        AppCSSVariables.primaryOpacity(Token.pallete.primaryOpacity)
        AppCSSVariables.primaryPair(Token.pallete.primaryPair)
        AppCSSVariables.primaryPairOpacity(Token.pallete.primaryPairOpacity)
        AppCSSVariables.primaryText(Token.pallete.primaryText)
        AppCSSVariables.secondary(Token.pallete.secondary)
        AppCSSVariables.secondaryPair(Token.pallete.secondaryPair)
        AppCSSVariables.tertiary(Token.pallete.tertiary)
        AppCSSVariables.neutralLight(Token.pallete.neutralLight)
        AppCSSVariables.neutralMedium(Token.pallete.neutralMedium)
        AppCSSVariables.neutralLighten(Token.pallete.neutralLighten)
        AppCSSVariables.neutralDark(Token.pallete.neutralDark)
    }
    val primaryText by style {
        color(AppCSSVariables.primaryText.value())
    }
    val bgPrimary by style {
        createBG(AppCSSVariables.primary.value(),
            AppCSSVariables.primaryPair.value())
    }
    val bgSecondary by style {
        createBG(AppCSSVariables.secondary.value(),
            AppCSSVariables.secondaryPair.value())
    }
    val bgNeutral by style {
        createBG(AppCSSVariables.neutralLight.value(),
            AppCSSVariables.neutralLighten.value())
        color(AppCSSVariables.neutralMedium.value())
    }
    val bgPrimaryOpacity by style {
        createBG(AppCSSVariables.primaryOpacity.value(),
            AppCSSVariables.primaryPairOpacity.value())
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
