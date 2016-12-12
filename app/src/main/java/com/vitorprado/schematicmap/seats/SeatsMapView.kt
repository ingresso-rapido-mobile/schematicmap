package com.vitorprado.schematicmap.seats

import android.content.Context
import android.util.AttributeSet
import com.vitorprado.schematicmap.ImprovedMapView

class SeatsMapView : ImprovedMapView {

    private var seatsLayer: SeatsLayer? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun loadMap(seats: List<Seat>, seatSelectedListener: (Seat) -> Any?) {
        this.isScaleAndRotateTogether = true
        seatsLayer = SeatsLayer(this, seats, seatSelectedListener)
        seatsLayer!!.setLevel(Int.MAX_VALUE)
        addLayer(seatsLayer)
        refresh()
    }
}