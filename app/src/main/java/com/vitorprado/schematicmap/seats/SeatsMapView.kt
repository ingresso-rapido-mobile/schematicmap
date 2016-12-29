package com.vitorprado.schematicmap.seats

import android.content.Context
import android.util.AttributeSet
import com.vitorprado.schematicmap.ImprovedMapView
import java.util.*

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

    fun selectSeat(receivedSeat: Seat) {
        changeSeatStatus(receivedSeat, SeatState.SELECTED)
    }

    fun selectSeat(receivedSeat: Long) {
        changeSeatStatus(receivedSeat, SeatState.SELECTED)
    }

    fun deselectSeat(receivedSeat: Seat) {
        changeSeatStatus(receivedSeat, SeatState.AVAILABLE)
    }

    fun resetAllSeats() {
        seatsLayer?.resetAllSeats()
    }

    private fun changeSeatStatus(receivedSeat: Seat, state: SeatState) {
        for (seat in seatsLayer?.seats?:ArrayList<Seat>()) {
            if (seat.id == receivedSeat.id) {
                seat.state = state
            }
        }
    }

    private fun changeSeatStatus(receivedSeat: Long, state: SeatState) {
        for (seat in seatsLayer?.seats?:ArrayList<Seat>()) {
            if (seat.id == receivedSeat) {
                seat.state = state
            }
        }
    }
}