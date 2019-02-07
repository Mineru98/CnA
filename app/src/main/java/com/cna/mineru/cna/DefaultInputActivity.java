package com.cna.mineru.cna;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cna.mineru.cna.Utils.MyYearMonthDayPicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DefaultInputActivity extends AppCompatActivity {

    private EditText et_name;
    private EditText et_birthday;
    private Button btn_birthday;
    private TextView btn_next;

    public enum Class1 {
        중1, 중학생1학년, 중딩1, 중학교1학년, 중학1
    }
    public enum Class2 {
        중2, 중학생2학년, 중딩2, 중학교2학년, 중학2
    }
    public enum Class3 {
        중3, 중학생3학년, 중딩3, 중학교3학년, 중학3
    }
    public enum Class4 {
        고1, 고등학생1학년, 고딩1, 고등학교1학년, 고등학교1
    }
    public enum Class5 {
        고2, 고등학생2학년, 고딩2, 고등학교2학년, 고등학교2
    }
    public enum Class6 {
        고3, 고등학생3학년, 고딩3, 고등학교3학년, 고등학교3
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            Log.d("YearMonthPickerTest", "year = " + year + ", month = " + monthOfYear + ", day = " + dayOfMonth);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defaultinput_activity);

        et_name = (EditText) findViewById(R.id.et_name);
        et_birthday = (EditText) findViewById(R.id.et_birthday);
        btn_birthday = (Button) findViewById(R.id.btn_birthday);
        btn_next = (TextView) findViewById(R.id.btn_next);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat t_year = new SimpleDateFormat( "yyyy");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat t_month = new SimpleDateFormat( "MM");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat t_day = new SimpleDateFormat( "dd");

        Date time = new Date();

        final int c_year = Integer.parseInt(t_year.format(time));


        btn_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyYearMonthDayPicker datePicker = new MyYearMonthDayPicker();
                datePicker.setListener(d);
                datePicker.show(getSupportFragmentManager(),"grid_view_calendar");
                datePicker.setDialogResult(new MyYearMonthDayPicker.OnMyDialogResult() {
                    @Override
                    public void finish(int year, int month, int day) {
                        //현재 날짜 시간과 finish로 받은 날짜 비교
                        if(c_year-year>19||c_year-year<13) {//2019-2000
                            Toast.makeText(DefaultInputActivity.this, "중, 고등학생 아님", Toast.LENGTH_SHORT).show();
                            et_birthday.setText("중, 고등학생 아님");
                        }else{
                            if(c_year-year==13){
                                Toast.makeText(DefaultInputActivity.this, "중1", Toast.LENGTH_SHORT).show();
                                et_birthday.setText("중1");
                            }else if(c_year-year==14){
                                Toast.makeText(DefaultInputActivity.this, "중2", Toast.LENGTH_SHORT).show();
                                et_birthday.setText("중2");
                            }else if(c_year-year==15){
                                Toast.makeText(DefaultInputActivity.this, "중3", Toast.LENGTH_SHORT).show();
                                et_birthday.setText("중3");
                            }else if(c_year-year==16){
                                Toast.makeText(DefaultInputActivity.this, "고1", Toast.LENGTH_SHORT).show();
                                et_birthday.setText("고1");
                            }else if(c_year-year==17){
                                Toast.makeText(DefaultInputActivity.this, "고2", Toast.LENGTH_SHORT).show();
                                et_birthday.setText("고2");
                            }else if(c_year-year==18) {
                                Toast.makeText(DefaultInputActivity.this, "고3", Toast.LENGTH_SHORT).show();
                                et_birthday.setText("고3");
                            }
                        }
                    }
                });
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Class1 type : Class1.values()) {
                    if(et_birthday.getText().toString().equals(type.toString())){
                        Toast.makeText(DefaultInputActivity.this, ""+type.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                for(Class2 type : Class2.values()) {
                    if(et_birthday.getText().toString().equals(type.toString())){
                        Toast.makeText(DefaultInputActivity.this, ""+type.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                for(Class3 type : Class3.values()) {
                    if(et_birthday.getText().toString().equals(type.toString())){
                        Toast.makeText(DefaultInputActivity.this, ""+type.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                for(Class4 type : Class4.values()) {
                    if(et_birthday.getText().toString().equals(type.toString())){
                        Toast.makeText(DefaultInputActivity.this, ""+type.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                for(Class5 type : Class5.values()) {
                    if(et_birthday.getText().toString().equals(type.toString())){
                        Toast.makeText(DefaultInputActivity.this, ""+type.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                for(Class6 type : Class6.values()) {
                    if(et_birthday.getText().toString().equals(type.toString())){
                        Toast.makeText(DefaultInputActivity.this, ""+type.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
