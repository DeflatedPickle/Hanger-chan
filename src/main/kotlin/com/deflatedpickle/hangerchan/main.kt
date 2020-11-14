/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan

import com.deflatedpickle.hangerchan.backend.util.WindowDetector
import com.deflatedpickle.hangerchan.frontend.HangerPanel
import com.deflatedpickle.hangerchan.frontend.Window
import com.deflatedpickle.hangerchan.frontend.decoration.NativeWindowBounds
import com.deflatedpickle.hangerchan.frontend.decoration.NativeWindowTitle
import javax.swing.SwingUtilities

fun main() {
    HangerPanel.decorationList.add(NativeWindowBounds)
    HangerPanel.decorationList.add(NativeWindowTitle)

    SwingUtilities.invokeLater {
        Window.isVisible = true

        Thread(WindowDetector, "WindowDetector").start()
    }
}
