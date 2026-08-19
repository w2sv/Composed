package com.w2sv.composed.playground

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main(args: Array<String>) =
    when {
        args.contentEquals(arrayOf("--help")) -> println(playgroundUsage)
        else -> runCatching { parseInitialSample(args) }.fold(
            onSuccess = ::launchPlayground,
            onFailure = ::printUsageError
        )
    }

private fun printUsageError(error: Throwable) {
    System.err.println(error.message)
    System.err.println()
    System.err.println(playgroundUsage)
}

private fun launchPlayground(initialSample: Sample?) =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Composed playground",
            onPreviewKeyEvent = {
                val escOrQPressed = (it.key == Key.Escape || it.key == Key.Q) && it.type == KeyEventType.KeyDown
                if (escOrQPressed) exitApplication()
                escOrQPressed
            }
        ) {
            Playground(initialSample = initialSample)
        }
    }

internal fun parseInitialSample(args: Array<String>): Sample? {
    require(args.size <= 1) { "Expected at most one sample ID." }
    val sampleId = args.singleOrNull() ?: return null
    return requireNotNull(Sample.fromId(sampleId)) { "Unknown sample '$sampleId'." }
}

internal val playgroundUsage: String
    get() = buildString {
        appendLine("Gradle usage:")
        appendLine("  ./gradlew :playground:run [--args=<sample-id>]")
        appendLine("  ./gradlew :playground:hotRunJvm --auto [--args=<sample-id>]")
        appendLine()
        appendLine("Samples:")
        Sample.entries.forEach {
            appendLine("  ${it.id.padEnd(20)} ${it.title}")
        }
    }.trimEnd()
