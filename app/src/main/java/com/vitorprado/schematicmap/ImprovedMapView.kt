package com.vitorprado.schematicmap

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.onlylemi.mapview.library.MapView

private const val TOP = 0
private const val BOTTOM = 1
private const val RIGHT = 2
private const val LEFT = 3
private const val TOUCH_STATE_NO = 0
private const val TOUCH_STATE_SCROLL = 1
private const val TOUCH_STATE_SCALE = 2
private const val TOUCH_STATE_ROTATE = 3
private const val TOUCH_STATE_TWO_POINTED = 4

open class ImprovedMapView : MapView {

    var downEvent: Pair<Float, Float> = Pair(0f, 0f)
    private var maxX: Float = 10000f
    private var maxY: Float = 10000f
    private var minZoomModifier = 0f
    private var maxZoomModifier = 0f
    private var lastXPosition: Float = 0f
    private var lastYPosition: Float = 0f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setMaxDraggingCoordinates(x: Float, y: Float) {
        this.maxX = x
        this.maxY = y
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> downEvent = Pair(event?.x ?: 0f, event?.y ?: 0f)
        }
        return superClassTouchModified(event!!)
    }

    private fun superClassTouchModified(event: MotionEvent): Boolean {
        if (!this.isMapLoadFinish) {
            return false
        } else {
            when (event.action and 255) {
                MotionEvent.ACTION_DOWN -> {
                    this.saveMatrix.set(this.currentMatrix)
                    this.startTouch.set(event.x, event.y)
                    this.currentTouchState = TOUCH_STATE_SCROLL
                }
                MotionEvent.ACTION_UP -> {
                    if (this.withFloorPlan(event.getX(), event.getY())) {
                        val scale1 = this.layers.iterator()
                        while (scale1.hasNext()) {
                            scale1.next().onTouch(event)
                        }
                    }
                    this.currentTouchState = TOUCH_STATE_NO
                }
                MotionEvent.ACTION_MOVE -> {
                    val newDist: Float
                    val newDegree: Float
                    var scale: Float
                    val rotate: Float

                    when (this.currentTouchState) {
                        TOUCH_STATE_SCROLL -> {
                            this.currentMatrix.set(this.saveMatrix)
                            if (canDragX(event)) lastXPosition = event.x - this.startTouch.x
                            if (canDragY(event)) lastYPosition = event.y - this.startTouch.y
                            this.currentMatrix.postTranslate(lastXPosition, lastYPosition)
                            this.refresh()
                        }
                        TOUCH_STATE_SCALE -> {
                            this.currentMatrix.set(this.saveMatrix)
                            newDist = this.distance(event, this.mid)
                            scale = newDist / this.oldDist
                            if (minZoomModifier == 0f && maxZoomModifier == 0f) {
                                minZoomModifier = this.saveZoom
                                maxZoomModifier = this.saveZoom + .7f
                                setMinZoom(minZoomModifier)
                                setMaxZoom(maxZoomModifier)
                            }
                            if (scale * this.saveZoom < this.minZoom) {
                                scale = this.minZoom / this.saveZoom
                            } else if (scale * this.saveZoom > this.maxZoom) {
                                scale = this.maxZoom / this.saveZoom
                            }

                            this.currentZoom = scale * this.saveZoom
                            this.currentMatrix.postScale(scale, scale, this.mid.x, this.mid.y)
                            this.refresh()
                        }
                        TOUCH_STATE_ROTATE -> {
                            this.currentMatrix.set(this.saveMatrix)
                            newDegree = this.rotation(event, this.mid)
                            rotate = newDegree - this.oldDegree
                            this.currentRotateDegrees = (rotate + this.saveRotateDegrees) % 360.0f
                            this.currentRotateDegrees = if (this.currentRotateDegrees > 0.0f) this.currentRotateDegrees
                            else this.currentRotateDegrees + 360.0f
                            this.refresh()
                        }
                        TOUCH_STATE_TWO_POINTED -> {
                            this.oldDist = this.distance(event, this.mid)
                            this.currentTouchState = TOUCH_STATE_SCALE
                        }
                    }
                }
                MotionEvent.ACTION_POINTER_DOWN -> if (event.pointerCount == 2) {
                    this.saveMatrix.set(this.currentMatrix)
                    this.saveZoom = this.currentZoom
                    this.saveRotateDegrees = this.currentRotateDegrees
                    this.startTouch.set(event.getX(0), event.getY(0))
                    this.currentTouchState = TOUCH_STATE_TWO_POINTED
                    this.mid = this.midPoint(event)
                    this.oldDist = this.distance(event, this.mid)
                    this.oldDegree = this.rotation(event, this.mid)
                }
                MotionEvent.ACTION_POINTER_UP -> this.currentTouchState = TOUCH_STATE_NO
                else -> {}
            }
            return true
        }
    }

    private fun canDragX(event: MotionEvent): Boolean {
        val currentX: Float
        val checkingMatrix = Matrix()
        val newMapWidth = when {
            Math.round(mapWidth / maxX) >= 2 -> mapWidth / Math.round(mapWidth / maxX)
            else -> mapWidth
        }
        checkingMatrix.set(this.saveMatrix)
        checkingMatrix.postTranslate(event.x - this.startTouch.x, event.y - this.startTouch.y)
        val values = FloatArray(9)
        checkingMatrix.getValues(values)
        currentX = values[Matrix.MTRANS_X]
        val direction = when {
            event.x - this.startTouch.x < 0 -> LEFT
            event.x - this.startTouch.x > 0 -> RIGHT
            else -> -1
        }

        if (currentZoom > minZoomModifier && minZoomModifier > 0f) {
            val mapWidthWithScale = mapWidth * currentZoom
            return when (direction) {
                RIGHT -> currentX < 0
                else -> currentX > (mapWidthWithScale - maxX) * -1
            }
        } else {
            return when (direction) {
                RIGHT -> currentX < 0
                else -> currentX > (newMapWidth - maxX) * -1
            }
        }
    }

    private fun canDragY(event: MotionEvent): Boolean {
        val currentY: Float
        val checkingMatrix = Matrix()
        val newMapHeigth = when {
            Math.round(mapHeight / maxY) >= 2 -> mapHeight / Math.round(mapHeight / maxY)
            else -> mapHeight
        }
        checkingMatrix.set(this.saveMatrix)
        checkingMatrix.postTranslate(event.x - this.startTouch.x, event.y - this.startTouch.y)
        val values = FloatArray(9)
        checkingMatrix.getValues(values)
        currentY = values[Matrix.MTRANS_Y]
        val direction = when {
            event.y - this.startTouch.y < 0 -> TOP
            event.y - this.startTouch.y > 0 -> BOTTOM
            else -> -1
        }

        if (currentZoom > minZoomModifier && minZoomModifier > 0f) {
            val mapHeightWithScale = mapHeight * currentZoom
            return when (direction) {
                TOP -> currentY > if (mapHeightWithScale < maxY) 0f else (mapHeightWithScale - maxY) * -1
                else -> if (mapHeightWithScale > maxY) currentY < 0 else currentY < maxY - mapHeightWithScale
            }
        } else {
            return when (direction) {
                TOP -> currentY > 0
                else -> currentY < (newMapHeigth - maxY) * -1
            }
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
    }
}
