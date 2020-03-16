/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan

import com.sun.jna.platform.win32.WinDef
import org.jbox2d.dynamics.Body

class NativeWindow(
    val hwnd: WinDef.HWND,
    var lastUnits: WinDef.RECT,
    val body: Body,
    val internalBodyList: MutableList<Body>
)
