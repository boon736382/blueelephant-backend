package com.pongsawad.blueelephant

import android.content.Context
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class SwipeTouchListener(
    context: Context,
    private val onSwipeLeft: () -> Unit,
    private val onSwipeRight: () -> Unit
) : View.OnTouchListener {

    private var downX = 0f
    private var downY = 0f

    private val SWIPE_THRESHOLD = 150   // how far to move before counting as swipe

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event ?: return false

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                return true
            }

            MotionEvent.ACTION_UP -> {
                val diffX = event.x - downX
                val diffY = event.y - downY

                // Only detect left/right swipes (ignore vertical movement)
                if (abs(diffX) > abs(diffY) && abs(diffX) > SWIPE_THRESHOLD) {

                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                    return true
                }
            }
        }
        return false
    }
}
