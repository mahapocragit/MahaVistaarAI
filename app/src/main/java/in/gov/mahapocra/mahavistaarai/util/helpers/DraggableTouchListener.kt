package `in`.gov.mahapocra.mahavistaarai.util.helpers

import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class DraggableTouchListener(
    private val onClick: () -> Unit
) : View.OnTouchListener {

    private var dX = 0f
    private var dY = 0f
    private var startX = 0f
    private var startY = 0f

    companion object {
        private const val CLICK_THRESHOLD = 20
        private const val MARGIN = 32
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val parent = v.parent as View

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dX = v.x - event.rawX
                dY = v.y - event.rawY
                startX = event.rawX
                startY = event.rawY
            }

            MotionEvent.ACTION_MOVE -> {
                val boundedX = (event.rawX + dX)
                    .coerceIn(MARGIN.toFloat(), (parent.width - v.width - MARGIN).toFloat())

                val boundedY = (event.rawY + dY)
                    .coerceIn(MARGIN.toFloat(), (parent.height - v.height - MARGIN).toFloat())

                v.x = boundedX
                v.y = boundedY
            }

            MotionEvent.ACTION_UP -> {
                if (
                    abs(event.rawX - startX) < CLICK_THRESHOLD &&
                    abs(event.rawY - startY) < CLICK_THRESHOLD
                ) {
                    onClick.invoke()
                }
            }
        }
        return true
    }
}
