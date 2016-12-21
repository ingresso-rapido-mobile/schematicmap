package com.vitorprado.schematicmap.seats

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import com.vitorprado.schematicmap.Point

class Seat {

    val path by lazy { createPath() }
    val name: String
    val id: Long
    val position: Point
    val radius: Float
    var state: SeatState
    var type: SeatType

    constructor(name: String, id: Long, position: Point, radius: Float, state: SeatState, type: SeatType) {
        this.name = name
        this.id = id
        this.position = position
        this.radius = radius
        this.state = state
        this.type = type
    }

    fun draw(canvas: Canvas?, paint: Paint, wheelchairIcon: Drawable) {
        if (type == SeatType.PWD) {
            wheelchairIcon.setBounds((position.x - 8f).toInt(), (position.y - 8f).toInt(), (position.x + 8f).toInt(), (position.y + 8f).toInt())
            wheelchairIcon.draw(canvas)
        } else {
            canvas?.drawPath(path, paint)
        }
    }

    private fun createPath(): Path {
        val p = Path()
        p.addCircle(position.x, position.y, radius, Path.Direction.CCW)
        return p
    }
}

enum class SeatState { AVAILABLE, SELECTED, UNAVAILABLE }

enum class SeatType { NORMAL, PWD }