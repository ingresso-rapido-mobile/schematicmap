package com.vitorprado.schematicmap.seats

import android.graphics.*
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import com.onlylemi.mapview.library.layer.MapLayer
import com.vitorprado.schematicmap.ImprovedMapView
import com.vitorprado.schematicmap.Point
import com.vitorprado.schematicmap.R
import java.util.*

class SeatsLayer(val seatsMapView: ImprovedMapView, val seats: List<Seat>, val seatClickedListener: (Seat) -> Any?) : MapLayer(seatsMapView) {

    private var clip: Region? = null
    private var wheelchairIcon = ContextCompat.getDrawable(seatsMapView.context, R.drawable.wheelchair)
    private var wheelchairCompanionIcon = ContextCompat.getDrawable(seatsMapView.context, R.drawable.ic_icon_acompanhante)

    override fun draw(canvas: Canvas?, currentMatrix: Matrix?, currentZoom: Float, currentRotateDegrees: Float) {
        clip = canvas?.let { Region(0, 0, canvas.width, canvas.height) }
        val paint = Paint()
        paint.style = Paint.Style.FILL

        canvas?.save()
        canvas?.matrix = currentMatrix
        for (it in seats) {
            paint.color = when (it.state) {
                SeatState.AVAILABLE -> when (it.type) {
                    SeatType.NORMAL -> Color.GREEN
                    SeatType.CPWD -> Color.BLACK
                    SeatType.PWD -> Color.BLUE
                }
                SeatState.SELECTED -> Color.YELLOW
                SeatState.UNAVAILABLE -> Color.GRAY
                SeatState.HIGHLIGHTED -> Color.argb(255, 254, 160, 9)
            }
            it.draw(canvas, paint, wheelchairIcon, wheelchairCompanionIcon)
        }
        canvas?.restore()
    }

    override fun onTouch(event: MotionEvent?) {
        if (hasMoved(Pair(event?.x?:0f, event?.y?:0f))) return
        val clickPoints = event?.let { mapView.convertMapXYToScreenXY(event.x, event.y) }
        if (clickPoints != null) checkIFClickedInSector(clickPoints)
    }

    private fun hasMoved(points: Pair<Float, Float>): Boolean {
        return !((points.first in (seatsMapView.downEvent.first - 10f)..(seatsMapView.downEvent.first + 10f))
                && (points.second in (seatsMapView.downEvent.second - 10f)..(seatsMapView.downEvent.second + 10f)))
    }

    private fun checkIFClickedInSector(clickPoints: FloatArray) {
        for (it in closeEnoughSeats(seats, clickPoints)) {
            if (it.state == SeatState.UNAVAILABLE) continue
            selectSeat(it)
            return
        }
    }

    private fun closeEnoughSeats(seats: List<Seat>, clickPoints: FloatArray): List<Seat> {
        val filteredList = seats.filter { isCloseEnough(it.position, clickPoints) }
        Collections.sort(filteredList, { l, r ->
            val dist1 = distance(l.position.x, l.position.y, clickPoints[0], clickPoints[1])
            val dist2 = distance(r.position.x, r.position.y, clickPoints[0], clickPoints[1])
            when {
                dist1 > dist2 -> 1
                dist1 < dist2 -> -1
                else -> 0
            }
        })
        return filteredList
    }

    private fun isCloseEnough(position: Point, clickPoints: FloatArray): Boolean {
        return distance(position.x, position.y, clickPoints[0], clickPoints[1]) <= 25f
    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return Math.sqrt((((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1))).toDouble()).toFloat()
    }

    private fun selectSeat(seat: Seat) {
        if (seat.state == SeatState.UNAVAILABLE || seat.state == SeatState.HIGHLIGHTED) return
        val realSeat = findSeat(seat)
        realSeat?.state = when (realSeat?.state) {
            SeatState.AVAILABLE -> SeatState.SELECTED
            SeatState.SELECTED -> SeatState.AVAILABLE
            else -> realSeat?.state?:SeatState.UNAVAILABLE
        }
        mapView.refresh()
        seatClickedListener.invoke(realSeat?:seat)
    }

    private fun findSeat(mySeat: Seat): Seat? {
        return seats.firstOrNull { mySeat.name == it.name }
    }

    fun resetAllSeats() {
        for (seat in seats) {
            seat.state = when (seat.state) {
                SeatState.SELECTED -> SeatState.AVAILABLE
                else -> seat.state
            }
        }
    }
}