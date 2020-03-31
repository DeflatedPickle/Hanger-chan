/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.util.win32

import com.deflatedpickle.hangerchan.ApplicationWindow
import com.deflatedpickle.hangerchan.util.physics.PhysicsUtil
import com.sun.jna.Native
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import kotlin.properties.Delegates

object MonitorUtil {
    lateinit var monitor: WinUser.HMONITOR
    val monitorInfo = WinUser.MONITORINFO()

    var monitorWidth by Delegates.notNull<Float>()
    var monitorHeight by Delegates.notNull<Float>()

    init {
        if (!MonitorUtil::monitor.isInitialized) {
            monitor = User32.INSTANCE.MonitorFromWindow(WinDef.HWND(Native.getComponentPointer(ApplicationWindow)), User32.MONITOR_DEFAULTTONEAREST)
            User32.INSTANCE.GetMonitorInfo(monitor, monitorInfo)

            monitorWidth = monitorInfo.rcWork.right.toFloat() * PhysicsUtil.scaleDown
            monitorHeight = monitorInfo.rcWork.bottom.toFloat() * PhysicsUtil.scaleDown
        }
    }
}