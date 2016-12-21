package com.vitorprado.schematicmap.sector

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import com.vitorprado.schematicmap.ImprovedMapView

class SectorMapView : ImprovedMapView {

    private var sectorsLayer: SectorLayer? = null
    var sectorColor: Int? = null
    var sectorAlpha: Int? = null
    var sectorPaintMode: PorterDuff.Mode? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun loadMap(sectors: List<Sector>, sectorSelectedListener: (Sector) -> Any?) {
        this.isScaleAndRotateTogether = true
        sectorsLayer = SectorLayer(this, sectors, sectorSelectedListener)
        sectorsLayer!!.setLevel(Int.MAX_VALUE)
        addLayer(sectorsLayer)
        refresh()
    }

    fun selectSector(sector: Sector) {
        selectSector(sector.id)
        refresh()
    }

    fun selectSector(sectorId: Int) {
        sectorsLayer?.selectSector(sectorId)
        refresh()
    }
}
