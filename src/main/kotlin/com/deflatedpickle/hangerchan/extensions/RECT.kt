package com.deflatedpickle.hangerchan.extensions

import com.deflatedpickle.hangerchan.util.PhysicsUtil
import com.sun.jna.platform.win32.WinDef

fun WinDef.RECT.isInside(x: Float, y: Float, scale: Float) = x * PhysicsUtil.scaleUp > this.left &&
            x * PhysicsUtil.scaleUp < this.right &&
            y * PhysicsUtil.scaleUp > this.top &&
            y * PhysicsUtil.scaleUp < this.bottom
