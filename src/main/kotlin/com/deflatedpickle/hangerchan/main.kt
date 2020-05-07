/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan

import com.deflatedpickle.hangerchan.event.WindowChangeEvent
import com.deflatedpickle.hangerchan.event.WindowCloseEvent
import com.deflatedpickle.hangerchan.event.WindowMoveEvent
import com.deflatedpickle.hangerchan.event.WindowOpenEvent
import com.deflatedpickle.hangerchan.util.WindowUtil
import com.deflatedpickle.hangerchan.util.physics.BorderUtil
import com.deflatedpickle.hangerchan.util.physics.PhysicsUtil
import com.deflatedpickle.hangerchan.util.win32.CursorUtil
import com.deflatedpickle.hangerchan.util.win32.Win32WindowUtil
import com.deflatedpickle.jna.WinUserExtended
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import java.awt.event.ActionListener
import javax.swing.Timer
import org.apache.logging.log4j.LogManager

@Suppress("KDocMissingDocumentation")
fun main() {
    System.setProperty("log4j.skipJansi", "false")
    val logger = LogManager.getLogger("Main")

    // Creates and starts a new thread for native events
    val nativeEventsThread = Thread({
        // Creates a hook to listen for native events
        val event = User32.INSTANCE.SetWinEventHook(
                WinUserExtended.EVENT_MIN,
                WinUserExtended.EVENT_MAX,
                null,
                { _, event, hwnd,
                  idObject, idChild,
                  dwEventThread, dwmsEventTime ->
                    hwnd?.let {
                        WindowUtil.findNativeWindowForHWND(hwnd)?.let {
                            var output = true
                            // High is always 0, so just test the low
                            when (event.low.toInt()) {
                                WinUserExtended.EVENT_OBJECT_CREATE -> {
                                    // TODO: Object create events are not fired for windows opening, figure out why
                                }
                                WinUserExtended.EVENT_OBJECT_HIDE,
                                WinUserExtended.EVENT_SYSTEM_CAPTURESTART,
                                WinUserExtended.EVENT_SYSTEM_CAPTUREEND -> {
                                    WindowUtil.oldActiveWindow = WindowUtil.activeWindow
                                    WindowUtil.activeWindow = it.hWnd
                                    WindowChangeEvent.trigger(it)
                                }
                                WinUserExtended.EVENT_OBJECT_LOCATIONCHANGE -> {
                                    it.newUnits = Win32WindowUtil.getRect(hwnd)
                                    WindowMoveEvent.trigger(it)
                                }
                                WinUserExtended.EVENT_OBJECT_DESTROY -> {
                                    WindowCloseEvent.trigger(it)
                                }
                                else -> output = false
                            }

                            if (output) {
                                logger.debug("An event was sent with the code $event from $dwEventThread, which is linked with ${Win32WindowUtil.getTitle(hwnd)} at $dwmsEventTime")
                            }
                        }
                    }
                }, 0, 0,
                WinUserExtended.WINEVENT_OUTOFCONTEXT or WinUserExtended.WINEVENT_SKIPOWNPROCESS)
        if (event == null) {
            logger.warn("The native event hook failed to register")
        } else {
            logger.info("Registered the native event hook")
        }

        // Runs a message loop so the hook works
        // FIXME: Sometimes this throws Invalid memory access
        val message: WinUser.MSG = WinUser.MSG()
        while (User32.INSTANCE.GetMessage(message, null, 0, 0) != 0) {
            User32.INSTANCE.TranslateMessage(message)
            User32.INSTANCE.DispatchMessage(message)
        }
    }, "NativeEvents")
    logger.info("Spawned the native event thread")

    ApplicationWindow

    logger.info("Constructed the window")

    // 1.524
    // What do those numbers mean? I left that comment over a year ago with no context (16/03/2020)
    HangerChan
    logger.debug("Constructed Hanger-chan using the world")
    ApplicationWindow.add(HangerChan)
    logger.info("Added the Hanger-chan widget to the window")

    PhysicsUtil.world.setContactListener(ContactAdapter)
    logger.debug("Set the worlds collision listener")

    // Cursor
    Cursor
    logger.debug("Constructed the cursor body")

    ApplicationWindow.isVisible = true
    logger.debug("Made the window visible")

    BorderUtil.createAllMonitorBorders(HangerChan.borders, PhysicsUtil.world)
    logger.debug("Created monitor borders")

    var counter = 0
    val timer = Timer(1000 / 144 * 3, ActionListener {
        counter++

        // Check if there are any new windows
        if (counter % 12 == 0) {
            for (hWnd in Win32WindowUtil.getAllWindows(0)) {
                if (Win32WindowUtil.getTitle(hWnd) !in WindowUtil.programBlacklist &&
                        Win32WindowUtil.getTitle(hWnd) != ApplicationWindow.title &&
                        !HangerChan.windowList.any { it.hWnd == hWnd }) {
                    WindowOpenEvent.trigger(hWnd)
                }
            }
        }

        if (counter % 3 == 0) {
            PhysicsUtil.world.step(1f / 60f, 1, 1)
            // logger.info("Increased the PhysicsUtil.world step")
            HangerChan.animate()
        }

        if (counter % 12 == 0) {
            val windowHWNDList = mutableListOf<WinDef.HWND>()
            val windowRectList = mutableListOf<WinDef.RECT>()
            for (hWnd in Win32WindowUtil.getAllWindowsByTop(0)) {
                windowHWNDList.add(hWnd)
                windowRectList.add(Win32WindowUtil.getRect(hWnd))
            }

            // Get each window rect, from the bottom to the top
            for ((index, rect) in windowRectList.reversed().withIndex()) {
                val nativeWindow = WindowUtil.findNativeWindowForHWND(windowHWNDList.reversed()[index], HangerChan.windowList)

                nativeWindow?.let {
                    if (WindowUtil.isFullyCovered(rect, windowRectList.reversed())) {
                        nativeWindow.fullBody.isActive = false

                        for (i in nativeWindow.splitBodyList) {
                            i.isActive = false
                        }
                    } else {
                        nativeWindow.fullBody.isActive =
                                HangerChan.embeddedWindow !=
                                        nativeWindow.hWnd

                        for (i in nativeWindow.splitBodyList) {
                            i.isActive =
                                    HangerChan.embeddedWindow !=
                                            nativeWindow.hWnd
                        }
                    }
                }
            }
        }

        if (counter % 8 == 0) {
            for (hWnd in WindowUtil.openWindows) {
                if (User32.INSTANCE.IsWindowVisible(hWnd)) {
                    // TODO: Disable collision boxes of non-visible windows
                }
            }
        }

        CursorUtil.update(HangerChan)
        HangerChan.repaint()
    })
    timer.start()
    logger.info("Started the animation and window detection timer")

    nativeEventsThread.start()
}
