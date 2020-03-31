/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.event

import com.pploder.events.SimpleEvent
import java.util.function.Consumer
import org.apache.logging.log4j.LogManager

abstract class AbstractEvent<T> : SimpleEvent<T>() {
    val logger = LogManager.getLogger(this::class.simpleName)

    override fun addListener(listener: Consumer<T>?) {
        logger.debug("A listener was attached to ${this::class.simpleName}")
        super.addListener(listener)
    }
}
