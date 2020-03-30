package com.deflatedpickle.hangerchan

import com.deflatedpickle.hangerchan.util.CursorUtil
import com.deflatedpickle.hangerchan.util.PhysicsUtil

object Cursor {
    val body = CursorUtil.createBody(PhysicsUtil.world)

    // Changes when the mouse is clicked -- used to determine thrown force
    var clickedX = 0f
    var clickedY = 0f
    var releasedX = 0f
    var releasedY = 0f

    // Changes when the mouse is moved
    var mouseX = 0f
    var mouseY = 0f
}