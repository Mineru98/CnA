package com.cna.mineru.cna.Fragment.ExamFragmentChild;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cna.mineru.cna.Adapter.ExamChipAdapter;
import com.cna.mineru.cna.Adapter.ExpandableListAdapter;
import com.cna.mineru.cna.DB.ExamSQLClass;
import com.cna.mineru.cna.DB.GraphDataSQLClass;
import com.cna.mineru.cna.DB.HomeSQLClass;
import com.cna.mineru.cna.DB.UserSQLClass;
import com.cna.mineru.cna.DTO.ChipData;
import com.cna.mineru.cna.DTO.ClassData;
import com.cna.mineru.cna.DTO.ExamData;
import com.cna.mineru.cna.MainActivity;
import com.cna.mineru.cna.R;
import com.cna.mineru.cna.RandomExam;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class DoExamFragment2 extends Fragment {


    private ExamChipAdapter mAdapater;
    private View view;
    private TextView btn_start;

    private BubbleSeekBar seekBar_1;
    private BubbleSeekBar seekBar_2;
    private BubbleSeekBar seekBar_3;

    private ListView listView;
    private NestedScrollView sv;
    private ExpandableListView lvExp;

    private HomeSQLClass h_db;
    private GraphDataSQLClass c_db;
    private ExamSQLClass db;
    private UserSQLClass u_db;

    private int max_value;
    private boolean isNext = false;
    private int time = 0;
    private int problem_num = 0;
    private int ClassId = 0;
    private ArrayList<ChipData> list = new ArrayList<>();
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    int t_count = 1;

    public DoExamFragment2() {

    }

    @SuppressLint({"ClickableViewAccessibility", "RestrictedApi"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new ExamSQLClass(getActivity());
        h_db = new HomeSQLClass(getActivity());
        c_db = new GraphDataSQLClass(getActivity());
        u_db = new UserSQLClass(getActivity());
        ClassId = u_db.getClassId();

        max_value = h_db.getCount();
        problem_num = 0;
        time = 0;

        if (max_value < 4) {
            max_value = 4;
        }

        view = inflater.inflate(R.layout.fragment_do_exam2, container, false);

        sv = (NestedScrollView) view.findViewById(R.id.sv);
        lvExp = (ExpandableListView) view.findViewById(R.id.lvExp);
        listView = (ListView) view.findViewById(R.id.listView);
        btn_start = (TextView) view.findViewById(R.id.btn_start);

        mAdapater = new ExamChipAdapter(getActivity(), R.layout.item, list);
        prepareListData(ClassId);

        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        listView.setAdapter(mAdapater);
        lvExp.setAdapter(listAdapter);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sv.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        lvExp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sv.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        lvExp.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                list.add(new ChipData(t_count++, listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition)));
                mAdapater = new ExamChipAdapter(v.getContext(), R.layout.item, list);
                TextView tv = (TextView) parent.findViewById(R.id.lblListItem);
                listView.setAdapter(mAdapater);
                return false;
            }
        });

        lvExp.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        lvExp.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        View boundary1 = (View) view.findViewById(R.id.boundary1);
        TextView tv_lv = (TextView) view.findViewById(R.id.tv_lv);
        TextView tv_lvE = (TextView) view.findViewById(R.id.tv_lvE);
        TextView tv_lvE2 = (TextView) view.findViewById(R.id.tv_lvE2);
        TextView tv_mid_low_1 = (TextView) view.findViewById(R.id.tv_mid_low_1);
        TextView tv_mid_low_2 = (TextView) view.findViewById(R.id.tv_mid_low_2);
        TextView tv_mid_low_3 = (TextView) view.findViewById(R.id.tv_mid_low_3);


        seekBar_1 = (BubbleSeekBar) view.findViewById(R.id.seekBar_1);
        seekBar_2 = (BubbleSeekBar) view.findViewById(R.id.seekBar_2);
        seekBar_3 = (BubbleSeekBar) view.findViewById(R.id.seekBar_3);

        seekBar_1.setProgress(25f);
        seekBar_1.getConfigBuilder()
                .min(1)
                .max(4)
                .build();

        seekBar_1.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                seekBar_3.setProgress(seekBar_2.getProgress()*(int)seekBar_1.getProgress());
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
            }
        });

        seekBar_2.setProgress(25f);
        seekBar_2.getConfigBuilder()
                .min(1)
                .max(10)
                .build();
        seekBar_2.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                seekBar_3.setProgress(seekBar_2.getProgress()*(int)seekBar_1.getProgress());
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });

        seekBar_3.setProgress(25f);
        seekBar_3.getConfigBuilder()
                .min(0)
                .max(60)
                .build();
        seekBar_3.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });

        sv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                seekBar_1.correctOffsetWhenContainerOnScrolling();
                seekBar_2.correctOffsetWhenContainerOnScrolling();
                seekBar_3.correctOffsetWhenContainerOnScrolling();
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int time = Integer.parseInt(seekBar_3.getTag().toString());
//                int ExamNum = Integer.parseInt(seekBar_1.getTag().toString());
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                int time = seekBar_3.getProgress();
                int ExamNum = seekBar_1.getProgress();
                dialog.setTitle("알림");
                dialog.setMessage(time + "분간 시험이 진행됩니다.\n이대로 진행 하시겠습니까?");
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<ExamData> make = db.make_Exam(ExamNum);
                        if (make.size() > 0) {
                            int RoomId = db.get_Exam_RoomId();
                            dialog.dismiss();
                            int[] ExamIdArr = new int[ExamNum];
                            for (int k = 0; k < ExamNum; k++)
                                ExamIdArr[k] = make.get(k).NoteId;

                            Intent i = new Intent((MainActivity) getActivity(), RandomExam.class);
                            i.putExtra("time", time * 60 * 1000);
                            i.putExtra("ExamIdArr", ExamIdArr);
                            i.putExtra("ExamNum", ExamNum);
                            i.putExtra("RoomId", RoomId);
                            startActivity(i);
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
        return view;
    }

    private void prepareListData(int classId) {
        ArrayList[] sub_list;
        ArrayList<ClassData> t_list;
        ArrayList<ClassData> t_list2;
        int count = 1;

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        t_list = c_db.set_title(classId, 0);
        for (int i = 0; i < t_list.size(); i++){
            listDataHeader.add(t_list.get(i).Title);
        }

//        t_list.clear();
        t_list2 = c_db.set_title(classId, 1);
        sub_list = new ArrayList[c_db.get_size(classId, 1)];
        for(int i =0;i<sub_list.length;i++)
            sub_list[i] = new ArrayList<String>();

        int i = 0, j = 0;
        while (c_db.get_size(classId, 0) > j) {
            if(c_db.get_size(classId, 1) - 1 == i){
                sub_list[j].add(t_list2.get(i).Title);
                listDataChild.put(listDataHeader.get(j), sub_list[j]);
                break;
            }

            if (count == t_list2.get(i).Tag) {
                sub_list[j].add(t_list2.get(i).Title);
                i++;
            }
            else {
                listDataChild.put(listDataHeader.get(j), sub_list[j]);
                count++;
                j++;
            }
        }
    }
}
