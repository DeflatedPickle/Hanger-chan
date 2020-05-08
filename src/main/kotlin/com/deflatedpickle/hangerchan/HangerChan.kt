/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan

import com.deflatedpickle.hangerchan.extensions.isInside
import com.deflatedpickle.hangerchan.util.WindowUtil
import com.deflatedpickle.hangerchan.util.physics.PhysicsUtil
import com.deflatedpickle.hangerchan.util.win32.Win32WindowUtil
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import org.apache.logging.log4j.LogManager
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

object HangerChan : JPanel() {
    private val logger = LogManager.getLogger(HangerChan::class.simpleName)

    private val sheet = SpriteSheet("/hangerchan/Hangerchan", 8, 10)
    var currentAction = Action.Idle

    private var beingControlled = false

    val body: Body = PhysicsUtil.world.createBody(BodyDef().apply {
        type = BodyType.DYNAMIC
        position.set(20f, -10f)
        fixedRotation = true
    }).apply {
        createFixture(PolygonShape().apply {
            setAsBox(
                    PhysicsUtil.guiToPhysics(sheet.spriteWidth / 4f),
                    PhysicsUtil.guiToPhysics(sheet.spriteHeight / 4f)
            )
        }, 1f).apply {
            m_mass = 1f
            friction = 0.3f
            density = 1f
        }
    }

    var borders: MutableList<Body> = mutableListOf()
    var windowList: MutableList<NativeWindow> = mutableListOf()

    var isEmbedded = false
    var embeddedWindow: WinDef.HWND = User32.INSTANCE.GetDesktopWindow()

    // -1 = Left, 1 = Right
    var direction = -1
    // A cooldown that happens after the action is changed
    var graceCoolDown = 30
    private var currentFrame = 0

    var collisionSide: Vec2? = null

    // Mouse events
    var isGrabbed = false
    var isBeingPulled = false

    private var justFell = false

    var onGround = false

    init {
        isOpaque = false

        ApplicationWindow.addMouseListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                ApplicationWindow.cursor = java.awt.Cursor.getPredefinedCursor(
                        if (isInside()) {
                            java.awt.Cursor.HAND_CURSOR
                        } else {
                            java.awt.Cursor.DEFAULT_CURSOR
                        }
                )
            }

            override fun mousePressed(e: MouseEvent) {
                // If inside Hanger-chan
                if (isInside()) {
                    isGrabbed = true

                    Cursor.clickedPoint.set(e.xOnScreen, e.yOnScreen)
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                if (isGrabbed) {
                    Cursor.releasedPoint.set(e.xOnScreen, e.yOnScreen)

                    // TODO: Drag over desktop to reset the embedded window
                    for (nativeWindow in windowList) {
                        if (nativeWindow.hWnd != embeddedWindow &&
                                Cursor.currentPoint.x > nativeWindow.lastUnits.left &&
                                Cursor.currentPoint.x < nativeWindow.lastUnits.right &&
                                Cursor.currentPoint.y > nativeWindow.lastUnits.top &&
                                Cursor.currentPoint.y < nativeWindow.lastUnits.bottom) {
                            isEmbedded = true
                            embeddedWindow = nativeWindow.hWnd
                            nativeWindow.fullBody.isActive = false

                            logger.info("Placed Hanger-chan inside ${Win32WindowUtil.getTitle(embeddedWindow)}")

                            for (i in nativeWindow.internalBodyList) {
                                i.isActive = true
                            }
                            break
                        }
                    }

                    with(User32.INSTANCE.GetDesktopWindow()) {
                        val list = mutableListOf<Boolean>()

                        for (nativeWindow in windowList) {
                            if (!nativeWindow.lastUnits.isInside(
                                            PhysicsUtil.guiToPhysics(Cursor.currentPoint.x),
                                            PhysicsUtil.guiToPhysics(Cursor.currentPoint.y)
                                    )
                            ) {
                                list.add(false)
                            } else {
                                list.add(true)
                            }
                        }

                        if (list.all { !it }) {
                            isEmbedded = true
                            embeddedWindow = this
                            list.clear()
                        }
                    }
                }

                isGrabbed = false
                isBeingPulled = false
            }

            override fun mouseDragged(e: MouseEvent) {
                Cursor.currentPoint.set(e.xOnScreen, e.yOnScreen)

                if (isGrabbed) {
                    isBeingPulled = true
                }
            }
        }.apply { ApplicationWindow.addMouseMotionListener(this) })

        ApplicationWindow.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_A -> {
                        currentAction = Action.Walking
                        direction = -1
                    }
                    KeyEvent.VK_D -> {
                        currentAction = Action.Walking
                        direction = 1
                    }
                    KeyEvent.VK_SPACE -> {
                        currentAction = if (onGround) {
                            Action.Jumping
                        } else {
                            Action.Idle
                        }
                    }
                    else -> {
                        currentAction = Action.Idle
                    }
                }
            }

            override fun keyReleased(e: KeyEvent) {
                currentAction = Action.Idle
            }
        })
    }

    fun isInside(): Boolean =
            PhysicsUtil.guiToPhysics(Cursor.body.position.x) > body.position.x - sheet.spriteWidth / 2 &&
            PhysicsUtil.guiToPhysics(Cursor.body.position.x) < body.position.x + sheet.spriteWidth / 2 &&
            PhysicsUtil.guiToPhysics(Cursor.body.position.y) > body.position.y - sheet.spriteHeight / 2 &&
            PhysicsUtil.guiToPhysics(Cursor.body.position.y) < body.position.y + sheet.spriteHeight / 2

    fun animate() {
        if (currentFrame < 7) {
            currentFrame++
        } else {
            currentFrame = 0
        }

        if (beingControlled) {
            currentAction.manual()
        } else {
            when (collisionSide?.x) {
                // Left
                -1f -> {
                    direction = 1
                    collisionSide = null
                }
                // Right
                1f -> {
                    direction = -1
                    collisionSide = null
                }
            }

            if (body.linearVelocity.y < -1 &&
                    PhysicsUtil.guiToPhysics(Cursor.clickedPoint.y) == 0f ||
                    currentAction == Action.Thrown) {
                currentAction = Action.Falling
                justFell = true
            } else if (PhysicsUtil.guiToPhysics(Cursor.clickedPoint.y) != 0f) {
                currentAction = Action.Thrown
            } else {
                if (justFell) {
                    currentAction = Action.Idle
                    justFell = false
                }
            }

            if (isGrabbed) {
                currentAction = Action.Grabbed
            }

            currentAction.automatic()
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2D = g as Graphics2D
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // println("Position: ${body.position}, Velocity: ${body.linearVelocity}")
        g.drawImage(
                sheet.spriteMap[currentAction.toString()]!![currentFrame],
                (PhysicsUtil.physicsToGui(body.position.x) - sheet.spriteWidth / 4 +
                        if (direction == -1) sheet.spriteWidth / 2 else 0).toInt(),
                (PhysicsUtil.physicsToGui(-body.position.y) - sheet.spriteHeight / 4).toInt(),
                (sheet.spriteWidth / 2) * direction,
                sheet.spriteHeight / 2,
                this
        )

        // Debug prints
        g2D.stroke = BasicStroke(2f)

        g2D.color = Color.RED
        PhysicsUtil.drawPhysicsShape(g2D, body)
        val title = Win32WindowUtil.getTitle(embeddedWindow)
        PhysicsUtil.drawText(g2D, "Placed In: ${if (title != "") title else "Desktop"}", body, yIncrease = -1.4f)
        PhysicsUtil.drawText(g2D, "X: ${"%.1f".format(body.position.x)}", body)
        PhysicsUtil.drawText(g2D, "Y: ${"%.1f".format(body.position.y)}", body, yIncrease = 1.4f)
        PhysicsUtil.drawText(g2D, "Act: ${currentAction.name}", body, yIncrease = 1.4f * 2)

        g2D.color = Color.GREEN
        if (borders.isNotEmpty()) {
            for (b in borders) {
                PhysicsUtil.drawPhysicsShape(g2D, b)
            }
        }

        if (Cursor.body.isActive) {
            g2D.color = Color.CYAN
        } else {
            g2D.color = Color.BLACK
        }
        PhysicsUtil.drawPhysicsShape(g2D, Cursor.body)

        if (windowList.isNotEmpty()) {
            for (w in windowList) {
                if (embeddedWindow == w.hWnd) {
                    for (ww in w.internalBodyList) {
                        g2D.color = Color.PINK
                        g2D.stroke = BasicStroke(8f)
                        PhysicsUtil.drawPhysicsShape(g2D, ww)
                    }
                }

                if (isGrabbed) {
                    if (Cursor.currentPoint.x > w.lastUnits.left &&
                            Cursor.currentPoint.x < w.lastUnits.right &&
                            Cursor.currentPoint.y > w.lastUnits.top &&
                            Cursor.currentPoint.y < w.lastUnits.bottom) {
                        g2D.color = Color.CYAN
                        g2D.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f)
                        PhysicsUtil.drawWindowFill(g2D, w.fullBody)
                        break
                    }
                }
            }

            for (window in windowList) {
                g2D.stroke = BasicStroke(2f)
                PhysicsUtil.drawWindowShape(window, g2D)
            }
        }
    }

    fun walk(direction: Int) {
        body.linearVelocity.x = 12f * direction
    }

    fun walkToPoint(point: WindowUtil.Point) {
        Pathing.default.regenPath(point).apply {
            println(this)
        }
    }

    fun jump() {
        body.linearVelocity.y = 12f
    }
}
