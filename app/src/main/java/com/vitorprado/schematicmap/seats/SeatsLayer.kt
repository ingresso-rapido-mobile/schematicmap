package com.vitorprado.schematicmap.seats

import android.graphics.*
import android.view.MotionEvent
import com.onlylemi.mapview.library.layer.MapLayer
import com.vitorprado.schematicmap.ImprovedMapView

class SeatsLayer(val seatsMapView: ImprovedMapView, val seats: List<Seat>, val seatClickedListener: (Seat) -> Any?) : MapLayer(seatsMapView) {

    private var clip: Region? = null

    override fun draw(canvas: Canvas?, currentMatrix: Matrix?, currentZoom: Float, currentRotateDegrees: Float) {
        clip = canvas?.let { Region(0, 0, canvas.width, canvas.height) }
        val paint = Paint()
        paint.style = Paint.Style.FILL

        canvas?.save()
        canvas?.matrix = currentMatrix
        for (it in seats) {
            paint.color = when (it.state) {
                SeatState.AVAILABLE -> Color.GREEN
                SeatState.SELECTED -> Color.BLUE
                SeatState.UNAVAILABLE -> Color.GRAY
            }
            it.draw(canvas, paint)
        }
        canvas?.restore()
    }

    override fun onTouch(event: MotionEvent?) {
        if (hasMoved(Pair(event?.x?:0f, event?.y?:0f))) return
        val clickPoints = event?.let { mapView.convertMapXYToScreenXY(event.x, event.y) }
        checkIFClickedInSector(clickPoints)
    }

    private fun hasMoved(points: Pair<Float, Float>): Boolean {
        return !((points.first in (seatsMapView.downEvent.first - 10f)..(seatsMapView.downEvent.first + 10f)) && (points.second in (seatsMapView.downEvent.second - 10f)..(seatsMapView.downEvent.second + 10f)))
    }

    private fun checkIFClickedInSector(clickPoints: FloatArray?) {
        val clickBounds = RectF()
        createClickPath(clickPoints).computeBounds(clickBounds, false)

        for (it in seats) {
            if (it.state == SeatState.UNAVAILABLE) continue

            val seatBounds = RectF()
            it.path.computeBounds(seatBounds, false)
            if (seatBounds.intersect(clickBounds)) {
                selectSeat(it)
                return
            }
        }
    }

    private fun selectSeat(seat: Seat) {
        if (seat.state == SeatState.UNAVAILABLE) return
        seat.state = when (seat.state) {
            SeatState.AVAILABLE -> SeatState.SELECTED
            SeatState.SELECTED -> SeatState.AVAILABLE
            SeatState.UNAVAILABLE -> SeatState.UNAVAILABLE
        }
        mapView.refresh()
        seatClickedListener.invoke(seat)
    }

    private fun createClickPath(clickPoints: FloatArray?): Path {
        val path = Path()
        path.moveTo((clickPoints?.get(0)?.minus(5)) as Float, clickPoints?.get(1)?.minus(5) as Float)
        path.lineTo((clickPoints?.get(0)?.minus(5)) as Float, clickPoints?.get(1)?.minus(5) as Float)
        path.lineTo((clickPoints?.get(0)?.plus(5))  as Float, clickPoints?.get(1)?.minus(5) as Float)
        path.lineTo((clickPoints?.get(0)?.plus(5))  as Float, clickPoints?.get(1)?.plus(5)  as Float)
        path.lineTo((clickPoints?.get(0)?.minus(5)) as Float, clickPoints?.get(1)?.plus(5)  as Float)
        path.lineTo((clickPoints?.get(0)?.minus(5)) as Float, clickPoints?.get(1)?.minus(5) as Float)
        return path
    }
}