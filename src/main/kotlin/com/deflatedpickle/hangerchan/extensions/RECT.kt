/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.extensions

import com.sun.jna.platform.win32.WinDef

fun WinDef.RECT.isInside(x: Int, y: Int) =
        isInside(x.toFloat(), y.toFloat(), 1f)

fun WinDef.RECT.isInside(x: Float, y: Float) =
        isInside(x, y, 1f)

fun WinDef.RECT.isInside(x: Float, y: Float, scale: Float) =
        isInsideX(x, scale) && isInsideY(y, scale)

fun WinDef.RECT.isInsideX(x: Int) =
        isInsideX(x.toFloat(), 1f)

fun WinDef.RECT.isInsideX(x: Float, scale: Float) =
        x * scale > this.left &&
        x * scale < this.right

fun WinDef.RECT.isInsideY(y: Int) =
        isInsideX(y.toFloat(), 1f)

fun WinDef.RECT.isInsideY(y: Float, scale: Float) =
        y * scale > this.top &&
        y * scale < this.bottom
