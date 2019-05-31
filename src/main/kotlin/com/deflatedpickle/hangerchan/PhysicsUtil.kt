package com.deflatedpickle.hangerchan

import com.sun.jna.platform.win32.WinDef
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D

object PhysicsUtil {
    // *claw* RAWR *claw* UWU
    val scaleUp = 20f
    val scaleDown = 1 / scaleUp

    fun drawPhysicsShape(graphics2D: Graphics2D, body: Body) {
        val shape = (body.fixtureList.shape as PolygonShape)
        val vertices = shape.vertices
        for (i in vertices) {
            val next: Vec2 = if (vertices.indexOf(i) < shape.vertexCount - 1) {
                vertices[vertices.indexOf(i) + 1]
            }
            else {
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

    // Some windows pass through the the filter and get bounding boxes without actually showing a window
    // This function helps find the windows that behave this way so they can be added to the blacklist
    // It also draws the windows borders by calling "drawPhysicsShape"
    fun drawWindowShape(hwnd: WinDef.HWND, graphics2D: Graphics2D, body: Body) {
        val shape = (body.fixtureList.shape as PolygonShape)
        val vertices = shape.vertices

        val x = body.transform.p.x
        val y = body.transform.p.y

        val title = WindowUtil.getTitle(hwnd)

        val originalFont = graphics2D.font
        graphics2D.font = Font(originalFont.fontName, Font.BOLD, 14)

        val finalX = (vertices[0].x + x + 1) * scaleUp
        val finalY = (vertices[0].y - y + 1) * scaleUp
        val width = graphics2D.fontMetrics.stringWidth(title)
        val height = graphics2D.fontMetrics.height

        graphics2D.color = Color.BLACK
        graphics2D.fillRect(finalX.toInt(), finalY.toInt() - height, width + 12, height + (height / 2))
        graphics2D.color = Color.WHITE
        graphics2D.drawString(title, finalX + 6, finalY)

        graphics2D.font = originalFont

        graphics2D.color = Color.MAGENTA
        drawPhysicsShape(graphics2D, body)
    }
}