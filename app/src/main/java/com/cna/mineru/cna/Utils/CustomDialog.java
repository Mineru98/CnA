package com.cna.mineru.cna.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cna.mineru.cna.R;

import java.util.Objects;

@SuppressLint("ValidFragment")
public class CustomDialog extends DialogFragment {
    private OnMyDialogResult mDialogResult;
    private LoadingDialog loadingDialog;

    private TextView btn_ok;
    private TextView btn_cancel;

    private TextInputLayout layout_email;
    private AppCompatEditText et_email;

    private String str_tv_title="";
    private String str_tv_subtitle="";

    @SuppressLint("ValidFragment")
    public CustomDialog(int code){
        if(code ==1){
            str_tv_title = "로그아웃";
            str_tv_subtitle = "로그아웃 하시겠습니까?";
        }else if(code == 2){
            str_tv_title = "회원탈퇴";
            str_tv_subtitle = "회원탈퇴 하시겠습니까?";
        }else if(code == 3) {
            str_tv_title = "비밀번호 변경요청";
            str_tv_subtitle = "이메일을 입력해주시면 해당 메일로 변경메일이 날라갑니다.";
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        if(str_tv_title.equals("비밀번호 변경요청")){
            View dig = inflater.inflate(R.layout.custom_dialog2, null);

            TextView tv_title = (TextView) dig.findViewById(R.id.tv_title);
            TextView tv_subtitle = (TextView) dig.findViewById(R.id.tv_subtitle);
            layout_email = (TextInputLayout) dig.findViewById(R.id.layout_email);
            et_email = (AppCompatEditText) dig.findViewById(R.id.et_email);
            btn_ok = (TextView) dig.findViewById(R.id.btn_ok);
            btn_cancel = (TextView) dig.findViewById(R.id.btn_cancel);

            tv_title.setText(str_tv_title);
            tv_subtitle.setText(str_tv_subtitle);

            btn_ok.setText("전송");
            btn_cancel.setText("취소");

            btn_ok.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    mDialogResult.finish(1, et_email.getText().toString());
                    CustomDialog.this.dismiss();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogResult.finish(0,"");
                    CustomDialog.this.dismiss();
                }
            });
            builder.setView(dig);
        }else{
            View dig = inflater.inflate(R.layout.custom_dialog, null);
            TextView tv_title = (TextView) dig.findViewById(R.id.tv_title);
            TextView tv_subtitle = (TextView) dig.findViewById(R.id.tv_subtitle);
            tv_title.setText(str_tv_title);
            tv_subtitle.setText(str_tv_subtitle);

            btn_ok = (TextView) dig.findViewById(R.id.btn_ok);
            btn_cancel = (TextView) dig.findViewById(R.id.btn_cancel);

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogResult.finish(1);
                    CustomDialog.this.dismiss();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogResult.finish(0);
                    CustomDialog.this.dismiss();
                }
            });
            builder.setView(dig);
        }

        return builder.create();
    }

    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult {
        void finish(int result);
        void finish(int result,String email);
    }

}
