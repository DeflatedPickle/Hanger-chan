package com.deflatedpickle.hangerchan

import com.deflatedpickle.jna.User32Extended
import com.sun.jna.Native
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import org.jbox2d.callbacks.ContactImpulse
import org.jbox2d.callbacks.ContactListener
import org.jbox2d.collision.Manifold
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.World
import org.jbox2d.dynamics.contacts.Contact
import java.awt.Color
import java.awt.event.ActionListener
import javax.swing.JFrame
import javax.swing.Timer


@Suppress("KDocMissingDocumentation")
fun main(args: Array<String>) {
    val frame = JFrame("Hanger-chan")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    frame.isUndecorated = true
    frame.isAlwaysOnTop = true
    frame.background = Color(0, 0, 0, 0)

    frame.extendedState = JFrame.MAXIMIZED_BOTH

    var monitor: WinUser.HMONITOR? = null
    val monitorInfo = WinUser.MONITORINFO()

    val world = World(Vec2(0f, -80f))

    // 1.524
    val hangerchan = Hangerchan(frame, world)
    frame.contentPane.add(hangerchan)

    var collisionPoint = Vec2()
    val collisions = object : ContactListener {
        override fun endContact(contact: Contact) {
            hangerchan.onGround = contact.isTouching
        }

        override fun beginContact(contact: Contact) {
            hangerchan.onGround = contact.isTouching
        }

        override fun preSolve(contact: Contact, oldManifold: Manifold) {
            if (oldManifold.localNormal.x != 0f) {
                collisionPoint = oldManifold.localNormal
            }
        }

        override fun postSolve(contact: Contact, impulse: ContactImpulse) {
            hangerchan.collisionSide = collisionPoint
        }
    }
    world.setContactListener(collisions)

    // Cursor
    val cursorLocation = WinDef.POINT()
    User32.INSTANCE.GetCursorPos(cursorLocation)

    val cursorWidth = User32.INSTANCE.GetSystemMetrics(User32.SM_CXCURSOR)
    val cursorHeight = User32.INSTANCE.GetSystemMetrics(User32.SM_CYCURSOR)

    val cursorBody = world.createBody(BodyDef().apply {
        position.set(cursorLocation.x + cursorWidth / 2f, -cursorLocation.y - cursorHeight / 2f)
    }).apply {
        createFixture(PolygonShape().apply {
            setAsBox(cursorWidth / 2f, cursorHeight / 2f)
        }, 0f)
    }
    hangerchan.cursor = cursorBody
    var counter = 0
    val openWindows = mutableListOf<WinDef.HWND>()
    val timer = Timer(1000 / 144 * 4, ActionListener {
        counter++

        // Check if there are any new windows
        if (counter % 12 == 0) {
            for (w in WindowUtil.getAllWindows(0)) {
                // I think these program's open on start-up and fail the window check, even when you haven't used them
                // So they have a window border, so
                // TODO: Figure out why these programs behave like this and find more examples that act like this
                // More examples might help finding out why they behave like this
                val annoyingPrograms = listOf("Settings", "Microsoft Store", "Photos", "Films & TV", "Groove Music")
                if (WindowUtil.getTitle(w) !in annoyingPrograms && !hangerchan.windows.containsKey(w)) {
                    // println("Found new windows")
                    openWindows.add(w)

                    val rect = WinDef.RECT()
                    User32.INSTANCE.GetWindowRect(w, rect)

                    val x = rect.left.toFloat() * PhysicsUtil.scaleDown
                    val y = rect.top.toFloat() * PhysicsUtil.scaleDown
                    val width = (rect.right.toFloat() * PhysicsUtil.scaleDown) - x
                    val height = (rect.bottom.toFloat() * PhysicsUtil.scaleDown) - y

                    // println("X: $x, Y: $y, Width: $width, Height: $height")

                    val body = world.createBody(BodyDef().apply {
                        position.set(x + width / 2, -y - height / 2)
                    }).apply {
                        createFixture(PolygonShape().apply {
                            setAsBox(width / 2, height / 2)
                        }, 0f)
                    }

                    val internalBodyList = mutableListOf<Body>()

                    internalBodyList.add(world.createBody(BodyDef().apply {
                        position.set(x, -y - height / 2)
                    }).apply {
                        isActive = false
                        createFixture(PolygonShape().apply {
                            setAsBox(0f, height / 2)
                        }, 0f)
                    })

                    internalBodyList.add(world.createBody(BodyDef().apply {
                        position.set(x + width, -y - height / 2)
                    }).apply {
                        isActive = false
                        createFixture(PolygonShape().apply {
                            setAsBox(0f, height / 2)
                        }, 0f)
                    })

                    internalBodyList.add(world.createBody(BodyDef().apply {
                        position.set(x + width / 2, -y)
                    }).apply {
                        isActive = false
                        createFixture(PolygonShape().apply {
                            setAsBox(width / 2, 0f)
                        }, 0f)
                    })

                    internalBodyList.add(world.createBody(BodyDef().apply {
                        position.set(x + width / 2, -y - height)
                    }).apply {
                        isActive = false
                        createFixture(PolygonShape().apply {
                            setAsBox(width / 2, 0f)
                        }, 0f)
                    })

                    hangerchan.windows[w] = Window(w, rect, body, internalBodyList)
                }
            }

            for (i in openWindows) {
                if (WindowUtil.getTitle(i) == "" && hangerchan.windows.keys.contains(i)) {
                    // println("A window was closed")
                    hangerchan.windows.remove(i)
                }
            }
        }

        if (counter % 3 == 0) {
            world.step(1f / 60f, 1, 1)

            hangerchan.animate()
        }

        for ((k, v) in hangerchan.windows) {
            val rect = WinDef.RECT()
            User32.INSTANCE.GetWindowRect(k, rect)

            // Check if the positions are the same before moving the collision box
            // Occasionally fails, leaving the box far away from the window, don't know why
            // if (v.lastUnits.top != rect.top && v.lastUnits.bottom != rect.bottom && v.lastUnits.left != rect.left && v.lastUnits.right != rect.right) {
            val x = rect.left.toFloat() * PhysicsUtil.scaleDown
            val y = rect.top.toFloat() * PhysicsUtil.scaleDown
            val width = (rect.right.toFloat() * PhysicsUtil.scaleDown) - x
            val height = (rect.bottom.toFloat() * PhysicsUtil.scaleDown) - y

            v.body.setTransform(Vec2(x + width / 2, -y - height / 2), 0f)
            (v.body.fixtureList.shape as PolygonShape).setAsBox(width / 2, height / 2)
            // }

            v.internalBodyList[0].setTransform(Vec2(x, -y - height / 2), 0f)
            (v.internalBodyList[0].fixtureList.shape as PolygonShape).setAsBox(0f, height / 2)

            v.internalBodyList[1].setTransform(Vec2(x + width, -y - height / 2), 0f)
            (v.internalBodyList[1].fixtureList.shape as PolygonShape).setAsBox(0f, height / 2)

            v.internalBodyList[2].setTransform(Vec2(x + width / 2, -y), 0f)
            (v.internalBodyList[2].fixtureList.shape as PolygonShape).setAsBox(width / 2, 0f)

            v.internalBodyList[3].setTransform(Vec2(x + width / 2, -y - height), 0f)
            (v.internalBodyList[3].fixtureList.shape as PolygonShape).setAsBox(width / 2, 0f)

            v.lastUnits = rect
        }

        User32.INSTANCE.GetCursorPos(cursorLocation)

        // TODO: Get the size of the current cursor instead of the constant size
        val cursorWidth = User32.INSTANCE.GetSystemMetrics(User32.SM_CXCURSOR)
        val cursorHeight = User32.INSTANCE.GetSystemMetrics(User32.SM_CYCURSOR)
        // println("${cursorLocation.x}:${cursorLocation.y}, $cursorWidth:$cursorHeight")
        cursorBody.setTransform(Vec2((cursorLocation.x.toFloat() + cursorWidth / 4) * PhysicsUtil.scaleDown, -(cursorLocation.y.toFloat() + cursorHeight / 4) * PhysicsUtil.scaleDown), 0f)
        (cursorBody.fixtureList.shape as PolygonShape).setAsBox((cursorWidth / 2) * PhysicsUtil.scaleDown, (cursorHeight / 2) * PhysicsUtil.scaleDown)

        cursorBody.isActive = User32.INSTANCE.GetAsyncKeyState(User32Extended.VK_LBUTTON) < 0 && !hangerchan.isGrabbed

        hangerchan.repaint()
    })
    timer.start()

    frame.pack()
    frame.isVisible = true

    if (monitor == null) {
        monitor = User32.INSTANCE.MonitorFromWindow(WinDef.HWND(Native.getComponentPointer(frame)), User32.MONITOR_DEFAULTTONEAREST)
        User32.INSTANCE.GetMonitorInfo(monitor, monitorInfo)
    }

    val monitorWidth = monitorInfo.rcWork.right.toFloat() * PhysicsUtil.scaleDown
    val monitorHeight = monitorInfo.rcWork.bottom.toFloat() * PhysicsUtil.scaleDown

    // Top border
    hangerchan.borders.add(world.createBody(BodyDef().apply {
        position.set(monitorWidth / 2, 1f)
    }).apply {
        createFixture(PolygonShape().apply {
            setAsBox(monitorWidth / 2, 1f)
        }, 0f)
    })

    // Bottom border
    hangerchan.borders.add(world.createBody(BodyDef().apply {
        position.set(monitorWidth / 2, -monitorHeight - 1)
    }).apply {
        createFixture(PolygonShape().apply {
            setAsBox(monitorWidth / 2, 1f)
        }, 0f)
    })

    // Left border
    hangerchan.borders.add(world.createBody(BodyDef().apply {
        position.set(-1f, -monitorHeight / 2)
    }).apply {
        createFixture(PolygonShape().apply {
            setAsBox(1f, monitorHeight / 2)
        }, 0f)
    })

    // Right border
    hangerchan.borders.add(world.createBody(BodyDef().apply {
        position.set(monitorWidth + 1f, -monitorHeight / 2)
    }).apply {
        createFixture(PolygonShape().apply {
            setAsBox(1f, monitorHeight / 2)
        }, 0f)
    })
}