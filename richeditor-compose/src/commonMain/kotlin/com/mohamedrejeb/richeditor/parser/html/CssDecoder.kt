package com.mohamedrejeb.richeditor.parser.html

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.utils.maxDecimals
import kotlin.math.roundToInt

internal object CssDecoder {

    /**
     * Decodes the given CSS style map into a CSS style string.
     *
     * @param cssStyleMap the CSS style map to decode.
     * @return the decoded CSS style string.
     */
    internal fun decodeCssStyleMap(cssStyleMap: Map<String, String>): String {
        return cssStyleMap
            .map { "${it.key}: ${it.value};" }
            .joinToString(" ")
    }

    /**
     * Decodes the given span style into a CSS style map.
     *
     * @param spanStyle the span style to decode.
     * @return the decoded CSS style map.
     */
    internal fun decodeSpanStyleToCssStyleMap(spanStyle: SpanStyle): Map<String, String> {
        val cssStyleMap = mutableMapOf<String, String>()

        if (spanStyle.color.isSpecified) {
            cssStyleMap["color"] = decodeColorToCss(spanStyle.color)
        }
        if (spanStyle.fontSize.isSpecified) {
            decodeTextUnitToCss(spanStyle.fontSize)?.let { fontSize ->
                cssStyleMap["font-size"] = fontSize
            }
        }
        spanStyle.fontWeight?.let { fontWeight ->
            cssStyleMap["font-weight"] = decodeFontWeightToCss(fontWeight)
        }
        spanStyle.fontStyle?.let { fontStyle ->
            cssStyleMap["font-style"] = decodeFontStyleToCss(fontStyle)
        }
        if (spanStyle.letterSpacing.isSpecified) {
            decodeTextUnitToCss(spanStyle.letterSpacing)?.let { letterSpacing ->
                cssStyleMap["letter-spacing"] = letterSpacing
            }
        }
        spanStyle.baselineShift?.let { baselineShift ->
            cssStyleMap["baseline-shift"] = decodeBaselineShiftToCss(baselineShift)
        }
        if (spanStyle.background.isSpecified) {
            cssStyleMap["background"] = decodeColorToCss(spanStyle.background)
        }
        spanStyle.textDecoration?.let { textDecoration ->
            cssStyleMap["text-decoration"] = decodeTextDecorationToCss(textDecoration)
        }
        spanStyle.shadow?.let { shadow ->
            cssStyleMap["text-shadow"] = decodeTextShadowToCss(shadow)
        }

        return cssStyleMap
    }

    /**
     * Decodes the give [ParagraphStyle] into a CSS style map.
     *
     * @param paragraphStyle the paragraph style to decode.
     * @return the decoded CSS style map.
     */
    internal fun decodeParagraphStyleToCssStyleMap(paragraphStyle: ParagraphStyle): Map<String, String> {
        val cssStyleMap = mutableMapOf<String, String>()

        decodeTextAlignToCss(paragraphStyle.textAlign, paragraphStyle.textDirection)?.let { textAlign ->
            cssStyleMap["text-align"] = textAlign
        }

        decodeTextDirectionToCss(paragraphStyle.textDirection)?.let { textDirection ->
            cssStyleMap["direction"] = textDirection
        }

        decodeTextUnitToCss(paragraphStyle.lineHeight)?.let { lineHeight ->
            cssStyleMap["line-height"] = lineHeight
        }

        decodeTextUnitToCss(paragraphStyle.textIndent?.firstLine)?.let { textIndent ->
            cssStyleMap["text-indent"] = textIndent
        }

        return cssStyleMap
    }

    /**
     * Decodes the given [Color] to a CSS color string.
     *
     * @param color the color to decode.
     * @return the decoded CSS color string.
     */
    internal fun decodeColorToCss(color: Color): String {
        return when (color) {
            Color.Unspecified -> ""
            else -> "rgba(${(color.red * 255).roundToInt()}, ${(color.green * 255).roundToInt()}, ${(color.blue * 255).roundToInt()}, ${color.alpha.maxDecimals(2)})"
        }
    }

    /**
     * Decodes the given size and returns a CSS size string.
     *
     * @param size the size to decode.
     * @return the decoded CSS size string.
     */
    internal fun decodeSizeToCss(size: Float): String {
        return "${size}px"
    }

    /**
     * Decodes the given [TextUnit] to a CSS text unit string.
     *
     * @param textUnit the text unit to decode.
     * @return the decoded CSS text unit string.
     */
    internal fun decodeTextUnitToCss(textUnit: TextUnit?): String? {
        return when {
            textUnit == null -> null
            textUnit.isUnspecified -> null
            textUnit.isSp -> "${textUnit.value}px"
            textUnit.isEm -> "${textUnit.value}em"
            else -> null
        }
    }

    /**
     * Decode the given [FontWeight] to a CSS font weight string.
     *
     * @param fontWeight the font weight to decode.
     * @return the decoded CSS font weight string.
     */
    internal fun decodeFontWeightToCss(fontWeight: FontWeight): String {
        return when (fontWeight) {
            FontWeight.Thin -> "100"
            FontWeight.ExtraLight -> "200"
            FontWeight.Light -> "300"
            FontWeight.Normal -> "400"
            FontWeight.Medium -> "500"
            FontWeight.SemiBold -> "600"
            FontWeight.Bold -> "700"
            FontWeight.ExtraBold -> "800"
            FontWeight.Black -> "900"
            else -> fontWeight.weight.toString()
        }
    }

    /**
     * Decodes the given [FontStyle] to a CSS font style string.
     *
     * @param fontStyle the font style to decode.
     * @return the decoded CSS font style string.
     */
    internal fun decodeFontStyleToCss(fontStyle: FontStyle): String {
        return when (fontStyle) {
            FontStyle.Normal -> "normal"
            FontStyle.Italic -> "italic"
            else -> "normal"
        }
    }

    /**
     * Decodes the given [TextDecoration] to a CSS text decoration string.
     *
     * @param textDecoration the text decoration to decode.
     * @return the decoded CSS text decoration string.
     */
    internal fun decodeTextDecorationToCss(textDecoration: TextDecoration): String {
        return when (textDecoration) {
            TextDecoration.None -> "none"
            TextDecoration.Underline -> "underline"
            TextDecoration.LineThrough -> "line-through"
            TextDecoration.Underline + TextDecoration.LineThrough -> "underline line-through"
            else -> "none"
        }
    }

    /**
     * Decodes the given [BaselineShift] to a CSS baseline shift string.
     *
     * @param baselineShift the baseline shift to decode.
     * @return the decoded CSS baseline shift string.
     */
    internal fun decodeBaselineShiftToCss(baselineShift: BaselineShift): String {
        return when (baselineShift) {
            BaselineShift.Subscript -> "sub"
            BaselineShift.Superscript -> "super"
            BaselineShift.None -> "baseline"
            else -> "${(baselineShift.multiplier * 100).roundToInt()}%"
        }
    }

    /**
     * Decodes the given [Shadow] to a CSS text shadow string.
     *
     * @param shadow the shadow to decode.
     * @return the decoded CSS text shadow string.
     */
    internal fun decodeTextShadowToCss(shadow: Shadow): String {
        val color = decodeColorToCss(shadow.color)
        val offsetX = decodeSizeToCss(shadow.offset.x)
        val offsetY = decodeSizeToCss(shadow.offset.y)
        val blurRadius = decodeSizeToCss(shadow.blurRadius)

        return "$offsetX $offsetY $blurRadius $color"
    }

    /**
     * Decodes the given [TextAlign] to a CSS text align string.
     *
     * @param textAlign the text align to decode.
     * @param textDirection the text direction to decode.
     * @return the decoded CSS text align string.
     */
    internal fun decodeTextAlignToCss(
        textAlign: TextAlign?,
        textDirection: TextDirection? = null
    ): String? {
        return when (textAlign) {
            TextAlign.Left -> "left"
            TextAlign.Center -> "center"
            TextAlign.Right -> "right"
            TextAlign.Justify -> "justify"
            TextAlign.Start -> if (textDirection == TextDirection.Rtl) "right" else "left"
            TextAlign.End -> if (textDirection == TextDirection.Rtl) "left" else "right"
            else -> null
        }
    }

    /**
     * Decodes the given [TextDirection] to a CSS text direction string.
     *
     * @param textDirection the text direction to decode.
     * @return the decoded CSS text direction string.
     */
    internal fun decodeTextDirectionToCss(textDirection: TextDirection?): String? {
        return when (textDirection) {
            TextDirection.Ltr -> "ltr"
            TextDirection.Rtl -> "rtl"
            else -> null
        }
    }

}