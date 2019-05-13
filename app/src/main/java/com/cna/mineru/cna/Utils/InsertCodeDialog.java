package com.cna.mineru.cna.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cna.mineru.cna.R;

import org.w3c.dom.Text;

import java.util.Objects;

public class InsertCodeDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View dig = inflater.inflate(R.layout.insertcode_dialog, null);
        TextView tv_title = (TextView) dig.findViewById(R.id.tv_title);
        EditText et_code = (EditText) dig.findViewById(R.id.et_code);
        TextView btn_ok = (TextView) dig.findViewById(R.id.btn_ok);
        TextView btn_cancel = (TextView) dig.findViewById(R.id.btn_cancel);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertCodeDialog.this.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertCodeDialog.this.dismiss();
            }
        });

        builder.setView(dig);
        return builder.create();
    }
}
