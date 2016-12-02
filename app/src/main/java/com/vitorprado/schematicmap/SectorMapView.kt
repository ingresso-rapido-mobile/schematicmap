package com.vitorprado.schematicmap

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import com.onlylemi.mapview.library.MapView

class SectorMapView : MapView {

    private var sectorsLayer: SectorLayer? = null
    var downEvent: Pair<Float, Float> = Pair(0f, 0f)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun loadMap(sectors: List<Sector>, sectorSelectedListener: (Sector) -> Any?) {
        sectorsLayer = SectorLayer(this, sectors, sectorSelectedListener)
        sectorsLayer!!.setLevel(Int.MAX_VALUE)
        sectorsLayer?.sectors?.first()?.visible = true
        sectorSelectedListener.invoke(sectorsLayer!!.sectors.first())
        addLayer(sectorsLayer)
        refresh()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> downEvent = Pair(event?.x?:0f, event?.y?:0f)
        }
        return super.onTouchEvent(event)
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
    }

    fun selectSector(sector: Sector) {
        selectSector(sector.id)
    }

    fun selectSector(sectorId: Int) {
        sectorsLayer?.selectSector(sectorId)
        refresh()
    }
}
