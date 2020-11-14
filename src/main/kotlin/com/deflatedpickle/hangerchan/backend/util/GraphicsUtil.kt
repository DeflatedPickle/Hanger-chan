/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.backend.util

import com.sun.jna.platform.win32.WinDef
import java.awt.Color
import java.awt.Graphics2D

object GraphicsUtil {
    fun drawText(g2D: Graphics2D, string: String, rect: WinDef.RECT, xPad: Int = 2, yPad: Int = 2, xAdder: Int = 0, yAdder: Int = 0) {
        val textWidth = g2D.fontMetrics.stringWidth(string)
        val textHeight = g2D.fontMetrics.height

        val originalColour = g2D.color

        g2D.color = Color.BLACK
        g2D.fillRect(
                rect.left + xAdder,
                rect.top + yAdder,
                textWidth + xPad * 2,
                textHeight + yPad * 2
        )

        g2D.color = Color.WHITE
        g2D.drawString(
                string,
                rect.left + xAdder + xPad,
                rect.top + yAdder + textHeight - textHeight / 4 + yPad
        )

        g2D.color = originalColour
    }
}
