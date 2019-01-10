package com.cna.mineru.cna.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.CalendarView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cna.mineru.cna.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MyCalendarView extends DialogFragment {
    OnMyDialogResult mDialogResult;
    String r_yaer;
    String r_month;
    String r_dayOfMonth;
    String date = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.dialog_calendar, null, false);

        TextView btn_cancel = (TextView)dialog.findViewById(R.id.btn_cancel);
        TextView btn_save = (TextView)dialog.findViewById(R.id.btn_save);

        CalendarView calendar = (CalendarView)dialog.findViewById(R.id.calendarView1);
        r_yaer= new SimpleDateFormat("yyyy").format(new Date(System.currentTimeMillis()));
        r_month = String.valueOf(Integer.parseInt(new SimpleDateFormat("MM").format(new Date(System.currentTimeMillis())))-1);
        r_dayOfMonth = new SimpleDateFormat("dd").format(new Date(System.currentTimeMillis()));
        date = r_yaer + r_month + r_dayOfMonth;
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                r_yaer =  String.valueOf(year);
                if(month<10){
                    r_month = String.valueOf("0"+month);
                }else{
                    r_month = String.valueOf(month);
                }
                if(dayOfMonth<10){
                    r_dayOfMonth = String.valueOf("0"+dayOfMonth);
                }else{
                    r_dayOfMonth = String.valueOf(dayOfMonth);
                }
                date = r_yaer + r_month + r_dayOfMonth;
            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mDialogResult.finish(date);
                dismiss();
            }
        });
        builder.setView(dialog);
        return builder.create();
    }


    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }
    public interface OnMyDialogResult{
        void finish(String result);
    }

}
