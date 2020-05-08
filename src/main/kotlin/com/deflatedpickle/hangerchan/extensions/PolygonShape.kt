package com.deflatedpickle.hangerchan.extensions

import org.jbox2d.collision.shapes.PolygonShape

fun PolygonShape.getWidth(): Float = this.vertices[0].x + this.vertices[2].x

fun PolygonShape.getHeight(): Float = this.vertices[0].y + this.vertices[2].y

fun PolygonShape.getSize(): Pair<Float, Float> = Pair(this.getWidth(), this.getHeight())

fun PolygonShape.isInside(x: Float, y: Float) =
        isInsideX(x) && isInsideY(y)

fun PolygonShape.isInsideX(x: Float) =
        x > this.vertices[0].x &&
                x < this.vertices[2].x

fun PolygonShape.isInsideY(y: Float) =
        y > this.vertices[3].y &&
                y < this.vertices[1].y