package `in`.gov.mahapocra.mahavistaarai.util

import android.view.View

object ScoreBubbleHelper {

    fun showScoreBubble(view: View, message: String) {
//        // We'll launch on Main since UI updates must be on main thread
//        CoroutineScope(Dispatchers.Main).launch {
//            // Optional delay if you need timing
//            delay(500)
//
//            // Create a TextView dynamically for the floating text
//            val textView = TextView(view.context).apply {
//                text = message
//                textSize = 24f
//                setTextColor(Color.WHITE)
//                setTypeface(null, Typeface.BOLD)
//                setShadowLayer(8f, 0f, 0f, Color.BLACK)
//            }
//
//            // Create layout params for position (center by default)
//            val params = FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                gravity = Gravity.CENTER
//            }
//
//            // Find a parent layout (view must be inside FrameLayout or similar)
//            val parent = view.rootView.findViewById<ViewGroup>(android.R.id.content)
//            parent.addView(textView, params)
//
//            // Animate it upwards + fade out
//            textView.animate()
//                .translationYBy(-200f)
//                .alpha(0f)
//                .setDuration(1200)
//                .setInterpolator(AccelerateDecelerateInterpolator())
//                .withEndAction {
//                    parent.removeView(textView)
//                }
//                .start()
//        }
    }
}
