package com.cna.mineru.cna.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.cna.mineru.cna.Adapter.DefaultInputAdapter;
import com.cna.mineru.cna.Adapter.ExamChipAdapter;
import com.cna.mineru.cna.Adapter.ExpandableListAdapter;
import com.cna.mineru.cna.DB.GraphDataSQLClass;
import com.cna.mineru.cna.DB.UserSQLClass;
import com.cna.mineru.cna.DTO.ChipData;
import com.cna.mineru.cna.DTO.ClassData;
import com.cna.mineru.cna.DTO.ListViewBtnItem;
import com.cna.mineru.cna.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class ClassListDialog extends DialogFragment {
    private OnMyDialogResult mDialogResult;
    private ExpandableListView lvExp;
    private UserSQLClass u_db;
    private GraphDataSQLClass c_db;
    private int ClassId;
    private int month;
    private Calendar cal;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();

        View dig = inflater.inflate(R.layout.classlist_dialog, null);
        TextView tv_title = dig.findViewById(R.id.tv_title);
        TextView tv_detail = dig.findViewById(R.id.tv_detail);
        lvExp = (ExpandableListView) dig.findViewById(R.id.lvExp);
        TextView btn_cancel = (TextView) dig.findViewById(R.id.btn_cancel);

        u_db = new UserSQLClass(this.getContext());
        c_db = new GraphDataSQLClass(this.getActivity());

        cal = Calendar.getInstance();
        month = cal.get ( cal.MONTH ) + 1;
        prepareListData(u_db.getClassId());

        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        lvExp.setAdapter(listAdapter);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassListDialog.this.getDialog().cancel();
            }
        });

        lvExp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        lvExp.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                mDialogResult.finish(c_db.getTag(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition)),
                        c_db.getSubTag(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition)),
                        listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));
                ClassListDialog.this.dismiss();
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

        builder.setView(dig);
        return builder.create();
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

    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult {
        void finish(int Tag, int SubTag, String result);
    }
}
