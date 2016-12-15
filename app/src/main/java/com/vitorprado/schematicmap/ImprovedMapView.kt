package com.vitorprado.schematicmap

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import com.onlylemi.mapview.library.MapView

open class ImprovedMapView : MapView {

    var downEvent: Pair<Float, Float> = Pair(0f, 0f)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> downEvent = Pair(event?.x?:0f, event?.y?:0f)
        }
        return super.onTouchEvent(event)
    }

    override fun draw(canvas: Canvas?) {
        setCurrentRotateDegrees(0f, mapHeight / 2, mapWidth / 2)
        super.draw(canvas)
    }
}
