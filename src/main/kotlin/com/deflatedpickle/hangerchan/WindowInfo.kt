package com.deflatedpickle.hangerchan

import com.sun.jna.platform.win32.WinDef
import org.jbox2d.dynamics.Body

class WindowInfo(val hwnd: WinDef.HWND, var lastUnits: WinDef.RECT, val body: Body)