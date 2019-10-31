package com.cna.mineru.cna.Utils

import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.text.TextUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog

import com.airbnb.lottie.LottieAnimationView
import com.cna.mineru.cna.R

class LoadingDialog {
    internal var progressDialog: AppCompatDialog? = null

    fun progressON(activity: Activity?, message: String) {
        if (activity == null || activity.isFinishing) {
            return
        }
        if (progressDialog != null && progressDialog!!.isShowing()) {
            progressSET(message)
        } else {
            progressDialog = AppCompatDialog(activity)
            progressDialog!!.setCancelable(false)
            progressDialog!!.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            progressDialog!!.setContentView(R.layout.progress_page)
            progressDialog!!.show()
        }

        val animationView =
            progressDialog!!.findViewById<LottieAnimationView>(R.id.animation_view)
        val animator = ValueAnimator.ofFloat(0f, 1f).setDuration(1600)
        animator.addUpdateListener { animation ->
            animationView?.progress = animation.animatedValue as Float
        }

        val tv_progress_message =
            progressDialog!!.findViewById<TextView>(R.id.tv_progress_message)
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message?.text = message
        }

        val cTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                progressOFF()
            }
        }
        cTimer.start()
    }

    private fun progressSET(message: String) {
        if (progressDialog == null || !progressDialog!!.isShowing()) {
            return
        }

        val tv_progress_message =
            progressDialog!!.findViewById<TextView>(R.id.tv_progress_message)
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message?.text = message
        }
    }

    fun progressOFF() {
        if (progressDialog != null && progressDialog!!.isShowing()) {
            progressDialog!!.dismiss()
        }
    }
}
