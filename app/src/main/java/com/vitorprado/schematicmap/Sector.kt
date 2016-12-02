package com.vitorprado.schematicmap

import android.graphics.Path

class Sector {
    val path by lazy { createPath() }
    val name: String
    val id: Int
    val points: List<Point>
    var visible: Boolean

    constructor(name: String, id: Int, points: List<Point>, visible: Boolean) {
        this.name = name
        this.id = id
        this.points = points
        this.visible = visible
        createPath()
    }

    private fun createPath(): Path {
        val localPath = Path()
        localPath.reset()
        localPath.moveTo(points[0].x, points[0].y)
        for (point in points) localPath.lineTo(point.x, point.y)
        return localPath
    }
}


class Point(val x: Float, val y: Float)
