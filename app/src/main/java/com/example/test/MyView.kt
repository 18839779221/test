package com.example.test

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.view.animation.Interpolator
import android.widget.Toast
import kotlin.math.sqrt

class MyView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var gd: GestureDetector? = null
    var listener: GestureDetectorListener? = null
    var touchSlop: Int = 0

    var onClick: () -> Unit = {}

    var onScroll: (deltaX: Int, deltaY: Int) -> Unit = { _: Int, _: Int -> }

    var velocityTracker: VelocityTracker? = null

    init {
        listener = GestureDetectorListener(this)
        gd = GestureDetector(context, listener)
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    private var downX = 0
    private var downY = 0
    private var lastX = 0
    private var lastY = 0


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        val x = event.rawX.toInt()
        val y = event.rawY.toInt()

        if(velocityTracker == null){
            velocityTracker = VelocityTracker.obtain()
        }

        var eventAddedToVelocityTracker = false
        val vevt = MotionEvent.obtain(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = x
                downY = y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = lastX - x
                val deltaY = lastY - y
                if (deltaX != 0 || deltaY != 0) {
                    onScroll(0, deltaY)
                }
            }
            MotionEvent.ACTION_UP -> {
                velocityTracker?.addMovement(vevt)
                eventAddedToVelocityTracker = true
                velocityTracker?.computeCurrentVelocity(1000,
                    ViewConfiguration.get(context).scaledMaximumFlingVelocity.toFloat()
                )
                if (sqrt(((x - downX) * (x - downX) + (y - downY) * (y - downY)).toDouble()) < touchSlop) {
                    onClick()
                }
            }
        }
        lastX = x
        lastY = y
        return true
    }


    private val interpolator = Interpolator { input ->
        val t = input - 1f
        t * t * t * t * t + 1f
    }

    class GestureDetectorListener(val view: View) : GestureDetector.OnGestureListener {
        override fun onDown(e: MotionEvent?): Boolean {
            //Toast.makeText(view.context, "onDown",Toast.LENGTH_SHORT).show()
            return true
        }

        override fun onShowPress(e: MotionEvent?) {
            //Toast.makeText(view.context, "onShowPress",Toast.LENGTH_SHORT).show()
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            //Toast.makeText(view.context, "onSingleTapUp",Toast.LENGTH_SHORT).show()
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (e1 == null) {
                return false
            }
            view.translationX = e1.x
            view.translationY = e1.y
            //Toast.makeText(view.context, "onScroll",Toast.LENGTH_SHORT).show()
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            //Toast.makeText(view.context, "onLongPress",Toast.LENGTH_SHORT).show()
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            Toast.makeText(view.context, "onFling", Toast.LENGTH_SHORT).show()
            return true
        }

    }

}