/**
 * Functions adopted from https://stackoverflow.com/a/70162451/12083276.
 */

package com.w2sv.composed.core

import android.content.res.Resources
import android.graphics.Typeface
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.core.text.HtmlCompat
import androidx.core.text.toHtml
import androidx.core.text.toSpanned

/**
 * @return A remembered html-styled resource text converted to an [AnnotatedString], keyed by [id] and [formatArgs].
 * #
 * Tested with:
 * - bold: &lt;b&gt;
 * - italic: &lt;i&gt;
 * - underline: &lt;u&gt;
 * - subscript: &lt;sub&gt;
 * - superscript: &lt;sup&gt;
 * - foreground color: &lt;font color="#9900FF"&gt;
 * - monospace font family: &lt;font face="monospace"&gt;
 */
@Composable
fun rememberStyledTextResource(@StringRes id: Int, vararg formatArgs: Any): AnnotatedString {
    val resources = LocalResources.current
    val density = LocalDensity.current
    return remember(id, *formatArgs) {
        resources.getAnnotatedString(id, density, *formatArgs)
    }
}

/**
 * Converts a html-styled resource text to an [AnnotatedString].
 * #
 * Tested with:
 * - bold: &lt;b&gt;
 * - italic: &lt;i&gt;
 * - underline: &lt;u&gt;
 * - subscript: &lt;sub&gt;
 * - superscript: &lt;sup&gt;
 * - foreground color: &lt;font color="#9900FF"&gt;
 * - monospace font family: &lt;font face="monospace"&gt;
 */
fun Resources.getAnnotatedString(
    @StringRes id: Int,
    density: Density? = null,
    vararg formatArgs: Any
): AnnotatedString =
    spannableStringToAnnotatedString(
        text = getHtmlText(id, *formatArgs),
        density = density
    )

private fun Resources.getHtmlText(@StringRes id: Int, vararg args: Any): Spanned =
    HtmlCompat.fromHtml(
        String.format(
            getText(id).toSpanned().toHtmlWithoutParagraphs(),
            *args
                .map { if (it is Spanned) it.toHtmlWithoutParagraphs() else it }
                .toTypedArray()
        ),
        HtmlCompat.FROM_HTML_MODE_COMPACT
    )

private fun Spanned.toHtmlWithoutParagraphs(): String =
    this
        .toHtml()
        .substringAfter("<p dir=\"ltr\">")
        .substringBeforeLast("</p>")

private fun spannableStringToAnnotatedString(text: Spanned, density: Density?): AnnotatedString =
    buildAnnotatedString {
        append(text)
        text.getSpans(0, text.length, Any::class.java)
            .forEach { span ->
                addStyle(
                    when (span) {
                        is StyleSpan -> when (span.style) {
                            Typeface.NORMAL -> SpanStyle(
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal
                            )

                            Typeface.BOLD -> SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Normal
                            )

                            Typeface.ITALIC -> SpanStyle(
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Italic
                            )

                            else -> SpanStyle()
                        }

                        is TypefaceSpan -> SpanStyle(
                            fontFamily = when (span.family) {
                                // FontFamily.SansSerif.name -> FontFamily.SansSerif
                                // FontFamily.Serif.name -> FontFamily.Serif
                                FontFamily.Monospace.name -> FontFamily.Monospace
                                // FontFamily.Cursive.name -> FontFamily.Cursive
                                else -> FontFamily.Default
                            }
                        )

                        is AbsoluteSizeSpan -> {
                            density
                                ?.run { SpanStyle(fontSize = if (span.dip) span.size.dp.toSp() else span.size.toSp()) }
                                ?: throw IllegalArgumentException(
                                    "Found AbsoluteSizeSpan but passed density null. Pass a Density to convert."
                                )
                        }

                        is RelativeSizeSpan -> SpanStyle(fontSize = span.sizeChange.em)
                        // is StrikethroughSpan -> SpanStyle(textDecoration = TextDecoration.LineThrough)
                        is UnderlineSpan -> SpanStyle(textDecoration = TextDecoration.Underline)
                        is SuperscriptSpan -> SpanStyle(baselineShift = BaselineShift.Superscript)
                        is SubscriptSpan -> SpanStyle(baselineShift = BaselineShift.Subscript)
                        is ForegroundColorSpan -> SpanStyle(color = Color(span.foregroundColor))
                        else -> SpanStyle()
                    },
                    text.getSpanStart(span),
                    text.getSpanEnd(span)
                )
            }
    }
