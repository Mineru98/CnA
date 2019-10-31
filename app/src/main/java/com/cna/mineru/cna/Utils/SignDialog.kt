package com.cna.mineru.cna.Utils

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager

import com.airbnb.lottie.LottieAnimationView
import com.cna.mineru.cna.R

import java.util.Objects

class SignDialog(private val context: Context) {
    private var dlg: Dialog? = null

    fun call_btn_ok() {
        dlg = Dialog(context)
        Objects.requireNonNull<Window>(dlg!!.window)
            .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dlg!!.setCancelable(false)
        dlg!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg!!.setContentView(R.layout.anim_dialog_o)
        dlg!!.show()
        val animationView = dlg!!.findViewById<View>(R.id.animation_view) as LottieAnimationView
        val animator = ValueAnimator.ofFloat(0f, 1f).setDuration(1800)
        animator.addUpdateListener { animation ->
            animationView.progress = animation.animatedValue as Float
        }
        animator.start()
        val hd = Handler()
        hd.postDelayed(run_over(), 1800)
    }

    fun call_btn_x() {
        dlg = Dialog(context)
        Objects.requireNonNull<Window>(dlg!!.window)
            .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dlg!!.setCancelable(false)
        dlg!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg!!.setContentView(R.layout.anim_dialog_x)
        dlg!!.show()
        val animationView = dlg!!.findViewById<View>(R.id.animation_view) as LottieAnimationView
        val animator = ValueAnimator.ofFloat(0f, 1f).setDuration(1200)
        animator.addUpdateListener { animation ->
            animationView.progress = animation.animatedValue as Float
        }
        animator.start()
        val hd = Handler()
        hd.postDelayed(run_over(), 1300)
    }

    private inner class run_over : Runnable {
        override fun run() {
            dlg!!.dismiss()
        }
    }
}
