package `in`.gov.mahapocra.mahavistaarai.util.helpers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

object AnimationHelper {

    fun shrinkLeftToCenter(view: View) {
        // Shrink to center (from top-left corner back to center)
        val shrinkToCenter = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.7f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.7f),
                ObjectAnimator.ofFloat(view, "translationX", -50f, 0f), // move right (back to center)
                ObjectAnimator.ofFloat(view, "translationY", -50f, 0f)  // move down (back to center)
            )
            duration = 600
        }

        // Expand to top-left corner
        val expandToTopLeft = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 0.7f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0.7f, 1f),
                ObjectAnimator.ofFloat(view, "translationX", 0f, -50f), // move left
                ObjectAnimator.ofFloat(view, "translationY", 0f, -50f)  // move up
            )
            duration = 600
        }

        val fullSet = AnimatorSet().apply {
            playSequentially(shrinkToCenter, expandToTopLeft)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    shrinkLeftToCenter(view) // Loop forever
                }
            })
        }
        fullSet.start()
    }

    fun shrinkToCenter(view: View) {
        // Shrink and move down
        val shrinkDown = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.7f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.7f),
                ObjectAnimator.ofFloat(view, "translationY", 0f, 50f) // move down 50px
            )
            duration = 600
        }

        // Expand and move up
        val expandUp = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 0.7f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0.7f, 1f),
                ObjectAnimator.ofFloat(view, "translationY", 50f, 0f) // move back to original
            )
            duration = 600
        }

        val fullSet = AnimatorSet().apply {
            playSequentially(shrinkDown, expandUp)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    shrinkToCenter(view) // Recursively continue the animation
                }
            })
        }

        fullSet.start()
    }

}