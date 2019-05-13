package com.cna.mineru.cna;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.cna.mineru.cna.DB.GraphDataSQLClass;
import com.cna.mineru.cna.DB.UserSQLClass;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_page);

        UserSQLClass db = new UserSQLClass(SplashActivity.this);
        GraphDataSQLClass c_db = new GraphDataSQLClass(SplashActivity.this);

        final LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        final ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(1600);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationView.setProgress((Float) animation.getAnimatedValue());
            }
        });

        animator.start();
        Handler hd = new Handler();
        SharedPreferences pref = getSharedPreferences("isFirst", MODE_PRIVATE);
        boolean first = pref.getBoolean("isFirst", false);
        if(!first){
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", true);
            editor.apply();
            // user 추가 시 테이블 오류 발생
            db.add_values(1,"게스트",1, 0);
            c_db.first();
            hd.postDelayed(new splash_handler_Login(), 1100);
        }else{
            SharedPreferences pref2 = getSharedPreferences("isLogin", MODE_PRIVATE);
            boolean login = pref2.getBoolean("isLogin",false);
            if(!login){
                //login Activity 실행
                hd.postDelayed(new splash_handler_Login(), 1100);
            }else{
                //MainActivity 실행
                hd.postDelayed(new splash_handler_Main(), 1100);
            }
        }
    }

    private class splash_handler_Login implements Runnable{
        public void run(){
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    private class splash_handler_Main implements Runnable{
        public void run(){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
