/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.api

import java.awt.Color
import java.awt.Graphics2D

interface Decoration {
    val strokeSize: Float
        get() = 1f

    val colour: Color
        get() = Color.BLACK

    fun paint(g2D: Graphics2D)
}
