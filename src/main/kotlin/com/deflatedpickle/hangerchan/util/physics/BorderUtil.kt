/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.util.physics

import com.deflatedpickle.hangerchan.extensions.toPhysics
import com.deflatedpickle.hangerchan.util.win32.MonitorUtil
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.World

object BorderUtil {
    fun createAllMonitorBorders(borders: MutableList<Body>, world: World) {
        createTopMonitorBorder(borders, world, MonitorUtil.monitorWidth.toFloat())
        createBottomMonitorBorder(borders, world, MonitorUtil.monitorWidth.toFloat(), MonitorUtil.monitorHeight.toFloat())
        createLeftMonitorBorder(borders, world, MonitorUtil.monitorHeight.toFloat())
        createRightMonitorBorder(borders, world, MonitorUtil.monitorWidth.toFloat(), MonitorUtil.monitorHeight.toFloat())
    }

    fun createTopMonitorBorder(borders: MutableList<Body>, world: World, width: Float) {
        borders.add(world.createBody(BodyDef().apply {
            position.set((width / 2).toPhysics(), 1f)
        }).apply {
            createFixture(PolygonShape().apply {
                setAsBox((width / 2).toPhysics(), 1f)
            }, 0f)
        })
    }

    fun createBottomMonitorBorder(borders: MutableList<Body>, world: World, width: Float, height: Float) {
        borders.add(world.createBody(BodyDef().apply {
            position.set((width / 2).toPhysics(), -height.toPhysics() - 1)
        }).apply {
            createFixture(PolygonShape().apply {
                setAsBox((width / 2).toPhysics(), 1f)
            }, 0f)
        })
    }

    fun createLeftMonitorBorder(borders: MutableList<Body>, world: World, height: Float) {
        borders.add(world.createBody(BodyDef().apply {
            position.set(-1f, (-height / 2).toPhysics())
        }).apply {
            createFixture(PolygonShape().apply {
                setAsBox(1f, (height / 2).toPhysics())
            }, 0f)
        })
    }

    fun createRightMonitorBorder(borders: MutableList<Body>, world: World, width: Float, height: Float) {
        borders.add(world.createBody(BodyDef().apply {
            position.set(width.toPhysics() + 1f, (-height / 2).toPhysics())
        }).apply {
            createFixture(PolygonShape().apply {
                setAsBox(1f, (height / 2).toPhysics())
            }, 0f)
        })
    }

    fun createAllWindowBorders(borders: MutableList<Body>, world: World, x: Float, y: Float, width: Float, height: Float) {
        createTopWindowBorder(borders, world, x, y, height, width)
        createBottomWindowBorder(borders, world, x, y, width, height)
        createLeftWindowBorder(borders, world, x, y, width, height)
        createRightWindowBorder(borders, world, x, y, width, height)
    }

    fun createTopWindowBorder(borders: MutableList<Body>, world: World, x: Float, y: Float, height: Float, width: Float) {
        borders.add(world.createBody(BodyDef().apply {
            position.set(x + width / 2, -y)
        }).apply {
            isActive = false
            createFixture(PolygonShape().apply {
                setAsBox(width / 2, 0f)
            }, 0f)
        })
    }

    fun createBottomWindowBorder(borders: MutableList<Body>, world: World, x: Float, y: Float, width: Float, height: Float) {
        borders.add(world.createBody(BodyDef().apply {
            position.set(x + width / 2, -y - height)
        }).apply {
            isActive = false
            createFixture(PolygonShape().apply {
                setAsBox(width / 2, 0f)
            }, 0f)
        })
    }

    fun createLeftWindowBorder(borders: MutableList<Body>, world: World, x: Float, y: Float, width: Float, height: Float) {
        borders.add(world.createBody(BodyDef().apply {
            position.set(x, -y - height / 2)
        }).apply {
            isActive = false
            createFixture(PolygonShape().apply {
                setAsBox(0f, height / 2)
            }, 0f)
        })
    }

    fun createRightWindowBorder(borders: MutableList<Body>, world: World, x: Float, y: Float, width: Float, height: Float) {
        borders.add(world.createBody(BodyDef().apply {
            position.set(x + width, -y - height / 2)
        }).apply {
            isActive = false
            createFixture(PolygonShape().apply {
                setAsBox(0f, height / 2)
            }, 0f)
        })
    }
}
