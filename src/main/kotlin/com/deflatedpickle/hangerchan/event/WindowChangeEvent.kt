/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.event

import com.deflatedpickle.hangerchan.NativeWindow
import com.deflatedpickle.hangerchan.util.win32.Win32WindowUtil

object WindowChangeEvent : AbstractEvent<NativeWindow>() {
    init {
        WindowChangeEvent.addListener {
        }
    }

    override fun trigger(t: NativeWindow) {
        logger.debug("Changed to ${Win32WindowUtil.getTitle(t.hWnd)}")
        super.trigger(t)
    }
}
