package com.cna.mineru.cna.Adapter

import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

import java.io.PrintWriter
import java.io.StringWriter

/*
    Fragment를 사용하는 Activity들에 연결 된 ViewPager
    (MainActivity, ExamActivity)
 */

class CustomViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    private val enabled: Boolean //이 것이 스크롤을 막아주는 중요 변수!

    init {
        this.enabled = false
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

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.enabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }
}

