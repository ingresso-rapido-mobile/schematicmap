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
        return superClassTouchModified(event!!)
    }

    private fun superClassTouchModified(event: MotionEvent): Boolean {
        if (!this.isMapLoadFinish) {
            return false
        } else {
            when (event.action and 255) {
                0 -> {
                    this.saveMatrix.set(this.currentMatrix)
                    this.startTouch.set(event.x, event.y)
                    this.currentTouchState = 1
                }
                1 -> {
                    if (this.withFloorPlan(event.getX(), event.getY())) {
                        val scale1 = this.layers.iterator()

                        while (scale1.hasNext()) {
                            scale1.next().onTouch(event)
                        }
                    }

                    this.currentTouchState = 0
                }
                2 -> {
                    val newDist: Float
                    val newDegree: Float
                    var scale: Float
                    val rotate: Float
                    when (this.currentTouchState) {
                        1 -> {
                            this.currentMatrix.set(this.saveMatrix)
                            this.currentMatrix.postTranslate(event.x - this.startTouch.x, event.y - this.startTouch.y)
                            this.refresh()
                        }
                        2 -> {
                            this.currentMatrix.set(this.saveMatrix)
                            newDist = this.distance(event, this.mid)
                            scale = newDist / this.oldDist
                            if (scale * this.saveZoom < this.minZoom) {
                                scale = this.minZoom / this.saveZoom
                            } else if (scale * this.saveZoom > this.maxZoom) {
                                scale = this.maxZoom / this.saveZoom
                            }

                            this.currentZoom = scale * this.saveZoom
                            this.currentMatrix.postScale(scale, scale, this.mid.x, this.mid.y)
                            this.refresh()
                        }
                        3 -> {
                            this.currentMatrix.set(this.saveMatrix)
                            newDegree = this.rotation(event, this.mid)
                            rotate = newDegree - this.oldDegree
                            this.currentRotateDegrees = (rotate + this.saveRotateDegrees) % 360.0f
                            this.currentRotateDegrees = if (this.currentRotateDegrees > 0.0f) this.currentRotateDegrees else this.currentRotateDegrees + 360.0f
//                            this.currentMatrix.postRotate(rotate, this.mid.x, this.mid.y)
                            this.refresh()
                        }
                        4 -> {
                            this.oldDist = this.distance(event, this.mid)
                            this.currentTouchState = 2
                        }
                    }
                }
                5 -> if (event.pointerCount == 2) {
                    this.saveMatrix.set(this.currentMatrix)
                    this.saveZoom = this.currentZoom
                    this.saveRotateDegrees = this.currentRotateDegrees
                    this.startTouch.set(event.getX(0), event.getY(0))
                    this.currentTouchState = 4
                    this.mid = this.midPoint(event)
                    this.oldDist = this.distance(event, this.mid)
                    this.oldDegree = this.rotation(event, this.mid)
                }
                6 -> this.currentTouchState = 0
                else -> { }
            }

            return true
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
    }
}
