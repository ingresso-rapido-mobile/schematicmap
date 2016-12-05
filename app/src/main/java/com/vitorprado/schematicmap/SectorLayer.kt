package com.vitorprado.schematicmap

import android.graphics.*
import android.view.MotionEvent
import com.onlylemi.mapview.library.layer.MapBaseLayer

class SectorLayer(val sectorMapView: SectorMapView, val sectors: List<Sector>, val sectorSelectedListener: (Sector) -> Any?) : MapBaseLayer(sectorMapView) {

    private var clip: Region? = null

    override fun draw(canvas: Canvas?, matrix: Matrix?, v: Float, v1: Float) {
        clip = canvas?.let { Region(0, 0, canvas.width, canvas.height) }
        val paint = Paint()
        paint.color = sectorMapView.sectorColor?:Color.CYAN
        paint.alpha = sectorMapView.sectorAlpha?:(255 * 0.7).toInt()
        paint.style = Paint.Style.FILL
        if (sectorMapView.sectorPaintMode != null) paint.xfermode = PorterDuffXfermode(sectorMapView.sectorPaintMode)

        canvas?.save()
        canvas?.matrix = matrix
        for (it in sectors) {
            if (it.visible) canvas?.drawPath(it.path, paint)
        }
        canvas?.restore()
    }

    override fun onTouch(event: MotionEvent?) {
        if (hasMoved(Pair(event?.x?:0f, event?.y?:0f))) return
        val clickPoints = event?.let { mapView.convertMapXYToScreenXY(event.x, event.y) }
        checkIFClickedInSector(clickPoints)
    }

    private fun hasMoved(points: Pair<Float, Float>): Boolean {
        return !((points.first in (sectorMapView.downEvent.first - 10f)..(sectorMapView.downEvent.first + 10f)) && (points.second in (sectorMapView.downEvent.second - 10f)..(sectorMapView.downEvent.second + 10f)))
    }

    private fun checkIFClickedInSector(clickPoints: FloatArray?) {
        val clickRegion = Region()
        clickRegion.setPath(createClickPath(clickPoints), clip)

        for (it in sectors) {
            val region = Region()
            region.setPath(it.path, clip)
            val found = clickPoints?.let { region.contains(it[0].toInt(), it[1].toInt()) }
            if (found?:false) {
                selectSector(it.id)
                mapView.refresh()
                sectorSelectedListener.invoke(it)
                return
            }
        }
    }

    private fun createClickPath(clickPoints: FloatArray?): Path {
        val path = Path()
        path.moveTo((clickPoints?.get(0)?.minus(10)) as Float, clickPoints?.get(1)?.minus(10) as Float)
        path.lineTo((clickPoints?.get(0)?.minus(10)) as Float, clickPoints?.get(1)?.minus(10) as Float)
        path.lineTo((clickPoints?.get(0)?.plus(10))  as Float, clickPoints?.get(1)?.minus(10) as Float)
        path.lineTo((clickPoints?.get(0)?.plus(10))  as Float, clickPoints?.get(1)?.plus(10)  as Float)
        path.lineTo((clickPoints?.get(0)?.minus(10)) as Float, clickPoints?.get(1)?.plus(10)  as Float)
        path.lineTo((clickPoints?.get(0)?.minus(10)) as Float, clickPoints?.get(1)?.minus(10) as Float)
        return path
    }

    fun selectSector(sectorId: Int) {
        for (sector in sectors) sector.visible = false
        sectors.find{ it.id == sectorId }?.visible = true
    }
}