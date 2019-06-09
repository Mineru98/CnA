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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cna.mineru.cna.DB.UserSQLClass;
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
    public CustomDialog(int code, int time) {
        if (code == 5) {
            str_tv_title = "알림";
            str_tv_subtitle = "" + time + "분간 시험이 진행됩니다.\n이대로 진행 하시겠습니까?";
        }
    }
    @SuppressLint("ValidFragment")
    public CustomDialog(int code) {
        if (code == 1) {
            str_tv_title = "로그아웃";
            str_tv_subtitle = "로그아웃 하시겠습니까?";
        } else if (code == 2) {
            str_tv_title = "회원탈퇴";
            str_tv_subtitle = "회원탈퇴 하시겠습니까?";
        } else if (code == 3) {
            str_tv_title = "비밀번호 변경요청";
            str_tv_subtitle = "이메일을 입력해주시면 해당 메일로 변경메일이 날라갑니다.";
        } else if (code == 4) {
            str_tv_title = "인터넷 연결 오류";
            str_tv_subtitle = "인터넷 연결 상태를 확인해 주세요.\n인터넷 설정으로 이동하시겠습니까?";
        } else if (code == 5) {
            str_tv_title = "알림";
            str_tv_subtitle = "분간 시험이 진행됩니다.\n이대로 진행 하시겠습니까?";
        } else if (code == 6) {
            str_tv_title = "오답노트 삭제";
            str_tv_subtitle = "정말로 삭제하시겠습니까?";
        } else if (code == 7) {
            str_tv_title = "쿠폰 추가 등록";
            str_tv_subtitle = "이미 등록 된 쿠폰이 있습니다.\n그래도 등록하시겠습니까?";
        } else if (code == 8) {
            str_tv_title = "입력 오류";
            str_tv_subtitle = "비밀번호가 일치하지 않습니다.";
        } else if (code == 9) {
            str_tv_title = "시험 종료";
            str_tv_subtitle = "시험을 완료하시겠습니까?\n'예'를 누르면 시험이 종료됩니다.";
        } else if (code == 10) {
            str_tv_title = "종료";
            str_tv_subtitle = "이대로 종료하시면 정답을 매기지 않는 모든 문제는 오답처리가 됩니다.\n그래도 종료하시겠습니까?";
        } else if (code == 11) {
            str_tv_title = "시험 시간 종료";
            str_tv_subtitle = "할당된 시간이 다 되었습니다.\n시험을 종료합니다.";
        } else if (code == 12) {
            str_tv_title = "시험 종료";
            str_tv_subtitle = "주어진 문제를 모두 풀었습니다.\n시험을 종료합니다.";
        } else if (code == 13) {
            str_tv_title = "입력 오류";
            str_tv_subtitle = "단원을 입력해주세요.";
        } else if (code == 14) {
            str_tv_title = "시험 종료";
            str_tv_subtitle = "예를 누르시면 시험 결과로 바로 넘어갑니다.\n아니오를 누르시면 시험 결과는 저장되고 결과는 나중에 계속 볼 수 있습니다.";
        } else if (code == 15) {
            str_tv_title = "종료";
            str_tv_subtitle = "이대로 종료하시면 시험 결과가 저장되지 않습니다.\n그래도 종료하시겠습니까?";
        } else if (code == 16) {
            str_tv_title = "프로필 설정";
            str_tv_subtitle = "사용자 이름을 변경해 주세요.";
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        if(str_tv_title.equals("비밀번호 변경요청")) {
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

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogResult.finish(1, et_email.getText().toString());
                    CustomDialog.this.dismiss();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogResult.finish(0, "");
                    CustomDialog.this.dismiss();
                }
            });
            builder.setView(dig);
        }else if (str_tv_title.equals("프로필 설정")){
            View dig = inflater.inflate(R.layout.custom_dialog2, null);

            TextView tv_title = (TextView) dig.findViewById(R.id.tv_title);
            TextView tv_subtitle = (TextView) dig.findViewById(R.id.tv_subtitle);
            et_email = (AppCompatEditText) dig.findViewById(R.id.et_email);
            btn_ok = (TextView) dig.findViewById(R.id.btn_ok);
            btn_cancel = (TextView) dig.findViewById(R.id.btn_cancel);

            tv_title.setText(str_tv_title);
            tv_subtitle.setText(str_tv_subtitle);

            UserSQLClass db = new UserSQLClass(getContext());
            et_email.setHint(db.get_Name());
            et_email.setInputType(InputType.TYPE_CLASS_TEXT);
            layout_email = (TextInputLayout) dig.findViewById(R.id.layout_email);
            layout_email.setHint("이름");
            btn_ok.setText("완료");
            btn_cancel.setText("취소");

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogResult.finish(1, et_email.getText().toString());
                    CustomDialog.this.dismiss();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogResult.finish(0, "");
                    CustomDialog.this.dismiss();
                }
            });
            builder.setView(dig);
        }else if(str_tv_title.equals("인터넷 연결 오류")){
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
        } else if (str_tv_subtitle.equals("비밀번호가 일치하지 않습니다.") || str_tv_subtitle.equals("단원을 입력해주세요.") || str_tv_title.equals("시험 시간 종료")){
            View dig = inflater.inflate(R.layout.custom_dialog, null);
            TextView tv_title = (TextView) dig.findViewById(R.id.tv_title);
            TextView tv_subtitle = (TextView) dig.findViewById(R.id.tv_subtitle);
            tv_title.setText(str_tv_title);
            tv_subtitle.setText(str_tv_subtitle);

            btn_ok = (TextView) dig.findViewById(R.id.btn_ok);
            btn_cancel = (TextView) dig.findViewById(R.id.btn_cancel);
            btn_ok.setVisibility(View.INVISIBLE);
            btn_cancel.setText("확인");

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogResult.finish(0);
                    CustomDialog.this.dismiss();
                }
            });
            builder.setView(dig);
        } else {
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
