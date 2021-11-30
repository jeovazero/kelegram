package kelegram.client.tokens

import org.jetbrains.compose.web.css.*

data class Palette(
    val primary: CSSColorValue = hsl(223, 81, 70),
    val primaryPair: CSSColorValue = hsl(190, 70, 57),
    val primaryText: CSSColorValue = hsl(0, 0, 100),
    val secondary: CSSColorValue = hsl(0, 100, 81),
    val secondaryPair: CSSColorValue = hsl(0, 100, 89),
    val secondaryText: CSSColorValue = hsl(0, 0, 100),
    val tertiary: CSSColorValue = hsl(112, 92, 45),
    val neutralLighten: CSSColorValue = hsl(0, 0, 100),
    val neutralLight: CSSColorValue = hsl(0, 0, 96),
    val neutralMedium: CSSColorValue = hsl(0, 0, 50),
    val neutralDark: CSSColorValue = hsl(0, 0, 33),
)

val FontFamily = arrayOf("Fira Sans","sans-serif")

object FontSize {
    val title: CSSSizeValue<CSSUnit.rem> = 1.5.cssRem
    val itemList: CSSSizeValue<CSSUnit.rem> = 1.125.cssRem
    val bodyText: CSSSizeValue<CSSUnit.rem> = 1.cssRem
    val bodySmallText: CSSSizeValue<CSSUnit.rem> = 0.85.cssRem
}

object BorderRadius {
    val small: CSSSizeValue<CSSUnit.px> = 4.px
    val medium: CSSSizeValue<CSSUnit.px> = 8.px
    val large: CSSSizeValue<CSSUnit.px> = 16.px
    val xLarge: CSSSizeValue<CSSUnit.px> = 24.px
}

object Shadow {
    val header: String = "0px 2px 16px rgba(0,0,0,0.06)"
}

object Token {
    val pallete: Palette = Palette()
    val fontFamily = FontFamily
    val borderRadius = BorderRadius
    val fontSize = FontSize
    val shadow = Shadow
}