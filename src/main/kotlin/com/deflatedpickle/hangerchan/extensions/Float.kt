/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.extensions

import com.deflatedpickle.hangerchan.util.physics.PhysicsUtil

fun Float.toPhysics() = PhysicsUtil.guiToPhysics(this)

fun Float.toGUI() = PhysicsUtil.physicsToGui(this)
