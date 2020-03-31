/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan

import java.awt.Color
import javax.swing.JFrame

object ApplicationWindow : JFrame("Hanger-chan") {
    init {
        this.defaultCloseOperation = EXIT_ON_CLOSE

        this.isUndecorated = true
        // TODO: You should be able to drag a window over her, causing her to peek out the side at you
        this.isAlwaysOnTop = true

        this.background = Color(0, 0, 0, 0)

        this.extendedState = MAXIMIZED_BOTH
    }
}
