/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.util

import com.deflatedpickle.hangerchan.HangerChan
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.World

object BorderUtil {
    fun createAllBorders(hangerChan: HangerChan, world: World) {
        this.createTopBorder(hangerChan, world, MonitorUtil.monitorWidth)
        this.createBottomBorder(hangerChan, world, MonitorUtil.monitorWidth, MonitorUtil.monitorHeight)
        this.createLeftBorder(hangerChan, world, MonitorUtil.monitorHeight)
        this.createRightBorder(hangerChan, world, MonitorUtil.monitorWidth, MonitorUtil.monitorHeight)
    }

    fun createTopBorder(hangerChan: HangerChan, world: World, width: Float) {
        hangerChan.borders.add(world.createBody(BodyDef().apply {
            position.set(width / 2, 1f)
        }).apply {
            createFixture(PolygonShape().apply {
                setAsBox(width / 2, 1f)
            }, 0f)
        })
    }

    fun createBottomBorder(hangerChan: HangerChan, world: World, width: Float, height: Float) {
        hangerChan.borders.add(world.createBody(BodyDef().apply {
            position.set(width / 2, -height - 1)
        }).apply {
            createFixture(PolygonShape().apply {
                setAsBox(width / 2, 1f)
            }, 0f)
        })
    }

    fun createLeftBorder(hangerChan: HangerChan, world: World, height: Float) {
        hangerChan.borders.add(world.createBody(BodyDef().apply {
            position.set(-1f, -height / 2)
        }).apply {
            createFixture(PolygonShape().apply {
                setAsBox(1f, height / 2)
            }, 0f)
        })
    }

    fun createRightBorder(hangerChan: HangerChan, world: World, width: Float, height: Float) {
        hangerChan.borders.add(world.createBody(BodyDef().apply {
            position.set(width + 1f, -height / 2)
        }).apply {
            createFixture(PolygonShape().apply {
                setAsBox(1f, height / 2)
            }, 0f)
        })
    }
}
