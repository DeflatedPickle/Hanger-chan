/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.util

import com.deflatedpickle.hangerchan.HangerChan
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.World

object BorderUtil {
    fun createAllBorders(borders: MutableList<Body>, world: World) {
        this.createTopBorder(borders, world, MonitorUtil.monitorWidth)
        this.createBottomBorder(borders, world, MonitorUtil.monitorWidth, MonitorUtil.monitorHeight)
        this.createLeftBorder(borders, world, MonitorUtil.monitorHeight)
        this.createRightBorder(borders, world, MonitorUtil.monitorWidth, MonitorUtil.monitorHeight)
    }

    fun createTopBorder(borders: MutableList<Body>, world: World, width: Float) {
        borders.add(world.createBody(BodyDef().apply {
            position.set(width / 2, 1f)
        }).apply {
            createFixture(PolygonShape().apply {
                setAsBox(width / 2, 1f)
            }, 0f)
        })
    }

    fun createBottomBorder(borders: MutableList<Body>, world: World, width: Float, height: Float) {
        borders.add(world.createBody(BodyDef().apply {
            position.set(width / 2, -height - 1)
        }).apply {
            createFixture(PolygonShape().apply {
                setAsBox(width / 2, 1f)
            }, 0f)
        })
    }

    fun createLeftBorder(borders: MutableList<Body>, world: World, height: Float) {
        borders.add(world.createBody(BodyDef().apply {
            position.set(-1f, -height / 2)
        }).apply {
            createFixture(PolygonShape().apply {
                setAsBox(1f, height / 2)
            }, 0f)
        })
    }

    fun createRightBorder(borders: MutableList<Body>, world: World, width: Float, height: Float) {
        borders.add(world.createBody(BodyDef().apply {
            position.set(width + 1f, -height / 2)
        }).apply {
            createFixture(PolygonShape().apply {
                setAsBox(1f, height / 2)
            }, 0f)
        })
    }
}
