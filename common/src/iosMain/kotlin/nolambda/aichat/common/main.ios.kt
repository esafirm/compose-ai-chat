package nolambda.aichat.common

import androidx.compose.ui.window.Application
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController =
    Application("Example Application") {
        App()
    }
