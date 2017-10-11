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
        try {
            this.post { refresh() }
        } catch (ignored: Exception) {}
    }

    fun selectSeat(receivedSeat: Seat) {
        changeSeatStatus(receivedSeat, SeatState.SELECTED)
    }

    fun selectSeat(receivedSeat: String) {
        changeSeatStatus(receivedSeat, SeatState.SELECTED)
    }

    fun highlightSeat(receivedSeat: String) {
        changeSeatStatus(receivedSeat, SeatState.HIGHLIGHTED)
    }

    fun deselectSeat(receivedSeat: Seat) {
        changeSeatStatus(receivedSeat, SeatState.AVAILABLE)
    }

    fun resetAllSeats() {
        seatsLayer?.resetAllSeats()
    }

    fun resetAllSeats(selectedSeats: List<String>) {
        seatsLayer?.resetAllSeats()
        selectedSeats.forEach { selectSeat(it) }
    }

    private fun changeSeatStatus(receivedSeat: Seat, state: SeatState) {
        for (seat in seatsLayer?.seats?:ArrayList<Seat>()) {
            if (seat.id == receivedSeat.id) {
                seat.state = state
            }
        }
    }

    private fun changeSeatStatus(receivedSeat: String, state: SeatState) {
        for (seat in seatsLayer?.seats?:ArrayList<Seat>()) {
            if (seat.name == receivedSeat) {
                seat.state = state
            }
        }
    }
}