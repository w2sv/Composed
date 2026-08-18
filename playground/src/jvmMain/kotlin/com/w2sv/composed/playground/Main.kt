package com.w2sv.composed.playground

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Composed playground"
        ) {
            PlaygroundApp()
        }
    }
