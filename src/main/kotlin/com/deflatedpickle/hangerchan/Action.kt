package com.deflatedpickle.hangerchan

import org.jbox2d.common.Vec2
import java.util.concurrent.ThreadLocalRandom

/**
 * Actions that Hanger-chan can carry out
 */
@Suppress("KDocMissingDocumentation")
enum class Action {
    Idle {
        override fun automatic(hc: Hangerchan) {
            // If this is set lower, her velocity stays at 0 forever. Bug?
            hc.body.linearVelocity.x = 0.4f

            if (hc.graceCooldown == 0) {
                val random = ThreadLocalRandom.current().nextInt(0, 11)

                if (random == 0) {
                    hc.currentAction = Walking
                    hc.graceCooldown = ThreadLocalRandom.current().nextInt(25, 36)
                }
                else if (random == 1) {
                    hc.direction *= -1
                }
            }
            else {
                hc.graceCooldown--
            }
        }

        override fun manual(hc: Hangerchan) {
            hc.body.linearVelocity.x = 0.4f
        }
    },
    Walking {
        override fun automatic(hc: Hangerchan) {
            hc.walk(hc.direction)

            if (hc.graceCooldown == 0) {
                val random = ThreadLocalRandom.current().nextInt(0, 11)

                if (random == 0) {
                    hc.currentAction = Idle
                    hc.graceCooldown = ThreadLocalRandom.current().nextInt(25, 36)
                }
            }
            else {
                hc.graceCooldown--
            }
        }

        override fun manual(hc: Hangerchan) {
            hc.walk(hc.direction)
        }
    },
    Jumping {
        override fun automatic(hc: Hangerchan) {
            hc.jump()
        }

        override fun manual(hc: Hangerchan) {
            hc.jump()
        }
    },

    Grabbed {
        override fun automatic(hc: Hangerchan) {
            if (hc.isBeingPulled) {
                hc.body.linearVelocity.x = 0f
                hc.body.setTransform(Vec2(hc.mouseX, -hc.mouseY), 0f)
            }
        }

        override fun manual(hc: Hangerchan) {
        }
    },
    Pulled {
        override fun automatic(hc: Hangerchan) {
        }

        override fun manual(hc: Hangerchan) {
        }
    },
    Thrown {
        override fun automatic(hc: Hangerchan) {
            // val force = Vec2(0f, clickedY + releasedY)
            // println(force)
            // body.linearVelocity = force
            // currentAction = Action.Falling

            hc.clickedX = 0f
            hc.clickedY = 0f
            hc.releasedX = 0f
            hc.releasedY = 0f
        }

        override fun manual(hc: Hangerchan) {
        }
    },

    Falling {
        override fun automatic(hc: Hangerchan) {
        }

        override fun manual(hc: Hangerchan) {
        }
    };

    abstract fun automatic(hc: Hangerchan)
    abstract fun manual(hc: Hangerchan)
}