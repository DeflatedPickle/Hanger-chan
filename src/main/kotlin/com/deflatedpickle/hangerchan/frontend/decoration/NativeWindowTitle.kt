/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.frontend.decoration

import com.deflatedpickle.hangerchan.api.Decoration
import com.deflatedpickle.hangerchan.backend.util.GraphicsUtil
import com.deflatedpickle.hangerchan.backend.util.WindowDetector
import com.deflatedpickle.hangerchan.backend.util.win32.Win32WindowUtil
import java.awt.Graphics2D

object NativeWindowTitle : Decoration {
    override fun paint(g2D: Graphics2D) {
        for (nativeWindow in WindowDetector.windowList) {
            val title = Win32WindowUtil.getTitle(nativeWindow.hWnd)
            val rect = Win32WindowUtil.getRect(nativeWindow.hWnd)

            GraphicsUtil.drawText(g2D, title, rect)
        }
    }
}
