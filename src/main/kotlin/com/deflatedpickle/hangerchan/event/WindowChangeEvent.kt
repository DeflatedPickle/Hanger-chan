/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.event

import com.deflatedpickle.hangerchan.util.win32.Win32WindowUtil
import com.sun.jna.platform.win32.WinDef

object WindowChangeEvent : AbstractEvent<WinDef.HWND?>() {
    init {
        WindowChangeEvent.addListener {
        }
    }

    override fun trigger(t: WinDef.HWND?) {
        logger.debug("Changed to ${if (t != null) Win32WindowUtil.getTitle(t) else "null" }")
        super.trigger(t)
    }
}
