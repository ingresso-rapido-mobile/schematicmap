package com.vitorprado.schematicmap.seats

import android.graphics.Bitmap
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
    var type: SeatType

    constructor(name: String, id: Long, position: Point, radius: Float, state: SeatState, type: SeatType) {
        this.name = name
        this.id = id
        this.position = position
        this.radius = radius
        this.state = state
        this.type = type
    }

    fun draw(canvas: Canvas?, paint: Paint, wheelchairIcon: Bitmap) {
        if (type == SeatType.PWD) {
            canvas?.drawBitmap(wheelchairIcon, position.x - 8f, position.y - 8f, paint)
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