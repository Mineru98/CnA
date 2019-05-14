package com.cna.mineru.cna.Utils;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cna.mineru.cna.R;

public class PhotoDialog2 extends DialogFragment {
    private OnMyDialogResult mDialogResult;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog = inflater.inflate(R.layout.dialog_camera2, null, false);
        TextView btn_camera = (TextView)dialog.findViewById(R.id.btn_camera);
        TextView btn_gallery = (TextView)dialog.findViewById(R.id.btn_gallery);
        TextView btn_editor = (TextView)dialog.findViewById(R.id.btn_editor);

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogResult.finish(1);
                dismiss();
            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogResult.finish(2);
                dismiss();
            }
        });

        btn_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogResult.finish(3);
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
        void finish(int result);
    }

}
