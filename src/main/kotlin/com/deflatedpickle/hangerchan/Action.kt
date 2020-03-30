/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan

import java.util.concurrent.ThreadLocalRandom
import org.jbox2d.common.Vec2

/**
 * Actions that Hanger-chan can carry out
 */
@Suppress("KDocMissingDocumentation")
enum class Action {
    Idle {
        override fun automatic() {
            // If this is set lower, her velocity stays at 0 forever. Bug?
            HangerChan.body.linearVelocity.x = 0.4f

            if (HangerChan.graceCoolDown == 0) {
                val random = ThreadLocalRandom.current().nextInt(0, 11)

                if (random == 0) {
                    HangerChan.currentAction = Walking
                    HangerChan.graceCoolDown = ThreadLocalRandom.current().nextInt(25, 36)
                } else if (random == 1) {
                    HangerChan.direction *= -1
                }
            } else {
                HangerChan.graceCoolDown--
            }
        }

        override fun manual() {
            HangerChan.body.linearVelocity.x = 0.4f
        }
    },
    Walking {
        override fun automatic() {
            HangerChan.walk(HangerChan.direction)

            if (HangerChan.graceCoolDown == 0) {
                val random = ThreadLocalRandom.current().nextInt(0, 11)

                if (random == 0) {
                    HangerChan.currentAction = Idle
                    HangerChan.graceCoolDown = ThreadLocalRandom.current().nextInt(25, 36)
                }
            } else {
                HangerChan.graceCoolDown--
            }
        }

        override fun manual() {
            HangerChan.walk(HangerChan.direction)
        }
    },
    Jumping {
        override fun automatic() {
            HangerChan.jump()
        }

        override fun manual() {
            HangerChan.jump()
        }
    },

    Grabbed {
        override fun automatic() {
            if (HangerChan.isBeingPulled) {
                HangerChan.body.linearVelocity.x = 0f
                HangerChan.body.setTransform(Vec2(Cursor.mouseX, -Cursor.mouseY), 0f)
            }
        }

        override fun manual() {
        }
    },
    Pulled {
        override fun automatic() {
        }

        override fun manual() {
        }
    },
    Thrown {
        override fun automatic() {
            // val force = Vec2(0f, clickedY + releasedY)
            // println(force)
            // body.linearVelocity = force
            // currentAction = Action.Falling

            Cursor.clickedX = 0f
            Cursor.clickedY = 0f
            Cursor.releasedX = 0f
            Cursor.releasedY = 0f
        }

        override fun manual() {
        }
    },

    Falling {
        override fun automatic() {
        }

        override fun manual() {
        }
    };

    abstract fun automatic()
    abstract fun manual()
}
