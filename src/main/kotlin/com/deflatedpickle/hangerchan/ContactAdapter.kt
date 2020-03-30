package com.deflatedpickle.hangerchan

import org.apache.logging.log4j.LogManager
import org.jbox2d.callbacks.ContactImpulse
import org.jbox2d.callbacks.ContactListener
import org.jbox2d.collision.Manifold
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.contacts.Contact

object ContactAdapter : ContactListener {
    private val logger = LogManager.getLogger(ContactAdapter::class.simpleName)

    var collisionPoint = Vec2()

    override fun endContact(contact: Contact) {
        when (collisionPoint.y) {
            -1f -> {
                HangerChan.onGround = contact.isTouching
                logger.debug("Hanger-chan left the ground")
            }
        }
    }

    override fun beginContact(contact: Contact) {
        when (collisionPoint.y) {
            -1f -> {
                HangerChan.onGround = contact.isTouching
                logger.debug("Hanger-chan hit the ground")
            }
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        if (oldManifold.localNormal.x != 0f &&
                collisionPoint != oldManifold.localNormal) {
            collisionPoint = oldManifold.localNormal
            logger.debug("Hanger-chan collided on $collisionPoint")
        }
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
        HangerChan.collisionSide = collisionPoint
    }
}