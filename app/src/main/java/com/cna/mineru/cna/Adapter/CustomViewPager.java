package com.cna.mineru.cna.Adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.io.PrintWriter;
import java.io.StringWriter;

/*
    Fragment를 사용하는 Activity들에 연결 된 ViewPager
    (MainActivity, ExamActivity)
 */

public class CustomViewPager extends ViewPager {
    private boolean enabled; //이 것이 스크롤을 막아주는 중요 변수!
    
    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            if (this.enabled) {
                return super.onTouchEvent(event);
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }
}

