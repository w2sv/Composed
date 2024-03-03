/**
 * Entire file content taken from https://stackoverflow.com/a/70162451/12083276.
 */

package com.w2sv.composeutils

import android.content.res.Resources
import android.graphics.Typeface
import android.text.Spanned
import android.text.SpannedString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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

@Composable
fun annotatedStringResource(@StringRes id: Int, vararg formatArgs: Any): AnnotatedString {
    val resources = resources()
    val density = LocalDensity.current
    return remember(id, formatArgs) {
        val text = resources.getText(id, *formatArgs)
        spannableStringToAnnotatedString(text, density)
    }
}

@Composable
fun annotatedStringResource(@StringRes id: Int): AnnotatedString {
    val resources = resources()
    val density = LocalDensity.current
    return remember(id) {
        val text = resources.getText(id)
        spannableStringToAnnotatedString(text, density)
    }
}

@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    return LocalContext.current.resources
}

private fun Spanned.toHtmlWithoutParagraphs(): String {
    return HtmlCompat
        .toHtml(this, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
        .substringAfter("<p dir=\"ltr\">")
        .substringBeforeLast("</p>")
}

private fun Resources.getText(@StringRes id: Int, vararg args: Any): CharSequence {
    return HtmlCompat.fromHtml(
        String.format(
            SpannedString(getText(id)).toHtmlWithoutParagraphs(),
            *args
                .map {
                    if (it is Spanned) it.toHtmlWithoutParagraphs() else it
                }
                .toTypedArray()
        ),
        HtmlCompat.FROM_HTML_MODE_LEGACY
    )
}

private fun spannableStringToAnnotatedString(
    text: CharSequence,
    density: Density
): AnnotatedString {
    return when (text) {
        is Spanned -> {
            buildAnnotatedString {
                append(text.toString())
                text.getSpans(0, text.length, Any::class.java)
                    .forEach {
                        val start = text.getSpanStart(it)
                        val end = text.getSpanEnd(it)
                        when (it) {
                            is StyleSpan -> when (it.style) {
                                Typeface.NORMAL -> addStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Normal
                                    ),
                                    start,
                                    end
                                )

                                Typeface.BOLD -> addStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontStyle = FontStyle.Normal
                                    ),
                                    start,
                                    end
                                )

                                Typeface.ITALIC -> addStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Italic
                                    ),
                                    start,
                                    end
                                )

                                Typeface.BOLD_ITALIC -> addStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontStyle = FontStyle.Italic
                                    ),
                                    start,
                                    end
                                )
                            }

                            is TypefaceSpan -> addStyle(
                                SpanStyle(
                                    fontFamily = when (it.family) {
                                        FontFamily.SansSerif.name -> FontFamily.SansSerif
                                        FontFamily.Serif.name -> FontFamily.Serif
                                        FontFamily.Monospace.name -> FontFamily.Monospace
                                        FontFamily.Cursive.name -> FontFamily.Cursive
                                        else -> FontFamily.Default
                                    }
                                ),
                                start,
                                end
                            )

                            is AbsoluteSizeSpan -> with(density) {
                                addStyle(
                                    SpanStyle(fontSize = if (it.dip) it.size.dp.toSp() else it.size.toSp()),
                                    start,
                                    end
                                )
                            }

                            is RelativeSizeSpan -> addStyle(
                                SpanStyle(fontSize = it.sizeChange.em),
                                start,
                                end
                            )

                            is StrikethroughSpan -> addStyle(
                                SpanStyle(textDecoration = TextDecoration.LineThrough),
                                start,
                                end
                            )

                            is UnderlineSpan -> addStyle(
                                SpanStyle(textDecoration = TextDecoration.Underline),
                                start,
                                end
                            )

                            is SuperscriptSpan -> addStyle(
                                SpanStyle(baselineShift = BaselineShift.Superscript),
                                start,
                                end
                            )

                            is SubscriptSpan -> addStyle(
                                SpanStyle(baselineShift = BaselineShift.Subscript),
                                start,
                                end
                            )

                            is ForegroundColorSpan -> addStyle(
                                SpanStyle(color = Color(it.foregroundColor)),
                                start,
                                end
                            )

                            else -> addStyle(SpanStyle(), start, end)
                        }
                    }
            }
        }

        else -> {
            AnnotatedString(text.toString())
        }
    }
}