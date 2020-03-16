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
        override fun automatic(hc: HangerChan) {
            // If this is set lower, her velocity stays at 0 forever. Bug?
            hc.body.linearVelocity.x = 0.4f

            if (hc.graceCoolDown == 0) {
                val random = ThreadLocalRandom.current().nextInt(0, 11)

                if (random == 0) {
                    hc.currentAction = Walking
                    hc.graceCoolDown = ThreadLocalRandom.current().nextInt(25, 36)
                } else if (random == 1) {
                    hc.direction *= -1
                }
            } else {
                hc.graceCoolDown--
            }
        }

        override fun manual(hc: HangerChan) {
            hc.body.linearVelocity.x = 0.4f
        }
    },
    Walking {
        override fun automatic(hc: HangerChan) {
            hc.walk(hc.direction)

            if (hc.graceCoolDown == 0) {
                val random = ThreadLocalRandom.current().nextInt(0, 11)

                if (random == 0) {
                    hc.currentAction = Idle
                    hc.graceCoolDown = ThreadLocalRandom.current().nextInt(25, 36)
                }
            } else {
                hc.graceCoolDown--
            }
        }

        override fun manual(hc: HangerChan) {
            hc.walk(hc.direction)
        }
    },
    Jumping {
        override fun automatic(hc: HangerChan) {
            hc.jump()
        }

        override fun manual(hc: HangerChan) {
            hc.jump()
        }
    },

    Grabbed {
        override fun automatic(hc: HangerChan) {
            if (hc.isBeingPulled) {
                hc.body.linearVelocity.x = 0f
                hc.body.setTransform(Vec2(hc.mouseX, -hc.mouseY), 0f)
            }
        }

        override fun manual(hc: HangerChan) {
        }
    },
    Pulled {
        override fun automatic(hc: HangerChan) {
        }

        override fun manual(hc: HangerChan) {
        }
    },
    Thrown {
        override fun automatic(hc: HangerChan) {
            // val force = Vec2(0f, clickedY + releasedY)
            // println(force)
            // body.linearVelocity = force
            // currentAction = Action.Falling

            hc.clickedX = 0f
            hc.clickedY = 0f
            hc.releasedX = 0f
            hc.releasedY = 0f
        }

        override fun manual(hc: HangerChan) {
        }
    },

    Falling {
        override fun automatic(hc: HangerChan) {
        }

        override fun manual(hc: HangerChan) {
        }
    };

    abstract fun automatic(hc: HangerChan)
    abstract fun manual(hc: HangerChan)
}
