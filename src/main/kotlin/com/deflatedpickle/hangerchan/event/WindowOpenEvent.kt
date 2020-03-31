/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.event

import com.deflatedpickle.hangerchan.HangerChan
import com.deflatedpickle.hangerchan.NativeWindow
import com.deflatedpickle.hangerchan.util.WindowUtil
import com.deflatedpickle.hangerchan.util.physics.BorderUtil
import com.deflatedpickle.hangerchan.util.physics.PhysicsUtil
import com.deflatedpickle.hangerchan.util.win32.Win32WindowUtil
import com.sun.jna.platform.win32.WinDef
import org.jbox2d.dynamics.Body

object WindowOpenEvent : AbstractEvent<WinDef.HWND>() {
    init {
        WindowOpenEvent.addListener { hWnd ->
            WindowUtil.openWindows.add(hWnd)

            val rect = WindowUtil.scaleWindowRect(hWnd)

            val internalBodyList = mutableListOf<Body>()
            BorderUtil.createAllWindowBorders(internalBodyList, PhysicsUtil.world, rect.x, rect.y, rect.width, rect.height)

            HangerChan.windowList.add(NativeWindow(
                    hWnd,
                    Win32WindowUtil.getRect(hWnd),
                    Win32WindowUtil.getRect(hWnd),
                    WindowUtil.createBody(rect),
                    internalBodyList)
            )
        }
    }

    override fun trigger(t: WinDef.HWND) {
        logger.debug("Opened ${Win32WindowUtil.getTitle(t)}")
        super.trigger(t)
    }
}
