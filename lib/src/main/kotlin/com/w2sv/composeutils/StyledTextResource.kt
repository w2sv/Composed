package com.w2sv.composeutils

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.core.text.HtmlCompat

/**
 * @see <a href="From https://stackoverflow.com/a/77319763/12083276">Reference</a>
 */
@Composable
@ReadOnlyComposable
fun styledTextResource(@StringRes id: Int): AnnotatedString =
    HtmlCompat.fromHtml(
        stringResource(id),
        HtmlCompat.FROM_HTML_MODE_COMPACT
    )
        .toAnnotatedString()

private fun Spanned.toAnnotatedString(): AnnotatedString =
    buildAnnotatedString {
        append(this@toAnnotatedString.toString())

        getSpans(
            0,
            this@toAnnotatedString.length,
            Any::class.java
        )
            .forEach { span ->
                val start = getSpanStart(span)
                val end = getSpanEnd(span)

                when (span) {
                    is StyleSpan ->
                        when (span.style) {
                            Typeface.BOLD -> addStyle(
                                SpanStyle(fontWeight = FontWeight.Bold),
                                start,
                                end
                            )

                            Typeface.ITALIC -> addStyle(
                                SpanStyle(fontStyle = FontStyle.Italic),
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
                }
            }
    }