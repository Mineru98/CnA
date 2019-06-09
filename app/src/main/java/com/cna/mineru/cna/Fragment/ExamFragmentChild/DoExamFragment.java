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
import android.widget.TextView;

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
import com.cna.mineru.cna.Utils.CustomDialog;
import com.cna.mineru.cna.Utils.DefaultInputDialog;
import com.cna.mineru.cna.Utils.InsertCodeDialog;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DoExamFragment extends Fragment {


    private ExamChipAdapter mAdapater;
    private View view;
    private TextView btn_start;
    private TextView tv_lvE;

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
    private int month;
    private Calendar cal;
    private int t_count = 1;

    private ArrayList<ChipData> list = new ArrayList<>();
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    public DoExamFragment() {

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
        time = 0;
        problem_num = 0;
        max_value = h_db.getCount();
        cal = Calendar.getInstance();
        month = cal.get ( cal.MONTH ) + 1;

        if (max_value < 10) {
            max_value = 10;
        }

        view = inflater.inflate(R.layout.fragment_do_exam, container, false);

        sv = (NestedScrollView) view.findViewById(R.id.sv);
        lvExp = (ExpandableListView) view.findViewById(R.id.lvExp);
        tv_lvE = (TextView) view.findViewById(R.id.tv_lvE);
        listView = (ListView) view.findViewById(R.id.listView);
        btn_start = (TextView) view.findViewById(R.id.btn_start);

        mAdapater = new ExamChipAdapter(getActivity(), R.layout.chip_item, list);
        prepareListData(ClassId);
        switch (ClassId){
            default:
                if(month>7)
                    tv_lvE.setText("2학기");
                else
                    tv_lvE.setText("1학기");
                break;
            case 11:
                if(month>7)
                    tv_lvE.setText("초등학교 1학년 2학기");
                else
                    tv_lvE.setText("초등학교 1학년 1학기");
                break;
            case 12:
                if(month>7)
                    tv_lvE.setText("초등학교 2학년 2학기");
                else
                    tv_lvE.setText("초등학교 2학년 1학기");
                break;
            case 13:
                if(month>7)
                    tv_lvE.setText("초등학교 3학년 2학기");
                else
                    tv_lvE.setText("초등학교 3학년 1학기");
                break;
            case 14:
                if(month>7)
                    tv_lvE.setText("초등학교 4학년 2학기");
                else
                    tv_lvE.setText("초등학교 4학년 1학기");
                break;
            case 15:
                if(month>7)
                    tv_lvE.setText("초등학교 5학년 2학기");
                else
                    tv_lvE.setText("초등학교 5학년 1학기");
                break;
            case 16:
                if(month>7)
                    tv_lvE.setText("초등학교 6학년 2학기");
                else
                    tv_lvE.setText("초등학교 6학년 1학기");
                break;
            case 21:
                if(month>7)
                    tv_lvE.setText("중학교 1학년 2학기");
                else
                    tv_lvE.setText("중학교 1학년 1학기");
                break;
            case 22:
                if(month>7)
                    tv_lvE.setText("중학교 2학년 2학기");
                else
                    tv_lvE.setText("중학교 2학년 1학기");
                break;
            case 23:
                if(month>7)
                    tv_lvE.setText("중학교 3학년 2학기");
                else
                    tv_lvE.setText("중학교 3학년 1학기");
                break;
            case 31:
                if(month>7)
                    tv_lvE.setText("고등학교 1학년 2학기");
                else
                    tv_lvE.setText("고등학교 1학년 1학기");
                break;
            case 32:
                if(month>7)
                    tv_lvE.setText("고등학교 2학년 2학기");
                else
                    tv_lvE.setText("고등학교 2학년 1학기");
                break;
            case 33:
                if(month>7)
                    tv_lvE.setText("고등학교 3학년 2학기");
                else
                    tv_lvE.setText("고등학교 3학년 1학기");
                break;
        }

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
                mAdapater = new ExamChipAdapter(v.getContext(), R.layout.chip_item, list);
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
                .max(max_value)
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
                if(!u_db.isClassChecked()){
                    DefaultInputDialog d = new DefaultInputDialog();
                    d.show(getActivity().getSupportFragmentManager(),"setting");
                    d.setDialogResult(new DefaultInputDialog.OnMyDialogResult() {
                        @Override
                        public void finish(int _class) {
                            DefaultInputDialog d2 = new DefaultInputDialog();
                            d2.show(getActivity().getSupportFragmentManager(),"setting2");
                            d2.setDialogResult(new DefaultInputDialog.OnMyDialogResult() {
                                @Override
                                public void finish(int _class) {

                                }
                            });
                        }
                    });
                }else{
                    CustomDialog d = new CustomDialog(5,seekBar_3.getProgress());
                    d.show(getActivity().getSupportFragmentManager(),"alter");
                    d.setDialogResult(new CustomDialog.OnMyDialogResult() {
                        @Override
                        public void finish(int _class) {
                            if(_class==1) {
                                int time = seekBar_3.getProgress();
                                int ExamNum = seekBar_1.getProgress();
                                ArrayList<ExamData> make = db.make_Exam(ExamNum);
                                if (make.size() > 0) {
                                    int RoomId = db.get_Exam_RoomId();
                                    d.dismiss();
                                    int[] ExamIdArr = new int[ExamNum];
                                    for (int k = 0; k < ExamNum; k++)
                                        ExamIdArr[k] = make.get(k).NoteId;

                                    Intent i = new Intent(getActivity(), RandomExam.class);
                                    i.putExtra("time", time * 60 * 1000);
                                    i.putExtra("ExamIdArr", ExamIdArr);
                                    i.putExtra("ExamNum", ExamNum);
                                    i.putExtra("RoomId", RoomId);
                                    startActivity(i);
                                }
                            } else
                                d.dismiss();
                        }

                        @Override
                        public void finish(int result, String email) {

                        }
                    });
                }
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

        t_list = c_db.set_title(classId, 0, month);
        for (int i = 0; i < t_list.size(); i++){
            listDataHeader.add(t_list.get(i).Title);
        }

        t_list2 = c_db.set_title(classId, 1, month);
        sub_list = new ArrayList[c_db.get_size(classId, 1, month)];
        for(int i =0;i<sub_list.length;i++)
            sub_list[i] = new ArrayList<String>();

        int i = 0, j = 0;
        while (c_db.get_size(classId, 0,month) > j) {
            if(c_db.get_size(classId, 1,month) - 1 == i){
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
