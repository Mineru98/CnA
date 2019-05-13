package com.cna.mineru.cna;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cna.mineru.cna.Adapter.FragmentExampleAdapter;
import com.cna.mineru.cna.Adapter.RandomViewPager;
import com.cna.mineru.cna.DB.ExamSQLClass;
import com.cna.mineru.cna.Fragment.ExamFragmentChild.DoExamFragmentChild.RandomExamSolveFragment;
import com.cna.mineru.cna.Utils.SignDialog;

import java.util.ArrayList;

public class RandomExamSolve extends AppCompatActivity {
    private ExamSQLClass db;

    private ViewPager viewPager;

    private Button btn_no;
    private Button btn_ok;


    private TextView tv_time;
    private TextView tv_title;
    private TextView tv_count;

    private int RoomId;
    private int ExamNum;
    private int[] ExamIdArr;
    private int CurrentViewId;

    private int[] ResultCheckList;
    private long[] ResultExamArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_solve);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        viewPager = (RandomViewPager) findViewById(R.id.view_pager);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_no = (Button) findViewById(R.id.btn_no);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_title = (TextView) findViewById(R.id.tv_title);

        ArrayList<Integer> i_list = new ArrayList<>();
        ArrayList<Integer> b_list = new ArrayList<>();
        db = new ExamSQLClass(this);
        final SignDialog dialog = new SignDialog(RandomExamSolve.this);

        ExamIdArr = getIntent().getIntArrayExtra("ExamIdArr");
        ExamNum = getIntent().getIntExtra("ExamNum", 0);
        RoomId= getIntent().getIntExtra("RoomId",0);
        ResultCheckList = new int[ExamNum];
        ResultExamArr = getIntent().getLongArrayExtra("ResultExamArr");

        CurrentViewId = 0;


        //시간 계산을 한지 얼마 되지 않은 상태에선 시간 측정이
        //제대로 이루어지지 않는 버그로 인해서 10초 미만 동안 문제를 푼 경우엔
        //그냥 10초 고정.
        for(int i=0;i<ExamNum;i++){
            if(ResultExamArr[i]<0)
                ResultExamArr[i]=10000;
        }

        @SuppressLint("DefaultLocale")
        String easy_outTime = String.format("%02d:%02d", (ResultExamArr[0] / 1000 / 60) % 60, (ResultExamArr[0] / 1000) % 60);
        tv_time.setText(easy_outTime);

        for (int i = 0; i < ExamNum; i++) {
            i_list.add(ExamIdArr[i]);
            b_list.add(0);
            ResultCheckList[i] = 0;
        }

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CurrentViewId == ExamNum - 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RandomExamSolve.this);
                    builder.setTitle("시험종료");
                    builder.setMessage("시험을 완료하시겠습니까?\n'예'를 누르면 시험이 종료됩니다.");
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //시험 결과 저장
                                    db.update_result(ResultCheckList, ResultExamArr, RoomId, ExamIdArr);
                                    Intent i = new Intent(RandomExamSolve.this,ExamResultActivity.class);
                                    i.putExtra("RoomId", RoomId);
                                    startActivity(i);
                                    finish();
                                }
                            });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                else{
                    dialog.call_btn_ok();
                }
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 1000);
                ResultCheckList[CurrentViewId] = 1;
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CurrentViewId == ExamNum - 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RandomExamSolve.this);
                    builder.setTitle("시험종료");
                    builder.setMessage("시험을 완료하시겠습니까?\n'예'를 누르면 시험이 종료됩니다.");
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //시험 결과 저장
                                    db.update_result(ResultCheckList, ResultExamArr, RoomId, ExamIdArr);
                                    finish();
                                }
                            });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }else{
                    dialog.call_btn_x();
                }
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 1000);
                ResultCheckList[CurrentViewId] = 0;
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
                @SuppressLint("DefaultLocale")
                String easy_outTime_each = String.format("%02d:%02d", (ResultExamArr[i] / 1000 / 60) % 60, (ResultExamArr[i] / 1000) % 60);
                tv_count.setText("A" + text_count);
                tv_time.setText(easy_outTime_each);
                for (int t = 0; t < ExamNum; t++) {
                    if (t == ExamNum - 1) {
                        CurrentViewId = i;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }

        });
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentExampleAdapter adapter = new FragmentExampleAdapter(getSupportFragmentManager());
        for (int i = 0; i < ExamNum; i++) {
            RandomExamSolveFragment RandomexamSolveFragment = new RandomExamSolveFragment(ExamIdArr[i]);
            adapter.addFragment(RandomexamSolveFragment);
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
    public void onBackPressed() {
        new AlertDialog.Builder(RandomExamSolve.this)
                .setTitle("종료")
                .setMessage("이대로 종료하시면 정답을 매기지 않는 모든 문제는 오답처리가 됩니다.\n그래도 종료하시겠습니까?")
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
