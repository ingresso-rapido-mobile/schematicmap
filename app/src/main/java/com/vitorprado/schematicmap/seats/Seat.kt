package com.vitorprado.schematicmap.seats

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.vitorprado.schematicmap.Point

class Seat {

    val path by lazy { createPath() }
    val name: String
    val id: Long
    val position: Point
    val radius: Float
    var state: SeatState

    constructor(name: String, id: Long, position: Point, radius: Float, state: SeatState) {
        this.name = name
        this.id = id
        this.position = position
        this.radius = radius
        this.state = state
    }

    fun draw(canvas: Canvas?, paint: Paint) {
        canvas?.drawPath(path, paint)
    }

    private fun createPath(): Path {
        val p = Path()
        p.addCircle(position.x, position.y, radius, Path.Direction.CCW)
        return p
    }
}

enum class SeatState { AVAILABLE, SELECTED, UNAVAILABLE }