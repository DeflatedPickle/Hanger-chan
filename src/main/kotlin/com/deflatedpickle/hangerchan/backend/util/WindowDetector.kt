/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.backend.util

import com.deflatedpickle.hangerchan.backend.NativeWindow
import com.deflatedpickle.hangerchan.backend.util.win32.Win32WindowUtil
import com.deflatedpickle.hangerchan.frontend.Window

object WindowDetector : Runnable {
    private val programBlacklist = listOf("Settings", "Microsoft Store", "Photos", "Films & TV", "Groove Music")

    val windowList = mutableListOf<NativeWindow>()

    override fun run() {
        this.firstTimeFill()

        while (Window.isVisible) {
            this.checkAndAdd()
        }
    }

    private fun firstTimeFill() {
        for (window in Win32WindowUtil.getAllWindows(0)) {
            if (Win32WindowUtil.getTitle(window) !in this.programBlacklist) {
                windowList.add(
                        NativeWindow(
                                hWnd = window
                        )
                )
            }
        }
    }

    private fun checkAndAdd() {
        //
    }
}
