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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cna.mineru.cna.Adapter.RandomViewPager;
import com.cna.mineru.cna.Adapter.FragmentExampleAdapter;
import com.cna.mineru.cna.DB.ExamSQLClass;
import com.cna.mineru.cna.Fragment.ExamFragmentChild.DoExamFragmentChild.RandomExamFragment;

import java.util.ArrayList;

public class RandomExam extends AppCompatActivity {

    private ViewPager viewPager;
    private TextView time_out;
    private Button btn_ok;

    private long setting_time;

    private int[] testArr;
    private long myBaseTime;
    private int count = 0;
    private boolean timeOut = false;
    private ArrayList<Integer> i_list;
    private ArrayList<Integer> b_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);
        i_list = new ArrayList<Integer>();
        b_list = new ArrayList<Integer>();
        ExamSQLClass db = new ExamSQLClass(this);

        viewPager = (RandomViewPager) findViewById(R.id.view_pager);
        time_out = (TextView) findViewById(R.id.time_out);
        btn_ok = (Button) findViewById(R.id.btn_ok);

        long setting_time_first = getIntent().getLongExtra("time", 0);
        setting_time = getIntent().getLongExtra("time",0);
        testArr = getIntent().getIntArrayExtra("randomArr");

        for(int i=0;i<4;i++){
            i_list.add(testArr[i]);
            b_list.add(0);
        }

        myBaseTime = SystemClock.elapsedRealtime();//처음 시간 기록
        myTimer.sendEmptyMessage(0);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 1000);
//                btn_ok.setEnabled(false);
                count++;
                Intent i = new Intent(RandomExam.this,RandomExamSolve.class);
                i.putExtra("id",viewPager.getCurrentItem());
                i.putExtra("noteId",testArr[viewPager.getCurrentItem()]);
                startActivityForResult(i,1000);
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

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
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(RandomExam.this);
                builder.setTitle("시간초과");
                builder.setMessage("할당된 시간이 다 되었습니다.\n시험을 종료합니다.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.show();
            }
        }
    };

    //현재시간을 계속 구해서 출력하는 메소드
    String getTimeOut(){
        long now = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        long outTime = setting_time - (now - myBaseTime);
        @SuppressLint("DefaultLocale")
        String easy_outTime = String.format("%02d:%02d:%02d",outTime/1000 / 60 / 60, (outTime/1000 / 60)%60, (outTime/1000)%60);
        if(easy_outTime.toString().equals("00:00:00")){
            myTimer.removeCallbacksAndMessages(0);
            myTimer.removeMessages(0);
            timeOut = true;
        }
        return easy_outTime;

    }


    private void setupViewPager(ViewPager viewPager) {
        FragmentExampleAdapter adapter = new FragmentExampleAdapter(getSupportFragmentManager());
        int exam_count = 4;
        for(int i = 0; i< exam_count; i++){
            RandomExamFragment RandomexamFragment = new RandomExamFragment(testArr[i]);
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
                    int result = data.getIntExtra("result",0);
                    int id = data.getIntExtra("id",0);
                    b_list.set(viewPager.getCurrentItem()-1,result);
                    Toast.makeText(RandomExam.this, ""+result, Toast.LENGTH_SHORT).show();
                    if(id==3) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RandomExam.this);
                        builder.setTitle("시험종료");
                        builder.setMessage("주어진 문제를 모두 풀었습니다.\n시험을 종료합니다.");
                        builder.setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
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
}

