/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan

import com.deflatedpickle.hangerchan.util.BorderUtil
import com.deflatedpickle.hangerchan.util.CursorUtil
import com.deflatedpickle.hangerchan.util.PhysicsUtil
import com.deflatedpickle.hangerchan.util.WindowUtil
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import java.awt.event.ActionListener
import javax.swing.Timer
import org.apache.logging.log4j.LogManager
import org.jbox2d.callbacks.ContactImpulse
import org.jbox2d.callbacks.ContactListener
import org.jbox2d.collision.Manifold
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.contacts.Contact

@Suppress("KDocMissingDocumentation")
fun main() {
    System.setProperty("log4j.skipJansi", "false")
    val logger = LogManager.getLogger("Main")
    val window = ApplicationWindow

    logger.info("Launched Hanger-chan")

    // 1.524
    // What do those numbers mean? I left that comment over a year ago with no context (16/03/2020)
    val hangerChan = HangerChan(PhysicsUtil.world)
    logger.debug("Constructed Hanger-chan using the world")
    window.add(hangerChan)
    logger.info("Added the Hanger-chan widget to the window")

    var collisionPoint = Vec2()
    val collisions = object : ContactListener {
        override fun endContact(contact: Contact) {
            when (collisionPoint.y) {
                -1f -> {
                    hangerChan.onGround = contact.isTouching
                    logger.debug("Hanger-chan left the ground")
                }
            }
        }

        override fun beginContact(contact: Contact) {
            when (collisionPoint.y) {
                -1f -> {
                    hangerChan.onGround = contact.isTouching
                    logger.debug("Hanger-chan hit the ground")
                }
            }
        }

        override fun preSolve(contact: Contact, oldManifold: Manifold) {
            if (oldManifold.localNormal.x != 0f &&
                    collisionPoint != oldManifold.localNormal) {
                collisionPoint = oldManifold.localNormal
                logger.debug("Hanger-chan collided on $collisionPoint")
            }
        }

        override fun postSolve(contact: Contact, impulse: ContactImpulse) {
            hangerChan.collisionSide = collisionPoint
        }
    }
    PhysicsUtil.world.setContactListener(collisions)
    logger.debug("Added the collision listener")

    // Cursor
    val cursorBody = CursorUtil.createBody(PhysicsUtil.world)
    logger.debug("Created the cursor body")
    CursorUtil.body = cursorBody
    hangerChan.cursor = cursorBody
    var counter = 0
    val openWindows = mutableListOf<WinDef.HWND>()
    val timer = Timer(1000 / 144 * 4, ActionListener {
        counter++

        // Check if there are any new windows
        if (counter % 12 == 0) {
            for (hWnd in WindowUtil.getAllWindows(0)) {
                if (WindowUtil.getTitle(hWnd) !in WindowUtil.annoyingPrograms &&
                        !hangerChan.windowList.any { it.hwnd == hWnd }) {
                    logger.info("Added ${WindowUtil.getTitle(hWnd)} to Hanger-chan's windows")
                    openWindows.add(hWnd)

                    val rect = WinDef.RECT()
                    User32.INSTANCE.GetWindowRect(hWnd, rect)

                    val x = rect.left.toFloat() * PhysicsUtil.scaleDown
                    val y = rect.top.toFloat() * PhysicsUtil.scaleDown
                    val width = (rect.right.toFloat() * PhysicsUtil.scaleDown) - x
                    val height = (rect.bottom.toFloat() * PhysicsUtil.scaleDown) - y

                    // println("X: $x, Y: $y, Width: $width, Height: $height")

                    val body = PhysicsUtil.world.createBody(BodyDef().apply {
                        position.set(x + width / 2, -y - height / 2)
                    }).apply {
                        createFixture(PolygonShape().apply {
                            setAsBox(width / 2, height / 2)
                        }, 0f)
                    }

                    val internalBodyList = mutableListOf<Body>()
                    BorderUtil.createAllBorders(internalBodyList, PhysicsUtil.world)

                    hangerChan.windowList.add(NativeWindow(hWnd, rect, body, internalBodyList))
                }
            }

            for (i in openWindows) {
                if (WindowUtil.getTitle(i) == "" && hangerChan.windowList.map { it.hwnd }.contains(i)) {
                    logger.info("Removed a window from Hanger-chan's windows")

                    for (w in hangerChan.windowList) {
                        if (w.hwnd == i) {
                            hangerChan.windowList.remove(w)
                        }
                    }
                }
            }
        }

        if (counter % 3 == 0) {
            PhysicsUtil.world.step(1f / 60f, 1, 1)
            // logger.info("Increased the PhysicsUtil.world step")

            hangerChan.animate()
        }

        for (nativeWindow in hangerChan.windowList) {
            val rect = WinDef.RECT()
            User32.INSTANCE.GetWindowRect(nativeWindow.hwnd, rect)

            // Check if the positions are the same before moving the collision box
            // Occasionally fails, leaving the box far away from the window, don't know why
            if (nativeWindow.lastUnits.top != rect.top && nativeWindow.lastUnits.bottom != rect.bottom && nativeWindow.lastUnits.left != rect.left && nativeWindow.lastUnits.right != rect.right) {
                val x = rect.left.toFloat() * PhysicsUtil.scaleDown
                val y = rect.top.toFloat() * PhysicsUtil.scaleDown
                val width = (rect.right.toFloat() * PhysicsUtil.scaleDown) - x
                val height = (rect.bottom.toFloat() * PhysicsUtil.scaleDown) - y

                nativeWindow.body.setTransform(Vec2(x + width / 2, -y - height / 2), 0f)
                (nativeWindow.body.fixtureList.shape as PolygonShape).setAsBox(width / 2, height / 2)

                nativeWindow.internalBodyList[0].setTransform(Vec2(x, -y - height / 2), 0f)
                (nativeWindow.internalBodyList[0].fixtureList.shape as PolygonShape).setAsBox(0f, height / 2)

                nativeWindow.internalBodyList[1].setTransform(Vec2(x + width, -y - height / 2), 0f)
                (nativeWindow.internalBodyList[1].fixtureList.shape as PolygonShape).setAsBox(0f, height / 2)

                nativeWindow.internalBodyList[2].setTransform(Vec2(x + width / 2, -y), 0f)
                (nativeWindow.internalBodyList[2].fixtureList.shape as PolygonShape).setAsBox(width / 2, 0f)

                nativeWindow.internalBodyList[3].setTransform(Vec2(x + width / 2, -y - height), 0f)
                (nativeWindow.internalBodyList[3].fixtureList.shape as PolygonShape).setAsBox(width / 2, 0f)

                nativeWindow.lastUnits = rect

                logger.info("Repositioned the body for ${WindowUtil.getTitle(nativeWindow.hwnd)}")
            }
        }

        CursorUtil.update(hangerChan)

        hangerChan.repaint()
    })
    timer.start()
    logger.info("Started the animation and window detection timer")

    window.pack()
    window.isVisible = true
    logger.debug("Made the window visible")

    BorderUtil.createAllBorders(hangerChan.borders, PhysicsUtil.world)
    logger.debug("Created monitor borders")
}
