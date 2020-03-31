/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.event

import com.deflatedpickle.hangerchan.NativeWindow
import com.deflatedpickle.hangerchan.util.WindowUtil
import com.deflatedpickle.hangerchan.util.win32.Win32WindowUtil

object WindowMoveEvent : AbstractEvent<NativeWindow>() {
    init {
        WindowMoveEvent.addListener { nativeWindow ->
            val scaledRect = WindowUtil.scaleRECT(nativeWindow.newUnits)
            nativeWindow.moveTo(scaledRect.x, scaledRect.y, scaledRect.width, scaledRect.height)
            nativeWindow.lastUnits = nativeWindow.newUnits
        }
    }

    override fun trigger(t: NativeWindow) {
        logger.debug("Moved ${Win32WindowUtil.getTitle(t.hWnd)}")
        super.trigger(t)
    }
}
