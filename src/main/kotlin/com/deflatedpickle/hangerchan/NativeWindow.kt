/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan

import com.sun.jna.platform.win32.WinDef
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body

class NativeWindow(
        val hWnd: WinDef.HWND,
        var lastUnits: WinDef.RECT,
        var newUnits: WinDef.RECT,
        val body: Body,
        val internalBodyList: MutableList<Body>
) {
    fun moveTo(x: Float, y: Float, width: Float, height: Float) {
        this.body.setTransform(Vec2(x + width / 2, -y - height / 2), 0f)
        (this.body.fixtureList.shape as PolygonShape).setAsBox(width / 2, height / 2)

        this.internalBodyList[0].setTransform(Vec2(x, -y - height / 2), 0f)
        (this.internalBodyList[0].fixtureList.shape as PolygonShape).setAsBox(0f, height / 2)

        this.internalBodyList[1].setTransform(Vec2(x + width, -y - height / 2), 0f)
        (this.internalBodyList[1].fixtureList.shape as PolygonShape).setAsBox(0f, height / 2)

        this.internalBodyList[2].setTransform(Vec2(x + width / 2, -y), 0f)
        (this.internalBodyList[2].fixtureList.shape as PolygonShape).setAsBox(width / 2, 0f)

        this.internalBodyList[3].setTransform(Vec2(x + width / 2, -y - height), 0f)
        (this.internalBodyList[3].fixtureList.shape as PolygonShape).setAsBox(width / 2, 0f)
    }
}
