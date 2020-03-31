package com.deflatedpickle.hangerchan.event

import com.pploder.events.SimpleEvent
import org.apache.logging.log4j.LogManager
import java.util.function.Consumer

abstract class AbstractEvent<T> : SimpleEvent<T>() {
    val logger = LogManager.getLogger(this::class.simpleName)

    override fun addListener(listener: Consumer<T>?) {
        logger.debug("A listener was attached to ${this::class.simpleName}")
        super.addListener(listener)
    }
}