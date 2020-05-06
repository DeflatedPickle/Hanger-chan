/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.util.physics

import com.deflatedpickle.hangerchan.NativeWindow
import com.deflatedpickle.hangerchan.util.win32.Win32WindowUtil
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import org.apache.logging.log4j.LogManager
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.World

object PhysicsUtil {
    // *claw* RAWR *claw* UWU
    private const val scaleUp = 20f
    private const val scaleDown = 1 / scaleUp

    val world = World(Vec2(0f, -80f))

    private val logger = LogManager.getLogger(PhysicsUtil::class.simpleName)

    init {
        logger.debug("Constructed the world with gravity vector of { ${world.gravity.x}, ${world.gravity.y} }")
    }

    fun physicsToGui(value: Float) = value * scaleUp
    fun guiToPhysics(value: Int) = guiToPhysics(value.toFloat())
    fun guiToPhysics(value: Float) = value * scaleDown

    fun drawPhysicsShape(graphics2D: Graphics2D, body: Body) {
        val shape = (body.fixtureList.shape as PolygonShape)
        val vertices = shape.vertices
        for (i in vertices) {
            val next: Vec2 = if (vertices.indexOf(i) < shape.vertexCount - 1) {
                vertices[vertices.indexOf(i) + 1]
            } else {
                vertices[0]
            }

            val x = body.transform.p.x
            val y = body.transform.p.y
            graphics2D.drawLine(
                    ((i.x + x) * scaleUp).toInt(),
                    ((i.y - y) * scaleUp).toInt(),
                    ((next.x + x) * scaleUp).toInt(),
                    ((next.y - y) * scaleUp).toInt())

            if (next == vertices[0]) {
                break
            }
        }
    }

    fun drawText(graphics2D: Graphics2D, string: String, body: Body, xIncrease: Float = 0f, yIncrease: Float = 0f) {
        val shape = (body.fixtureList.shape as PolygonShape)
        val vertices = shape.vertices

        val x = body.transform.p.x
        val y = body.transform.p.y

        val originalFont = graphics2D.font
        val originalColour = graphics2D.color
        graphics2D.font = Font(originalFont.fontName, Font.BOLD, 14)

        val finalX = (vertices[0].x + x + xIncrease) * scaleUp
        val finalY = (vertices[0].y - y + 1 + yIncrease) * scaleUp
        val width = graphics2D.fontMetrics.stringWidth(string)
        val height = graphics2D.fontMetrics.height

        graphics2D.color = Color.BLACK
        graphics2D.fillRect(finalX.toInt(), finalY.toInt() - height, width + 12, height + (height / 2))
        graphics2D.color = Color.WHITE
        graphics2D.drawString(string, finalX + 6, finalY)

        graphics2D.font = originalFont
        graphics2D.color = originalColour
    }

    // Some windows pass through the filter and get bounding boxes without actually showing a window
    // This function helps find the windows that behave this way, so they can be added to the blacklist
    // It also draws the windows borders by calling "drawPhysicsShape"
    fun drawWindowShape(nativeWindow: NativeWindow, graphics2D: Graphics2D) {
        drawText(
                graphics2D,
                Win32WindowUtil.getTitle(nativeWindow.hWnd),
                nativeWindow.fullBody
        )

        graphics2D.color = Color.MAGENTA
        drawPhysicsShape(graphics2D, nativeWindow.fullBody)

        graphics2D.color = Color.ORANGE
        graphics2D.stroke = BasicStroke(6f)
        drawBodyList(graphics2D, nativeWindow)
    }

    fun drawBodyList(graphics2D: Graphics2D, nativeWindow: NativeWindow) {
        for (body in nativeWindow.splitBodyList) {
            drawText(
                    graphics2D,
                    "Belongs to ${Win32WindowUtil.getTitle(nativeWindow.hWnd)}",
                    body
            )

            drawPhysicsShape(graphics2D, body)
        }
    }

    fun drawWindowFill(graphics2D: Graphics2D, body: Body) {
        val shape = (body.fixtureList.shape as PolygonShape)
        val vertices = shape.vertices

        val x = body.transform.p.x
        val y = body.transform.p.y

        val topX = (vertices[0].x + x) * scaleUp
        val topY = (vertices[0].y - y) * scaleUp

        val width = (vertices[2].x * 2) * scaleUp
        val height = (vertices[2].y * 2) * scaleUp

        graphics2D.fillRect(topX.toInt(), topY.toInt(), width.toInt(), height.toInt())
    }
}
