package com.cna.mineru.cna.Fragment.ExamFragmentChild;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cna.mineru.cna.DB.ExamSQLClass;
import com.cna.mineru.cna.DB.HomeSQLClass;
import com.cna.mineru.cna.MainActivity;
import com.cna.mineru.cna.R;
import com.cna.mineru.cna.RandomExam;

import java.util.ArrayList;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

public class DoExamFragment extends Fragment {

    private View view;
    private EditText et_hour;
    private EditText et_min;
    private EditText et_sec;
    private ExamSQLClass db;
    private HomeSQLClass h_db;
    private long settingtime;

    public DoExamFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new ExamSQLClass(getActivity());
        h_db = new HomeSQLClass(getActivity());

        view = inflater.inflate(R.layout.fragment_do_exam, container, false);
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
        TextView tv_1 = (TextView) view.findViewById(R.id.tv_1);
        TextView tv_2 = (TextView) view.findViewById(R.id.tv_2);
        et_hour = (EditText) view.findViewById(R.id.et_hour);
        et_min = (EditText) view.findViewById(R.id.et_min);
        et_sec = (EditText) view.findViewById(R.id.et_sec);

        et_hour.setText("00");
        et_min.setText("00");
        et_sec.setText("00");

        Button btn_start = (Button) view.findViewById(R.id.btn_start);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                if (et_hour.getText().toString().equals("00") && et_min.getText().toString().equals("00") && et_sec.getText().toString().equals("00")) {
                    Toast.makeText(getActivity(), "시간을 입력해 주세요.", Toast.LENGTH_LONG).show();
                } else {
                    if (et_hour.getText().toString().equals("00")) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle("알림");
                        if (et_min.getText().toString().equals("00")) {
                            settingtime = Long.parseLong(et_sec.getText().toString()) * 1000;
                            dialog.setMessage((settingtime / 1000) % 60 + "초간 시험이 진행됩니다.\n이대로 진행 하시겠습니까?");
                        } else {
                            settingtime = Long.parseLong(et_sec.getText().toString()) * 1000 + Long.parseLong(et_min.getText().toString()) * 60 * 1000;
                            dialog.setMessage((settingtime / 1000 / 60) % 60 + "분 " + (settingtime / 1000) % 60 + "초간 시험이 진행됩니다.\n이대로 진행 하시겠습니까?");
                        }
                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean test = db.make_Exam(4);
                                Log.d("TAG","Mineru : 1");
                                int[] note_list = h_db.getItemIdEach();
                                if (test) {
                                    dialog.dismiss();

                                    int[] tmp = new int[4];
                                    int[] result = new int[4];

                                    for (int k = 0; k < tmp.length; k++) {
                                        tmp[k] = (int) (Math.random() * (note_list.length));

                                        for (int j = 0; j < k; j++) {
                                            if (tmp[k] == tmp[j]) {
                                                k--;
                                                break;
                                            }
                                        }
                                    }

                                    for (int k = 0; k < 4; k++)
                                        result[k] = note_list[tmp[k]];

                                    Intent i = new Intent((MainActivity)getActivity(), RandomExam.class);
                                    i.putExtra("time", settingtime);
                                    i.putExtra("randomArr", result);
                                    startActivity(i);
                                    //startActivityForResult(i, 2000);
                                } else
                                    dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else if (et_min.getText().toString().equals("00")) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle("알림");
                        if (et_sec.getText().toString().equals("00")) {
                            settingtime = Long.parseLong(et_hour.getText().toString()) * 60 * 60 * 1000;
                            dialog.setMessage(settingtime / 1000 / 60 / 60 + "시간 동안 시험이 진행됩니다.\n이대로 진행 하시겠습니까?");
                        } else {
                            settingtime = Long.parseLong(et_sec.getText().toString()) * 1000 + Long.parseLong(et_hour.getText().toString()) * 60 * 60 * 1000;
                            dialog.setMessage(settingtime / 1000 / 60 / 60 + "시간 " + (settingtime / 1000) % 60 + "초간 시험이 진행됩니다.\n이대로 진행 하시겠습니까?");
                        }
                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean test = db.make_Exam(4);
                                int[] note_list = h_db.getItemIdEach();

                                if (test) {
                                    dialog.dismiss();

                                    int[] tmp = new int[4];
                                    int[] result = new int[4];

                                    for (int k = 0; k < tmp.length; k++) {
                                        tmp[k] = (int) (Math.random() * (note_list.length));

                                        for (int j = 0; j < k; j++) {
                                            if (tmp[k] == tmp[j]) {
                                                k--;
                                                break;
                                            }
                                        }
                                    }

                                    for (int k = 0; k < 4; k++)
                                        result[k] = note_list[tmp[k]];

                                    Intent i = new Intent((MainActivity)getActivity(), RandomExam.class);
                                    i.putExtra("time", settingtime);
                                    i.putExtra("randomArr", result);
                                    startActivity(i);
                                    //startActivityForResult(i, 2000);
                                } else
                                    dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else if (et_sec.getText().toString().equals("00")) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle("알림");
                        if (et_hour.getText().toString().equals("00")) {
                            settingtime = Long.parseLong(et_min.getText().toString()) * 60 * 1000;
                            dialog.setMessage(settingtime / 60 / 1000 / 60 + "시간 시험이 진행됩니다.\n이대로 진행 하시겠습니까?");
                        } else {
                            settingtime = Long.parseLong(et_min.getText().toString()) * 60 * 1000 + Long.parseLong(et_hour.getText().toString()) * 60 * 60 * 1000;
                            dialog.setMessage(settingtime / 1000 / 60 / 60 + "시간 " + (settingtime / 1000 / 60) % 60 + "분간 시험이 진행됩니다.\n이대로 진행 하시겠습니까?");
                        }
                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean test = db.make_Exam(4);
                                int[] note_list = h_db.getItemIdEach();

                                if (test) {
                                    dialog.dismiss();

                                    int[] tmp = new int[4];
                                    int[] result = new int[4];

                                    for (int k = 0; k < tmp.length; k++) {
                                        tmp[k] = (int) (Math.random() * (note_list.length));

                                        for (int j = 0; j < k; j++) {
                                            if (tmp[k] == tmp[j]) {
                                                k--;
                                                break;
                                            }
                                        }
                                    }

                                    for (int k = 0; k < 4; k++)
                                        result[k] = note_list[tmp[k]];

                                    Intent i = new Intent((MainActivity)getActivity(), RandomExam.class);
                                    i.putExtra("time", settingtime);
                                    i.putExtra("randomArr", result);
                                    startActivity(i);
                                    //startActivityForResult(i, 2000);
                                } else
                                    dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle("알림");
                        settingtime = Long.parseLong(et_hour.getText().toString()) * 60 * 60 * 1000 + Long.parseLong(et_min.getText().toString()) * 60 * 1000 + Long.parseLong(et_sec.getText().toString()) * 1000;
                        dialog.setMessage(settingtime / 1000 / 60 / 60 + "시간 " + (settingtime / 1000 / 60) % 60 + "분 " + (settingtime / 1000) % 60 + "초간 시험이 진행됩니다.\n이대로 진행 하시겠습니까?");
                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean test = db.make_Exam(4);
                                int[] note_list = h_db.getItemIdEach();

                                if (test) {
                                    dialog.dismiss();

                                    int[] tmp = new int[4];
                                    int[] result = new int[4];

                                    for (int k = 0; k < tmp.length; k++) {
                                        tmp[k] = (int) (Math.random() * (note_list.length));

                                        for (int j = 0; j < k; j++) {
                                            if (tmp[k] == tmp[j]) {
                                                k--;
                                                break;
                                            }
                                        }
                                    }

                                    for (int k = 0; k < 4; k++)
                                        result[k] = note_list[tmp[k]];

                                    Intent i = new Intent((MainActivity)getActivity(), RandomExam.class);
                                    i.putExtra("time", settingtime);
                                    i.putExtra("isOkay", 0);
                                    startActivity(i);
                                    //startActivityForResult(i, 2000);
                                } else
                                    dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }
            }
        });
        return view;
    }
}