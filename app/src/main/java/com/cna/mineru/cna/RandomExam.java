package com.cna.mineru.cna;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cna.mineru.cna.DB.ExamSQLClass;

public class RandomExam extends AppCompatActivity {

    private TextView time_out;
    private TextView textView;
    private long settingtime;
    private int[] testArr;
    private long myBaseTime;
    private ExamSQLClass db;
    private int count = 0;
    private int count_solve = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);
        db = new ExamSQLClass(this);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        time_out = (TextView) findViewById(R.id.time_out);
        textView = (TextView) findViewById(R.id.textView);
        Button btn_no = (Button) findViewById(R.id.btn_no);
        Button btn_ok = (Button) findViewById(R.id.btn_ok);

        settingtime = getIntent().getLongExtra("time",0);
        count_solve = getIntent().getIntExtra("isOkay", 0);
        testArr = getIntent().getIntArrayExtra("testArr");

        myBaseTime = SystemClock.elapsedRealtime();//처음 시간 기록
        myTimer.sendEmptyMessage(0);
        textView.setText(String.valueOf(testArr[count]));


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count_solve++;
                count++;
                if(count==4){
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("result",count_solve);
                    setResult(RESULT_OK,resultIntent);
                    myTimer.removeMessages(0);
                    finish();
                }else{
                    if(count!=5)
                        textView.setText(String.valueOf(testArr[count]));
                }
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count==4){
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("result",count_solve);
                    setResult(RESULT_OK,resultIntent);
                    myTimer.removeMessages(0);
                    finish();
                }else{
                    if(count!=5)
                        textView.setText(String.valueOf(testArr[count]));
                }
            }
        });

    }


    @SuppressLint("HandlerLeak")
    Handler myTimer = new Handler(){
        public void handleMessage(Message msg){
            time_out.setText(getTimeOut());
            myTimer.sendEmptyMessage(0);
        }
    };

    //현재시간을 계속 구해서 출력하는 메소드
    String getTimeOut(){
        long now = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        long outTime = settingtime -(now - myBaseTime);
        @SuppressLint("DefaultLocale")
        String easy_outTime = String.format("%02d:%02d:%02d",outTime/1000 / 60 / 60, (outTime/1000 / 60)%60, (outTime/1000)%60);
        if(easy_outTime.equals("00:00:00")){
            myTimer.removeMessages(0);
        }
        return easy_outTime;

    }
}

