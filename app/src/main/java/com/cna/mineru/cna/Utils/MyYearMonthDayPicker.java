package com.cna.mineru.cna.Utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.cna.mineru.cna.R;

import java.util.Calendar;

public class MyYearMonthDayPicker extends DialogFragment {
    OnMyDialogResult mDialogResult;

    private static final int MAX_YEAR = 2099;
    private static final int MIN_YEAR = 1980;

    private DatePickerDialog.OnDateSetListener listener;
    public Calendar cal = Calendar.getInstance();

    public void setListener(DatePickerDialog.OnDateSetListener listener){
        this.listener = listener;
    }

    Button btn_cancel;
    Button btn_ok;

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState){
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.year_month_day_picker,null);

        btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
        btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);

        final NumberPicker dayPicker = (NumberPicker)dialog.findViewById(R.id.picker_day);
        final NumberPicker monthPicker = (NumberPicker)dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker)dialog.findViewById(R.id.picker_year);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyYearMonthDayPicker.this.getDialog().cancel();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), dayPicker.getValue());
                mDialogResult.finish(yearPicker.getValue(),monthPicker.getValue(),dayPicker.getValue());
                MyYearMonthDayPicker.this.getDialog().cancel();
            }
        });

        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);
        dayPicker.setValue(cal.get(Calendar.DAY_OF_MONTH)+1);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(cal.get(Calendar.MONTH)+1);

        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(cal.get(Calendar.YEAR));

        builder.setView(dialog);

        return builder.create();
    }

    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult {
        void finish(int year,int month, int day);
    }
}