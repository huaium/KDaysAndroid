package com.mohamedrejeb.richeditor.parser.html

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals

internal class CssEncoderTest {
    @Test
    fun testParseCssStyle() {
        val style = "font-weight: bold; color: #ff0000; font-size: 12px;"
        val style2 = "font-weight  :  100; color   : #f00  ;   font-size:  -12.5px ;  "

        assertEquals(
            mapOf(
                "font-weight" to "bold",
                "color" to "#ff0000",
                "font-size" to "12px"
            ),
            CssEncoder.parseCssStyle(style)
        )
        assertEquals(
            mapOf(
                "font-weight" to "100",
                "color" to "#f00",
                "font-size" to "-12.5px"
            ),
            CssEncoder.parseCssStyle(style2)
        )
    }

    @Test
    fun testParseCssColor() {
        val color = "#ff0000"
        val color2 = "#f00"
        val color3 = "rgb(255, 0, 0)"
        val color4 = "rgba(255, 0, 0, 0.5)"
        val color5 = "blue"

        assertEquals(
            CssEncoder.parseCssColor(color),
            Color(255, 0, 0)
        )
        assertEquals(
            CssEncoder.parseCssColor(color2),
            Color(255, 0, 0)
        )
        assertEquals(
            CssEncoder.parseCssColor(color3),
            Color(255, 0, 0)
        )
        assertEquals(
            CssEncoder.parseCssColor(color4),
            Color(255, 0, 0, 127)
        )
        assertEquals(
            CssEncoder.parseCssColor(color5),
            Color(0, 0, 255)
        )
    }

    @Test
    fun testParseCssSize() {
        val size = "12px"
        val size2 = "12pt"
        val size3 = "12em"
        val size4 = "12rem"
        val size5 = "12%"
        val size6 = "12"
        val size7 = "12.5px"
        val size8 = "-12pt"

        assertEquals(
            12f,
            CssEncoder.parseCssSize(size)
        )
        assertEquals(
            16f,
            CssEncoder.parseCssSize(size2)?.roundToInt()?.toFloat()
        )
        assertEquals(
            192f,
            CssEncoder.parseCssSize(size3)
        )
        assertEquals(
            192f,
            CssEncoder.parseCssSize(size4)
        )
        assertEquals(
            1.92f,
            CssEncoder.parseCssSize(size5)
        )
        assertEquals(
            null,
            CssEncoder.parseCssSize(size6)
        )
        assertEquals(
            12.5f,
            CssEncoder.parseCssSize(size7)
        )
        assertEquals(
            -16f,
            CssEncoder.parseCssSize(size8)?.roundToInt()?.toFloat()
        )
    }

    @Test
    fun testParseCssFontWeight() {
        val fontWeight = "bold"
        val fontWeight2 = "bolder"
        val fontWeight3 = "lighter"
        val fontWeight4 = "100"
        val fontWeight5 = "900"
        val fontWeight6 = "normal"

        assertEquals(
            FontWeight.Bold,
            CssEncoder.parseCssFontWeight(fontWeight),
        )
        assertEquals(
            FontWeight.Black,
            CssEncoder.parseCssFontWeight(fontWeight2),
        )
        assertEquals(
            FontWeight.Thin,
            CssEncoder.parseCssFontWeight(fontWeight3),
        )
        assertEquals(
            FontWeight.Thin,
            CssEncoder.parseCssFontWeight(fontWeight4),
        )
        assertEquals(
            FontWeight.Black,
            CssEncoder.parseCssFontWeight(fontWeight5),
        )
        assertEquals(
            FontWeight.Normal,
            CssEncoder.parseCssFontWeight(fontWeight6),
        )
    }

    @Test
    fun testParseCssFontStyle() {
        val fontStyle = "italic"
        val fontStyle2 = "oblique"
        val fontStyle3 = "normal"
        val fontStyle4 = "wrong"

        assertEquals(
            FontStyle.Italic,
            CssEncoder.parseCssFontStyle(fontStyle),
        )
        assertEquals(
            FontStyle.Italic,
            CssEncoder.parseCssFontStyle(fontStyle2),
        )
        assertEquals(
            FontStyle.Normal,
            CssEncoder.parseCssFontStyle(fontStyle3),
        )
        assertEquals(
            null,
            CssEncoder.parseCssFontStyle(fontStyle4),
        )
    }

    @Test
    fun testParseCssTextDecoration() {
        val textDecoration = "underline"
        val textDecoration2 = "line-through"
        val textDecoration3 = "overline"
        val textDecoration4 = "none"
        val textDecoration5 = "wrong"

        assertEquals(
            TextDecoration.Underline,
            CssEncoder.parseCssTextDecoration(textDecoration),
        )
        assertEquals(
            TextDecoration.LineThrough,
            CssEncoder.parseCssTextDecoration(textDecoration2),
        )
        assertEquals(
            null,
            CssEncoder.parseCssTextDecoration(textDecoration3),
        )
        assertEquals(
            null,
            CssEncoder.parseCssTextDecoration(textDecoration4),
        )
        assertEquals(
            null,
            CssEncoder.parseCssTextDecoration(textDecoration5),
        )
    }

    @Test
    fun testParseCssBaselineShift() {
        val baselineShift = "sub"
        val baselineShift2 = "super"
        val baselineShift3 = "baseline"
        val baselineShift4 = "wrong"
        val baselineShift5 = "50%"

        assertEquals(
            BaselineShift.Subscript,
            CssEncoder.parseCssBaselineShift(baselineShift),
        )
        assertEquals(
            BaselineShift.Superscript,
            CssEncoder.parseCssBaselineShift(baselineShift2),
        )
        assertEquals(
            BaselineShift.None,
            CssEncoder.parseCssBaselineShift(baselineShift3),
        )
        assertEquals(
            null,
            CssEncoder.parseCssBaselineShift(baselineShift4),
        )
        assertEquals(
            BaselineShift(.5f),
            CssEncoder.parseCssBaselineShift(baselineShift5),
        )
    }

    @Test
    fun testParseCssTextShadow() {
        val textShadow = "1px 1px 1px #000"
        val textShadow2 = "1px 1px 2px black"
        val textShadow3 = "#fc0 1px 0 10px"
        val textShadow4 = "red 2px 5px"

        assertEquals(
            Shadow(
                offset = Offset(1f, 1f),
                blurRadius = 1f,
                color = Color(0, 0, 0)
            ),
            CssEncoder.parseCssTextShadow(textShadow)
        )
        assertEquals(
            Shadow(
                offset = Offset(1f, 1f),
                blurRadius = 2f,
                color = Color(0, 0, 0)
            ),
            CssEncoder.parseCssTextShadow(textShadow2)
        )
        assertEquals(
            Shadow(
                offset = Offset(1f, 0f),
                blurRadius = 10f,
                color = Color(255, 204, 0)
            ),
            CssEncoder.parseCssTextShadow(textShadow3)
        )
        assertEquals(
            Shadow(
                offset = Offset(2f, 5f),
                blurRadius = 0f,
                color = Color(255, 0, 0)
            ),
            CssEncoder.parseCssTextShadow(textShadow4)
        )
    }

    @Test
    fun testParseCssTextAlign() {
        val textAlign = "left"
        val textAlign2 = "right"
        val textAlign3 = "center"
        val textAlign4 = "justify"
        val textAlign5 = "wrong"

        assertEquals(
            TextAlign.Left,
            CssEncoder.parseCssTextAlign(textAlign),
        )
        assertEquals(
            TextAlign.Right,
            CssEncoder.parseCssTextAlign(textAlign2),
        )
        assertEquals(
            TextAlign.Center,
            CssEncoder.parseCssTextAlign(textAlign3),
        )
        assertEquals(
            TextAlign.Justify,
            CssEncoder.parseCssTextAlign(textAlign4),
        )
        assertEquals(
            null,
            CssEncoder.parseCssTextAlign(textAlign5),
        )
    }

    @Test
    fun testParseCssTextDirection() {
        val textDirection = "ltr"
        val textDirection2 = "rtl"
        val textDirection3 = "wrong"

        assertEquals(
            TextDirection.Ltr,
            CssEncoder.parseCssTextDirection(textDirection),
        )
        assertEquals(
            TextDirection.Rtl,
            CssEncoder.parseCssTextDirection(textDirection2),
        )
        assertEquals(
            null,
            CssEncoder.parseCssTextDirection(textDirection3),
        )
    }

    @Test
    fun testParseCssLineHeight() {
        val lineHeight = "1.5"
        val lineHeight2 = "normal"
        val lineHeight3 = "wrong"
        val lineHeight4 = "150%"
        val lineHeight5 = "3em"
        val lineHeight6 = "24px"

        assertEquals(
            1.5.em,
            CssEncoder.parseCssLineHeight(lineHeight),
        )
        assertEquals(
            TextUnit.Unspecified,
            CssEncoder.parseCssLineHeight(lineHeight2),
        )
        assertEquals(
            TextUnit.Unspecified,
            CssEncoder.parseCssLineHeight(lineHeight3),
        )
        assertEquals(
            1.5.em,
            CssEncoder.parseCssLineHeight(lineHeight4),
        )
        assertEquals(
            3.em,
            CssEncoder.parseCssLineHeight(lineHeight5),
        )
        assertEquals(
            24.sp,
            CssEncoder.parseCssLineHeight(lineHeight6),
        )
    }

    @Test
    fun testParseCssTextIndent() {
        val textIndent1 = "wrong"
        val textIndent2 = "150%"
        val textIndent3 = "3em"
        val textIndent4 = "24px"

        assertEquals(
            null,
            CssEncoder.parseCssTextIndent(textIndent1),
        )
        assertEquals(
            TextIndent(1.5.em, 1.5.em),
            CssEncoder.parseCssTextIndent(textIndent2),
        )
        assertEquals(
            TextIndent(3.em, 3.em),
            CssEncoder.parseCssTextIndent(textIndent3),
        )
        assertEquals(
            TextIndent(24.sp, 24.sp),
            CssEncoder.parseCssTextIndent(textIndent4),
        )
    }
}