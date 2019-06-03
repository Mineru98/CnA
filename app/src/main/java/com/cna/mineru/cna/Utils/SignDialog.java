package com.cna.mineru.cna.Utils;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.airbnb.lottie.LottieAnimationView;
import com.cna.mineru.cna.R;

import java.util.Objects;

public class SignDialog {

    private Context context;
    private Dialog dlg;

    public SignDialog(Context context) {
        this.context = context;
    }

    public void call_btn_ok() {
        dlg = new Dialog(context);
        Objects.requireNonNull(dlg.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dlg.setCancelable(false);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.anim_dialog_o);
        dlg.show();
        final LottieAnimationView animationView = (LottieAnimationView) dlg.findViewById(R.id.animation_view);
        final ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(1800);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationView.setProgress((Float) animation.getAnimatedValue());
            }
        });
        animator.start();
        Handler hd = new Handler();
        hd.postDelayed(new run_over(), 1800);
    }

    public void call_btn_x() {
        dlg = new Dialog(context);
        Objects.requireNonNull(dlg.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dlg.setCancelable(false);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.anim_dialog_x);
        dlg.show();
        final LottieAnimationView animationView = (LottieAnimationView) dlg.findViewById(R.id.animation_view);
        final ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(1200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationView.setProgress((Float) animation.getAnimatedValue());
            }
        });
        animator.start();
        Handler hd = new Handler();
        hd.postDelayed(new run_over(), 1300);
    }

    private class run_over implements Runnable{
        public void run(){
            dlg.dismiss();
        }
    }
}
