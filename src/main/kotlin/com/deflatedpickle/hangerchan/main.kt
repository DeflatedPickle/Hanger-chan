package com.deflatedpickle.hangerchan

// import org.dyn4j.dynamics.World
// import org.dyn4j.geometry.Vector2
import com.deflatedpickle.jna.User32Extended
import com.sun.jna.Native
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinGDI
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
        }

        override fun beginContact(contact: Contact) {
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

    val windowBodies = mutableMapOf<WinDef.HWND, Body>()
    val cursorBody = world.createBody(BodyDef().apply {
        position.set(cursorLocation.x + cursorWidth / 2f, -cursorLocation.y - cursorHeight / 2f)
    }).apply {
        createFixture(PolygonShape().apply {
            setAsBox(cursorWidth / 2f, cursorHeight / 2f)
        }, 0f)
    }
    hangerchan.cursor = cursorBody
    val timer = Timer(120 / 2, ActionListener {
        world.step(1f / 60f, 6, 2)

        hangerchan.animate()
        hangerchan.repaint()

        // TODO: Only update it if the position has changed
        // TODO: Poll for windows open since Hanger-chan was run
        // TODO: Try moving to a second thread to see if there's a performance increase
        for ((k, v) in windowBodies) {
            val rect = WinDef.RECT()
            User32.INSTANCE.GetWindowRect(k, rect)

            val x = rect.left.toFloat() * PhysicsUtil.scaleDown
            val y = rect.top.toFloat() * PhysicsUtil.scaleDown
            val width = (rect.right.toFloat() * PhysicsUtil.scaleDown) - x
            val height = (rect.bottom.toFloat() * PhysicsUtil.scaleDown) - y

            v.setTransform(Vec2(x + width / 2, -y - height / 2), 0f)
            (v.fixtureList.shape as PolygonShape).setAsBox(width / 2, height / 2)
        }

        User32.INSTANCE.GetCursorPos(cursorLocation)

        // TODO: Get the size of the current cursor instead of the constant size
        val cursorWidth = User32.INSTANCE.GetSystemMetrics(User32.SM_CXCURSOR)
        val cursorHeight = User32.INSTANCE.GetSystemMetrics(User32.SM_CYCURSOR)
        // println("${cursorLocation.x}:${cursorLocation.y}, $cursorWidth:$cursorHeight")
        cursorBody.setTransform(Vec2((cursorLocation.x.toFloat() + cursorWidth / 4) * PhysicsUtil.scaleDown, -(cursorLocation.y.toFloat() + cursorHeight / 4) * PhysicsUtil.scaleDown), 0f)
        (cursorBody.fixtureList.shape as PolygonShape).setAsBox((cursorWidth / 2) * PhysicsUtil.scaleDown, (cursorHeight / 2) * PhysicsUtil.scaleDown)

        cursorBody.isActive = User32.INSTANCE.GetAsyncKeyState(User32Extended.VK_LBUTTON) < 0 && !hangerchan.isGrabbed
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

    // Windows
    for (w in WindowUtil.getAllWindows(0)) {
        // I think these program's open on start-up and fail the window check, even when you haven't used them
        // So they have a window border, so
        // TODO: Figure out why these programs behave like this and find more examples that act like this
        // More examples might help finding out why they behave like this
        val annoyingPrograms = listOf("Settings", "Microsoft Store", "Photos")
        if (WindowUtil.getTitle(w) !in annoyingPrograms) {
            val rect = WinDef.RECT()
            User32.INSTANCE.GetWindowRect(w, rect)

            val x = rect.left.toFloat() * PhysicsUtil.scaleDown
            val y = rect.top.toFloat() * PhysicsUtil.scaleDown
            val width = (rect.right.toFloat() * PhysicsUtil.scaleDown) - x
            val height = (rect.bottom.toFloat() * PhysicsUtil.scaleDown) - y

            // println("X: $x, Y: $y, Width: $width, Height: $height")

            // TODO: Split into a fixture for each side of the window, so something can happen inside a window
            val body = world.createBody(BodyDef().apply {
                position.set(x + width / 2, -y - height / 2)
            }).apply {
                createFixture(PolygonShape().apply {
                    setAsBox(width / 2, height / 2)
                }, 0f)
            }
            hangerchan.windows.add(body)

            windowBodies[w] = body
        }
    }
}