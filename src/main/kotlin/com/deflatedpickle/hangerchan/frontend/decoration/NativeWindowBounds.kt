/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.frontend.decoration

import com.deflatedpickle.hangerchan.api.Decoration
import com.deflatedpickle.hangerchan.backend.util.WindowDetector
import com.deflatedpickle.hangerchan.backend.util.win32.Win32WindowUtil
import java.awt.Color
import java.awt.Graphics2D

object NativeWindowBounds : Decoration {
    override val strokeSize: Float
        get() = 2f

    override val colour: Color
        get() = Color.MAGENTA

    override fun paint(g2D: Graphics2D) {
        for (nativeWindow in WindowDetector.windowList) {
            val rect = Win32WindowUtil.getRect(nativeWindow.hWnd)

            g2D.drawRect(
                    rect.left,
                    rect.top,
                    rect.right - rect.left,
                    rect.bottom - rect.top
            )
        }
    }
}
