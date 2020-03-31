package com.deflatedpickle.hangerchan.util

import com.deflatedpickle.hangerchan.util.physics.PhysicsUtil
import com.deflatedpickle.hangerchan.util.win32.Win32WindowUtil
import com.sun.jna.platform.win32.WinDef
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef

object WindowUtil {
    val openWindows = mutableListOf<WinDef.HWND>()

    // I think these programs open on start-up and fail the window check, even when you haven't used them
    // So they have a window border, so
    // TODO: Figure out why these programs behave like this and find more examples that act like this
    // More examples might help to find out why they behave like this
    val annoyingPrograms = listOf("Settings", "Microsoft Store", "Photos", "Films & TV", "Groove Music")

    class Rect(val x: Float, val y: Float, val width: Float, val height: Float)

    fun scaleWindowRect(hWnd: WinDef.HWND): Rect {
        return scaleRECT(Win32WindowUtil.getRect(hWnd))
    }

    fun scaleRECT(rect: WinDef.RECT): Rect {
        val x = rect.left.toFloat() * PhysicsUtil.scaleDown
        val y = rect.top.toFloat() * PhysicsUtil.scaleDown
        val width = (rect.right.toFloat() * PhysicsUtil.scaleDown) - x
        val height = (rect.bottom.toFloat() * PhysicsUtil.scaleDown) - y

        return Rect(x, y, width, height)
    }

    fun createBody(rect: Rect): Body =
            PhysicsUtil.world.createBody(BodyDef().apply {
                position.set(rect.x + rect.width / 2, -rect.y - rect.height / 2)
            }).apply {
                createFixture(PolygonShape().apply {
                    setAsBox(rect.width / 2, rect.height / 2)
                }, 0f)
            }

}