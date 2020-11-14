/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.frontend

import java.awt.Color
import javax.swing.JFrame

object Window : JFrame("Hanger-chan") {
    init {
        this.isAlwaysOnTop = true
        this.isUndecorated = true

        this.background = Color(0, 0, 0, 0)

        this.extendedState = MAXIMIZED_BOTH
        this.defaultCloseOperation = EXIT_ON_CLOSE

        this.add(HangerPanel)
    }
}
