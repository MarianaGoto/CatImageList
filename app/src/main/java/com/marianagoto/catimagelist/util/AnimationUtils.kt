package com.marianagoto.catimagelist.util
import android.animation.ValueAnimator
import android.animation.ValueAnimator.*
import android.view.View

object AnimationUtils {
    fun animatePop(
        view: View,
        scaleFrom: Float = 1f,
        scaleTo: Float = 1.3f,
        duration: Long = 120
    ) {
        view.animate()
            .scaleX(scaleTo)
            .scaleY(scaleTo)
            .setDuration(duration)
            .withEndAction {
                view.animate()
                    .scaleX(scaleFrom)
                    .scaleY(scaleFrom)
                    .setDuration(duration)
                    .start()
            }
            .start()
    }

    fun animateBounceScale(view: View) {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 400

        animator.addUpdateListener {
            val value = it.animatedValue as Float
            // Usa seno para criar efeito de "pulsação"
            val scale = 1f + (0.3f * Math.sin(value * Math.PI * 2)).toFloat()
            view.scaleX = scale
            view.scaleY = scale
        }
        animator.start()
    }


}