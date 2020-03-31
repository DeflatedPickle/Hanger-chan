/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.event

import com.deflatedpickle.hangerchan.HangerChan
import com.deflatedpickle.hangerchan.NativeWindow
import com.deflatedpickle.hangerchan.util.win32.Win32WindowUtil

object WindowCloseEvent : AbstractEvent<NativeWindow>() {
    init {
        WindowCloseEvent.addListener { nativeWindow ->
            HangerChan.windowList.remove(nativeWindow)
        }
    }

    override fun trigger(t: NativeWindow) {
        logger.debug("Opened ${Win32WindowUtil.getTitle(t.hWnd)}")
        super.trigger(t)
    }
}
