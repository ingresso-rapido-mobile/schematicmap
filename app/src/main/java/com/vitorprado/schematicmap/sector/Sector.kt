package com.vitorprado.schematicmap.sector

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.vitorprado.schematicmap.Point
import java.util.*

class Sector {
    val paths by lazy { createPaths() }
    val name: String
    val id: Int
    val points: List<List<Point>>
    var visible: Boolean

    constructor(name: String, id: Int, points: List<List<Point>>, visible: Boolean) {
        this.name = name
        this.id = id
        this.points = points
        this.visible = visible
    }

    private fun createPaths(): List<Path> {
        val list = ArrayList<Path>()
        for (pointList in points) {
            list.add(createPath(pointList))
        }
        return list
    }

    private fun createPath(points: List<Point>): Path {
        val localPath = Path()
        localPath.reset()
        localPath.moveTo(points[0].x, points[0].y)
        for (point in points) localPath.lineTo(point.x, point.y)
        return localPath
    }

    fun draw(canvas: Canvas?, paint: Paint) {
        if (!visible) return
        for (path in paths) {
            canvas?.drawPath(path, paint)
        }
    }
}
