/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan

import com.deflatedpickle.hangerchan.util.physics.PhysicsUtil
import com.deflatedpickle.hangerchan.util.win32.CursorUtil

object Cursor {
    val body = CursorUtil.createBody(PhysicsUtil.world)

    // Changes when the mouse is clicked -- used to determine thrown force
    var clickedX = 0
    var clickedY = 0
    var releasedX = 0
    var releasedY = 0

    // Changes when the mouse is moved
    var mouseX = 0
    var mouseY = 0
}
