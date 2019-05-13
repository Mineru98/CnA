package com.cna.mineru.cna;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cna.mineru.cna.Adapter.RandomViewPager;
import com.cna.mineru.cna.Adapter.FragmentExampleAdapter;
import com.cna.mineru.cna.DB.ExamSQLClass;
import com.cna.mineru.cna.Fragment.ExamFragmentChild.DoExamFragmentChild.RandomExamFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class RandomExam extends AppCompatActivity {

    public int[] eachCount;
    public long[] eachBaseTime;
    public long[] eachPauseTime;
    public int CurrentViewId;

    private ViewPager viewPager;
    private TextView time_out;
    private TextView tv_count;
    private Button btn_ok;
    private AdView mAdView;

    private int setting_time;

    private int[] ExamIdArr;
    private long[] ResultExamArr;
    private long myBaseTime;
    private int count = 0;
    private int RoomId = 0;
    private boolean timeOut = false;
    public int ExamNum;
    private ExamSQLClass db;

    private ArrayList<Integer> b_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ArrayList<Integer> i_list = new ArrayList<>();
        b_list = new ArrayList<>();
        db = new ExamSQLClass(this);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mAdView = findViewById(R.id.adView);
        viewPager = (RandomViewPager) findViewById(R.id.view_pager);
        time_out = (TextView) findViewById(R.id.time_out);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        tv_count = (TextView) findViewById(R.id.tv_count);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setting_time = getIntent().getIntExtra("time",0);
        ExamIdArr = getIntent().getIntArrayExtra("ExamIdArr");
        ExamNum = getIntent().getIntExtra("ExamNum",0);
        RoomId= getIntent().getIntExtra("RoomId",0);

        eachCount = new int[ExamNum];
        eachBaseTime = new long[ExamNum];
        eachPauseTime = new long[ExamNum];
        ResultExamArr = new long[ExamNum];
        CurrentViewId = 0;

        for(int i=0;i<ExamNum;i++) {
            i_list.add(ExamIdArr[i]);
            b_list.add(0);
            eachCount[i] = 1;
            eachBaseTime[i] = 0;
            eachPauseTime[i] = 0;
        }

        eachBaseTime[0] = SystemClock.elapsedRealtime();
        eachTimer.sendEmptyMessage(0);

        myBaseTime = SystemClock.elapsedRealtime();//처음 시간 기록
        myTimer.sendEmptyMessage(0);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eachTimer.sendEmptyMessage(0);
                if(CurrentViewId==ExamNum-1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RandomExam.this);
                    builder.setTitle("시험종료");
                    builder.setMessage("시험을 완료하시겠습니까?\n'예'를 누르면 시험이 종료됩니다.");
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    myTimer.removeMessages(0);
                                    eachTimer.removeMessages(0);
                                    for(int i = 0;i<ExamNum;i++)
                                        ResultExamArr[i] = eachPauseTime[i] - eachBaseTime[i];
                                    Intent i = new Intent(RandomExam.this,RandomExamSolve.class);
                                    i.putExtra("ExamIdArr", ExamIdArr);
                                    i.putExtra("ExamNum", ExamNum);
                                    i.putExtra("ResultExamArr", ResultExamArr);
                                    i.putExtra("RoomId", RoomId);
                                    startActivity(i);
                                    finish();
                                }
                            });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myTimer.sendEmptyMessage(0);
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 1000);
                count++;
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onPageSelected(int i) {
                int text_count = i + 1;
                tv_count.setText("Q" + text_count);
                if (i == ExamNum - 1) {
                    btn_ok.setText("시험종료");
                } else {
                    btn_ok.setText("다음문제");
                }
                for (int t = 0; t < ExamNum; t++) {
                    if (t == 0)
                        eachTimer.removeMessages(0);//핸들러 메세지 제거

                    if (t == i) {
                        //현재 화면의 Index의 타이머만 run 상태로
                        if (eachBaseTime[t] != 0) {
                            long now = SystemClock.elapsedRealtime();
                            myTimer.sendEmptyMessage(0);
                            eachBaseTime[t] += (now - eachPauseTime[t]);
                        } else {
                            eachBaseTime[t] = SystemClock.elapsedRealtime();
                        }
                    } else if (t == CurrentViewId) {
                        //현재 화면의 Index가 아닌 타이머는 Pause 상태로
                        eachPauseTime[t] = SystemClock.elapsedRealtime();
                    }

                    if (t == ExamNum - 1) {
                        eachTimer.sendEmptyMessage(0);
                        CurrentViewId = i;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }


        });

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        setupViewPager(viewPager);
    }

    @SuppressLint("HandlerLeak")
    Handler myTimer = new Handler(){
        public void handleMessage(Message msg){
            if(!timeOut){
                time_out.setText(getTimeOut());
                myTimer.sendEmptyMessage(0);
            }
            else {
                eachTimer.removeMessages(0);
                //현재 화면의 Index의 타이머만 run 상태로
                eachPauseTime[CurrentViewId] = SystemClock.elapsedRealtime()+990;
                for (int i = 0; i < ExamNum; i++) {
                    long outTime = eachPauseTime[i] - eachBaseTime[i];
                    @SuppressLint("DefaultLocale")
                    String easy_outTime = String.format("%02d:%02d:%02d", outTime / 1000 / 60, (outTime / 1000) % 60, (outTime % 1000) / 10);
                    Log.d("TAG", "Mineru : " + i + ") " + easy_outTime);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(RandomExam.this);
                builder.setTitle("시간초과");
                builder.setMessage("할당된 시간이 다 되었습니다.\n시험을 종료합니다.");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                myTimer.removeMessages(0);
                                finish();
                            }
                        });
                builder.show();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler eachTimer = new Handler(){
        public void handleMessage(Message msg){
            eachTimer.sendEmptyMessage(0);
        }
    };

    //현재시간을 계속 구해서 출력하는 메소드
    String getTimeOut() {
        long now = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        long outTime = (long) setting_time - (now - myBaseTime);
        @SuppressLint("DefaultLocale")
        String easy_outTime = String.format("%02d:%02d:%02d", outTime / 1000 / 60 / 60, (outTime / 1000 / 60) % 60, (outTime / 1000) % 60);
        if (easy_outTime.toString().equals("00:00:00")) {
            myTimer.removeCallbacksAndMessages(0);
            myTimer.removeMessages(0);
            timeOut = true;
        }
        return easy_outTime;
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentExampleAdapter adapter = new FragmentExampleAdapter(getSupportFragmentManager());
        for(int i = 0; i< ExamNum; i++){
            RandomExamFragment RandomexamFragment = new RandomExamFragment(ExamIdArr[i]);
            adapter.addFragment(RandomexamFragment);
        }
        viewPager.setAdapter(adapter);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private class splashHandler implements Runnable{
        public void run()	{
            btn_ok.setEnabled(true); // 클릭 유효화
            viewPager.setCurrentItem(getItem(+1), true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 1000:
                    myTimer.removeMessages(0);
                    eachTimer.removeMessages(0);
                    int result = data.getIntExtra("result",0);
                    int id = data.getIntExtra("id",0);
                    b_list.set(viewPager.getCurrentItem()-1,result);
                    if(id==3) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RandomExam.this);
                        builder.setTitle("시험종료");
                        builder.setMessage("주어진 문제를 모두 풀었습니다.\n시험을 종료합니다.");
                        builder.setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        for(int i = 0;i<ExamNum;i++)
                                            ResultExamArr[i] = eachPauseTime[i] - eachBaseTime[i];
                                        Intent i = new Intent(RandomExam.this,RandomExamSolve.class);
                                        i.putExtra("randomArr", ExamIdArr);
                                        i.putExtra("ExamNum", ExamNum);
                                        i.putExtra("ResultArr", ResultExamArr);
                                        i.putExtra("RoomId", RoomId);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        });
                        builder.show();
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(RandomExam.this)
                .setTitle("종료")
                .setMessage("이대로 종료하시면 저장이 되지 않습니다.\n그래도 종료하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.delete_exam(RoomId);
                        finish();
                    }

                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                })
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        db.delete_exam(RoomId);
    }
}

