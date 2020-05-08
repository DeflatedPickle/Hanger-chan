/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.util

import com.deflatedpickle.hangerchan.HangerChan
import com.deflatedpickle.hangerchan.NativeWindow
import com.deflatedpickle.hangerchan.extensions.isInsideX
import com.deflatedpickle.hangerchan.extensions.isInsideY
import com.deflatedpickle.hangerchan.util.physics.PhysicsUtil
import com.deflatedpickle.hangerchan.util.win32.MonitorUtil
import com.deflatedpickle.hangerchan.util.win32.Win32WindowUtil
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef

object WindowUtil {
    val openWindows = mutableListOf<WinDef.HWND>()

    var oldActiveWindow: WinDef.HWND? = User32.INSTANCE.GetForegroundWindow()
    var activeWindow: WinDef.HWND? = null

    // I think these programs open on start-up and fail the window check, even when you haven't used them
    // So they have a window border, so
    // TODO: Figure out why these programs behave like this and find more examples that act like this
    // More examples might help to find out why they behave like this
    val programBlacklist = listOf("Settings", "Microsoft Store", "Photos", "Films & TV", "Groove Music")

    class Rect(val x: Float, val y: Float, val width: Float, val height: Float)
    class Point(var x: Int, var y: Int) {
        // That wasn't very cache data of you
        private val cacheX = x
        private val cacheY = y

        fun resetPosition(): Point = this.setPosition(cacheX, cacheY)

        fun setPosition(x: Int, y: Int): Point = this.apply {
            this.x = x
            this.y = y
        }
    }

    fun scaleWindowRect(hWnd: WinDef.HWND): Rect {
        return scaleRECT(Win32WindowUtil.getRect(hWnd))
    }

    fun scaleRECT(rect: WinDef.RECT): Rect {
        val x = PhysicsUtil.guiToPhysics(rect.left)
        val y = PhysicsUtil.guiToPhysics(rect.top)
        val width = PhysicsUtil.guiToPhysics(rect.right) - x
        val height = PhysicsUtil.guiToPhysics(rect.bottom) - y

        return Rect(x, y, width, height)
    }

    fun findNativeWindowForHWND(hWnd: WinDef.HWND, list: List<NativeWindow>): NativeWindow? {
        var nativeWindow: NativeWindow? = null

        for (i in list) {
            if (i.hWnd == hWnd) {
                nativeWindow = i
                break
            }
        }

        return nativeWindow
    }

    fun findNativeWindowForHWND(hWnd: WinDef.HWND): NativeWindow? = this.findNativeWindowForHWND(hWnd, HangerChan.windowList)

    fun createBody(rect: Rect): Body =
            PhysicsUtil.world.createBody(BodyDef().apply {
                position.set(rect.x + rect.width / 2, -rect.y - rect.height / 2)
            }).apply {
                createFixture(PolygonShape().apply {
                    setAsBox(rect.width / 2, rect.height / 2)
                }, 0f)
            }

    fun createBox(x: Float, y: Float, width: Float, height: Float): Body =
            PhysicsUtil.world.createBody(BodyDef().apply {
                position.set(x + width / 2, -y - height / 2)
            }).apply {
                createFixture(PolygonShape().apply {
                    setAsBox(width / 2, height / 2)
                }, 0f)
            }

    fun isFullyCovered(rect: WinDef.RECT, list: List<WinDef.RECT>): Boolean {
        for (loopRect in list.dropLast(1)) {
            if (loopRect.toRectangle().contains(rect.toRectangle())) {
                if (list.indexOf(loopRect) < list.indexOf(rect)) {
                    continue
                } else {
                    return true
                }
            }
        }

        return false
    }

    fun isPartiallyCovered(rect: WinDef.RECT, list: List<WinDef.RECT>): Boolean {
        for (loopRect in list.dropLast(1)) {
            if (loopRect.toRectangle().intersects(rect.toRectangle())) {
                if (list.indexOf(loopRect) < list.indexOf(rect)) {
                    continue
                } else {
                    return true
                }
            }
        }

        return false
    }

    fun findUncoveredPoint(startX: Int, startY: Int, endX: Int, endY: Int, chunkSize: Int, winList: List<WinDef.RECT>): Point {
        var x = 0
        var y = 0

        // Move left, doing the same
        for (loopX in 0..MonitorUtil.monitorHeight step chunkSize) {
            // Check if they cover this X or Y
            for (rect in winList) {
                if (rect.isInsideX(loopX)) {
                    x = loopX
                    break
                }
            }
        }

        // Loop *all* the window bounds
        for (loopY in (0..MonitorUtil.monitorWidth step chunkSize)) {
            for (rect in winList) {
                if (rect.isInsideY(loopY)) {
                    y = loopY
                    break
                }
            }
        }

        return Point(x, y)
    }
}
