/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan

import com.deflatedpickle.hangerchan.event.WindowCloseEvent
import com.deflatedpickle.hangerchan.event.WindowMoveEvent
import com.deflatedpickle.hangerchan.event.WindowOpenEvent
import com.deflatedpickle.hangerchan.util.WindowUtil
import com.deflatedpickle.hangerchan.util.physics.BorderUtil
import com.deflatedpickle.hangerchan.util.physics.PhysicsUtil
import com.deflatedpickle.hangerchan.util.win32.CursorUtil
import com.deflatedpickle.hangerchan.util.win32.Win32WindowUtil
import java.awt.event.ActionListener
import javax.swing.Timer
import org.apache.logging.log4j.LogManager

@Suppress("KDocMissingDocumentation")
fun main() {
    System.setProperty("log4j.skipJansi", "false")
    val logger = LogManager.getLogger("Main")
    ApplicationWindow

    logger.info("Launched Hanger-chan")

    // 1.524
    // What do those numbers mean? I left that comment over a year ago with no context (16/03/2020)
    HangerChan
    logger.debug("Constructed Hanger-chan using the world")
    ApplicationWindow.add(HangerChan)
    logger.info("Added the Hanger-chan widget to the window")

    PhysicsUtil.world.setContactListener(ContactAdapter)
    logger.debug("Added the collision listener")

    // Cursor
    Cursor
    logger.debug("Created the cursor body")

    var counter = 0
    val timer = Timer(1000 / 144 * 4, ActionListener {
        counter++

        // Check if there are any new windows
        if (counter % 12 == 0) {
            for (hWnd in Win32WindowUtil.getAllWindows(0)) {
                if (Win32WindowUtil.getTitle(hWnd) !in WindowUtil.annoyingPrograms &&
                        Win32WindowUtil.getTitle(hWnd) != ApplicationWindow.title &&
                        !HangerChan.windowList.any { it.hWnd == hWnd }) {
                    WindowOpenEvent.trigger(hWnd)
                }
            }

            for (i in WindowUtil.openWindows) {
                if (Win32WindowUtil.getTitle(i).isEmpty() && HangerChan.windowList.map { it.hWnd }.contains(i)) {

                    for (w in HangerChan.windowList) {
                        if (w.hWnd == i) {
                            WindowCloseEvent.trigger(w)
                        }
                    }
                }
            }
        }

        if (counter % 3 == 0) {
            PhysicsUtil.world.step(1f / 60f, 1, 1)
            // logger.info("Increased the PhysicsUtil.world step")

            HangerChan.animate()
        }

        for (nativeWindow in HangerChan.windowList) {
            val rect = Win32WindowUtil.getRect(nativeWindow.hWnd)

            // Check if the positions are the same before moving the collision box
            // Occasionally fails, leaving the box far away from the window, don't know why
            if (nativeWindow.lastUnits.top != rect.top && nativeWindow.lastUnits.bottom != rect.bottom && nativeWindow.lastUnits.left != rect.left && nativeWindow.lastUnits.right != rect.right) {
                nativeWindow.newUnits = rect
                WindowMoveEvent.trigger(nativeWindow)
            }
        }

        CursorUtil.update(HangerChan)
        HangerChan.repaint()
    })
    timer.start()
    logger.info("Started the animation and window detection timer")

    ApplicationWindow.isVisible = true
    logger.debug("Made the window visible")

    BorderUtil.createAllMonitorBorders(HangerChan.borders, PhysicsUtil.world)
    logger.debug("Created monitor borders")
}
