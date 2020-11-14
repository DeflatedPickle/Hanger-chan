/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.frontend

import com.deflatedpickle.hangerchan.api.Decoration
import java.awt.BasicStroke
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JPanel
import javax.swing.Timer

object HangerPanel : JPanel() {
    val decorationList = mutableListOf<Decoration>()

    private val timer = Timer(1000 / 144) {
        this.repaint()
    }

    init {
        this.isOpaque = false

        this.timer.start()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        g.font = Font(g.font.fontName, Font.BOLD, 14)

        for (decoration in this.decorationList) {
            val g2D = g.create() as Graphics2D
            g2D.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            )

            g2D.stroke = BasicStroke(decoration.strokeSize)
            g2D.color = decoration.colour

            decoration.paint(g2D)

            g2D.dispose()
        }
    }
}
