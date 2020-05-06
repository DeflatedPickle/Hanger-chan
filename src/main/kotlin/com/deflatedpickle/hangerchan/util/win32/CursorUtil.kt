/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.util.win32

import com.deflatedpickle.hangerchan.Cursor
import com.deflatedpickle.hangerchan.HangerChan
import com.deflatedpickle.hangerchan.util.physics.PhysicsUtil
import com.deflatedpickle.jna.User32Extended
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.World

object CursorUtil {
    val cursorLocation = WinDef.POINT()

    val cursorWidth = User32.INSTANCE.GetSystemMetrics(User32.SM_CXCURSOR)
    val cursorHeight = User32.INSTANCE.GetSystemMetrics(User32.SM_CYCURSOR)

    init {
        User32.INSTANCE.GetCursorPos(cursorLocation)
    }

    fun update(hangerChan: HangerChan) {
        User32.INSTANCE.GetCursorPos(cursorLocation)

        // TODO: Get the size of the current cursor instead of the constant size
        val cursorWidth = User32.INSTANCE.GetSystemMetrics(User32.SM_CXCURSOR)
        val cursorHeight = User32.INSTANCE.GetSystemMetrics(User32.SM_CYCURSOR)
        Cursor.body.setTransform(Vec2(
                PhysicsUtil.guiToPhysics(cursorLocation.x.toFloat() + cursorWidth / 4),
                PhysicsUtil.guiToPhysics(-(cursorLocation.y.toFloat() + cursorHeight / 4))
        ), 0f)
        (Cursor.body.fixtureList.shape as PolygonShape).setAsBox(PhysicsUtil.guiToPhysics(cursorWidth / 2), PhysicsUtil.guiToPhysics(cursorHeight / 2))
        Cursor.body.isActive = User32.INSTANCE.GetAsyncKeyState(User32Extended.VK_LBUTTON) < 0 && !hangerChan.isGrabbed
    }

    fun createBody(world: World): Body =
            world.createBody(BodyDef().apply {
                position.set(cursorLocation.x + cursorWidth / 2f, -cursorLocation.y - cursorHeight / 2f)
            }).apply {
                createFixture(PolygonShape().apply {
                    setAsBox(cursorWidth / 2f, cursorHeight / 2f)
                }, 0f)
            }
}
