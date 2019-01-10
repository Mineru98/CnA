package com.cna.mineru.cna;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cna.mineru.cna.DB.PlanSQLClass;
import com.cna.mineru.cna.Utils.MyCalendarView;

import java.util.Calendar;

public class AddPlan extends AppCompatActivity {

    private TextView tv_page;
    private Button btn_calendar;
    private EditText et_title;
    private EditText et_gold;
    private ImageView btn_back;
    private TextView btn_cancel;
    private  TextView btn_save;
    PlanSQLClass db;

    int year;
    int month;
    int dayOfMonth;
    int form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addplan);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        btn_save = (TextView) findViewById(R.id.btn_save);
        et_title = (EditText) findViewById(R.id.et_title);
        et_gold = (EditText) findViewById(R.id.et_gold);

        btn_calendar = (Button) findViewById(R.id.btn_calendar);
        tv_page = (TextView) findViewById(R.id.tv_page);

        db = new PlanSQLClass(getApplicationContext());

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        form=0;
                        tv_page.setText("p");
                        break;
                    case 1:
                        form=1;
                        tv_page.setText("");
                        break;
                    case 2:
                        form=2;
                        tv_page.setText("p");
                        break;
                    case 3:
                        form=3;
                        tv_page.setText("");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_calendar.setEnabled(false);
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 1000);
                MyCalendarView myCalendarView = new MyCalendarView();
                myCalendarView.show(getSupportFragmentManager(),"grid_view_calendar");
                myCalendarView.setDialogResult(new MyCalendarView.OnMyDialogResult() {
                    @Override
                    public void finish(String result) {
                        Calendar cal = Calendar.getInstance();
                        year = Integer.parseInt(result.substring(0,4));
                        month = Integer.parseInt(result.substring(4,6))+1;
                        dayOfMonth = Integer.parseInt(result.substring(6,8));
                        cal.set(Calendar.YEAR,year);
                        cal.set(Calendar.MONTH,month-1);
                        cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        switch (cal.get(Calendar.DAY_OF_WEEK)){
                            case 1:
                                btn_calendar.setText(""+year+"년 "+(month)+"월 "+dayOfMonth + "일 " + "("+"일"+")");
                                break;
                            case 2:
                                btn_calendar.setText(""+year+"년 "+(month)+"월 "+dayOfMonth + "일 " + "("+"월"+")");
                                break;
                            case 3:
                                btn_calendar.setText(""+year+"년 "+(month)+"월 "+dayOfMonth + "일 " + "("+"화"+")");
                                break;
                            case 4:
                                btn_calendar.setText(""+year+"년 "+(month)+"월 "+dayOfMonth + "일 " + "("+"수"+")");
                                break;
                            case 5:
                                btn_calendar.setText(""+year+"년 "+(month)+"월 "+dayOfMonth + "일 " + "("+"목"+")");
                                break;
                            case 6:
                                btn_calendar.setText(""+year+"년 "+(month)+"월 "+dayOfMonth + "일 " + "("+"금"+")");
                                break;
                            case 7:
                                btn_calendar.setText(""+year+"년 "+(month)+"월 "+dayOfMonth + "일 " + "("+"토"+")");
                                break;
                        }
                    }
                });
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_back.setEnabled(false);
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 1000);
                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_cancel.setEnabled(false);
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 1000);
                showDialog();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DB Insert
                btn_save.setEnabled(false);
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 1000);
                db.add_values(et_title.getText().toString(),form,Integer.parseInt(et_gold.getText().toString()),year+"-"+month+"-"+dayOfMonth);
                finish();
            }
        });
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("취소");
        builder.setMessage("정말 취소하시겠습니까?");

        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }
    private class splashHandler implements Runnable{
        public void run()	{
            btn_calendar.setEnabled(true);
            btn_back.setEnabled(true);
            btn_cancel.setEnabled(true);
            btn_save.setEnabled(true);
        }
    }
}
