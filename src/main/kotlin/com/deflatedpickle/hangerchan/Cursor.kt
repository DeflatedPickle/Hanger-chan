/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan

import com.deflatedpickle.hangerchan.util.WindowUtil
import com.deflatedpickle.hangerchan.util.physics.PhysicsUtil
import com.deflatedpickle.hangerchan.util.win32.CursorUtil

object Cursor {
    val body = CursorUtil.createBody(PhysicsUtil.world)

    // Changes when the mouse is clicked -- used to determine thrown force
    var clickedPoint = WindowUtil.Point(0, 0)
    var releasedPoint = WindowUtil.Point(0, 0)

    // Changes when the mouse is moved
    var currentPoint = WindowUtil.Point(0, 0)
}
