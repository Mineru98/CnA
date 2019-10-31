package com.cna.mineru.cna.Adapter

import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

import java.io.PrintWriter
import java.io.StringWriter

/*
    RandomExam에 연결 된 ViewPager
    RandomExam

 */

class RandomViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    private val enabled: Boolean //이 것이 스크롤을 막아주는 중요 변수!

    init {
        this.enabled = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        try {
            if (this.enabled) {
                return super.onTouchEvent(event)
            }
        } catch (e: Exception) {

            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw))
        }

        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        try {
            return super.onInterceptTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }

        return false
    }
}

