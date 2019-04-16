package com.cna.mineru.cna.Fragment.ExamFragmentChild;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cna.mineru.cna.DB.ExamSQLClass;
import com.cna.mineru.cna.DB.HomeSQLClass;
import com.cna.mineru.cna.MainActivity;
import com.cna.mineru.cna.R;
import com.cna.mineru.cna.RandomExam;

public class DoExamFragmentTmp extends Fragment {

    private TextView tv_time;
    private TextView tv_count;
    private TextView title2;
    private TextView title3;
    private TextView btn_next;
    private Button btn_add;
    private Button btn_add_double;
    private Button btn_sub;
    private Button btn_sub_double;
    private Button btn_add_time;
    private Button btn_add_double_time;
    private Button btn_sub_time;
    private Button btn_sub_double_time;

    private boolean isNext = false;
    private int time = 0;
    private int problem_num = 0;

    private View view;
    private ExamSQLClass db;
    private HomeSQLClass h_db;

    public DoExamFragmentTmp() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new ExamSQLClass(getActivity());
        h_db = new HomeSQLClass(getActivity());
        view = inflater.inflate(R.layout.fragment_do_exam_tmp, container, false);

        View boundary1 = (View) view.findViewById(R.id.boundary1);
        View boundary2 = (View) view.findViewById(R.id.boundary2);
        TextView title1 = (TextView) view.findViewById(R.id.title1);
        btn_next = (TextView) view.findViewById(R.id.btn_test);
        title2 = (TextView) view.findViewById(R.id.title2);
        title3 = (TextView) view.findViewById(R.id.title3);
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        tv_count = (TextView) view.findViewById(R.id.tv_count);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add_double = (Button) view.findViewById(R.id.btn_add_double);
        btn_sub = (Button) view.findViewById(R.id.btn_sub);
        btn_sub_double = (Button) view.findViewById(R.id.btn_sub_double);
        btn_add_time = (Button) view.findViewById(R.id.btn_add_time);
        btn_add_double_time = (Button) view.findViewById(R.id.btn_add_double_time);
        btn_sub_time = (Button) view.findViewById(R.id.btn_sub_time);
        btn_sub_double_time = (Button) view.findViewById(R.id.btn_sub_double_time);

        boundary1.setVisibility(View.INVISIBLE);
        title2.setVisibility(View.INVISIBLE);
        title3.setVisibility(View.INVISIBLE);
        tv_time.setVisibility(View.INVISIBLE);
        tv_count.setVisibility(View.INVISIBLE);
        btn_next.setVisibility(View.INVISIBLE);
        btn_add.setVisibility(View.INVISIBLE);
        btn_add_double.setVisibility(View.INVISIBLE);
        btn_sub.setVisibility(View.INVISIBLE);
        btn_sub_double.setVisibility(View.INVISIBLE);
        btn_add_time.setVisibility(View.INVISIBLE);
        btn_add_double_time.setVisibility(View.INVISIBLE);
        btn_sub_time.setVisibility(View.INVISIBLE);
        btn_sub_double_time.setVisibility(View.INVISIBLE);

        problem_num = 0;
        time = 0;

        final GridView gridView = (GridView) view.findViewById(R.id.btn_box);
        gridView.setAdapter(new ImageAdapter(getContext()));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (!isNext) {
                    Animation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(500);

                    boundary1.setVisibility(View.VISIBLE);
                    title2.setVisibility(View.VISIBLE);
                    title3.setVisibility(View.VISIBLE);
                    tv_time.setVisibility(View.VISIBLE);
                    tv_count.setVisibility(View.VISIBLE);
                    btn_next.setVisibility(View.VISIBLE);
                    btn_add.setVisibility(View.VISIBLE);
                    btn_add_double.setVisibility(View.VISIBLE);
                    btn_sub.setVisibility(View.VISIBLE);
                    btn_sub_double.setVisibility(View.VISIBLE);
                    btn_add_time.setVisibility(View.VISIBLE);
                    btn_add_double_time.setVisibility(View.VISIBLE);
                    btn_sub_time.setVisibility(View.VISIBLE);
                    btn_sub_double_time.setVisibility(View.VISIBLE);

                    boundary1.setAnimation(animation);
                    title2.setAnimation(animation);
                    title3.setAnimation(animation);
                    tv_time.setAnimation(animation);
                    tv_count.setAnimation(animation);
                    btn_next.setAnimation(animation);
                    btn_add.setAnimation(animation);
                    btn_add_double.setAnimation(animation);
                    btn_sub.setAnimation(animation);
                    btn_sub_double.setAnimation(animation);
                    btn_add_time.setAnimation(animation);
                    btn_add_double_time.setAnimation(animation);
                    btn_sub_time.setAnimation(animation);
                    btn_sub_double_time.setAnimation(animation);

                    //isNext = true;
                    problem_num += 4;
                    time += 4 * 3;
                    tv_time.setText("" + time + " 분");
                    tv_count.setText("" + problem_num + " 문제");
                } else {
                    problem_num += 4;
                    time += 4 * 3;
                    tv_time.setText("" + time + " 분");
                    tv_count.setText("" + problem_num + " 문제");
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int time = Integer.parseInt(tv_time.getText().toString().substring(0,tv_time.getText().toString().length()-2));
                int count = Integer.parseInt(tv_count.getText().toString().substring(0,tv_count.getText().toString().length()-3));
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("알림");
                dialog.setMessage(time + "분간 시험이 진행됩니다.\n이대로 진행 하시겠습니까?");
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean test = db.make_Exam(count);
                        int[] note_list = h_db.getItemIdEach();
                        if (test) {
                            dialog.dismiss();

                            int[] tmp = new int[count];
                            int[] result = new int[count];

                            for (int k = 0; k < tmp.length; k++) {
                                tmp[k] = (int) (Math.random() * (note_list.length));
                                for (int j = 0; j < k; j++) {
                                    if (tmp[k] == tmp[j]) {
                                        k--;
                                        break;
                                    }
                                }
                            }

                            for (int k = 0; k < count; k++)
                                result[k] = note_list[tmp[k]];

                            Intent i = new Intent((MainActivity)getActivity(), RandomExam.class);
                            i.putExtra("time", time * 60 * 1000);
                            i.putExtra("randomArr", result);
                            i.putExtra("ExamNum", count);
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
        });

        btn_sub_double.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                problem_num -= 2;
                tv_count.setText("" + problem_num + " 문제");
            }
        });
        btn_sub.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                problem_num -= 1;
                tv_count.setText("" + problem_num + " 문제");
            }
        });

        btn_sub_double_time.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                time = time - 4;
                tv_time.setText("" + time + " 분");
            }
        });
        btn_sub_time.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                time = time - 1;
                tv_time.setText("" + time + " 분");
            }
        });

        btn_add_double.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                problem_num += 2;
                tv_count.setText("" + problem_num + " 문제");
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                problem_num += 1;
                tv_count.setText("" + problem_num + " 문제");
            }
        });

        btn_add_double_time.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                time = time + 4;
                tv_time.setText("" + time + " 분");
            }
        });

        btn_add_time.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                time = time + 1;
                tv_time.setText("" + time + " 분");
            }
        });

        return view;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        private Integer[] mThumbIds = {
                R.drawable.btn_all, R.drawable.btn_1,
                R.drawable.btn_2, R.drawable.btn_3,
                R.drawable.btn_4, R.drawable.btn_5,
                R.drawable.btn_6, R.drawable.btn_7,
                R.drawable.btn_1mid, R.drawable.btn_1fin,
                R.drawable.btn_2mid, R.drawable.btn_2fin
        };

        ImageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mThumbIds.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(10, 10, 10, 10);
                imageView.setBackgroundColor(0x0000FF00);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(mThumbIds[position]);
            return imageView;

        }
    }
}
