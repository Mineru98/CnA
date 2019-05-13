package com.cna.mineru.cna.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cna.mineru.cna.Adapter.DefaultInputAdapter;
import com.cna.mineru.cna.DB.UserSQLClass;
import com.cna.mineru.cna.DTO.ListViewBtnItem;
import com.cna.mineru.cna.R;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class DefaultInputDialog extends DialogFragment {
    private OnMyDialogResult mDialogResult;
    private ListView lv;
    private DefaultInputAdapter mAdapater;
    private int mode;

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();

        View dig = inflater.inflate(R.layout.defaultinput_dialog, null);
        TextView tv_title = dig.findViewById(R.id.tv_title);
        TextView tv_detail = dig.findViewById(R.id.tv_detail);
        lv = dig.findViewById(R.id.listview1);
        TextView btn_cancel = (TextView) dig.findViewById(R.id.btn_cancel);

        UserSQLClass db = new UserSQLClass(this.getContext());

        SharedPreferences pref = getActivity().getSharedPreferences("isClassConfig", MODE_PRIVATE);
        mode = pref.getInt("isClassConfig", 0);
        ArrayList<ListViewBtnItem> items = new ArrayList<>();
        loadItemsFromDB(items);

        mAdapater = new DefaultInputAdapter(dig.getContext(), R.layout.item2, items) ;
        lv.setAdapter(mAdapater);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefaultInputDialog.this.getDialog().cancel();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                if(mode==0){
                    switch (position){
                        case 0:
                            SharedPreferences.Editor editor1 = pref.edit();
                            editor1.putInt("isClassConfig", 1);
                            editor1.apply();
                            mDialogResult.finish(1);
                            break;
                        case 1:
                            SharedPreferences.Editor editor2 = pref.edit();
                            editor2.putInt("isClassConfig", 2);
                            editor2.apply();
                            mDialogResult.finish(2);
                            break;
                        case 2:
                            SharedPreferences.Editor editor3 = pref.edit();
                            editor3.putInt("isClassConfig", 3);
                            editor3.apply();
                            mDialogResult.finish(3);
                            break;
                    }
                    DefaultInputDialog.this.getDialog().cancel();
                }else if(mode==1){
                    switch (position){
                        case 0:
                            mDialogResult.finish(1);
                            db.setClassId(11, "초등학교 1학년");
                            DefaultInputDialog.this.getDialog().cancel();
                            break;
                        case 1:
                            db.setClassId(12, "초등학교 2학년");
                            DefaultInputDialog.this.getDialog().cancel();
                            break;
                        case 2:
                            db.setClassId(13, "초등학교 3학년");
                            DefaultInputDialog.this.getDialog().cancel();
                            break;
                        case 3:
                            db.setClassId(14, "초등학교 4학년");
                            DefaultInputDialog.this.getDialog().cancel();
                            break;
                        case 4:
                            db.setClassId(15, "초등학교 6학년");
                            DefaultInputDialog.this.getDialog().cancel();
                            break;
                        case 5:
                            db.setClassId(16, "초등학교 6학년");
                            DefaultInputDialog.this.getDialog().cancel();
                            break;
                    }
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("isClassConfig", 0);
                    editor.apply();
                    DefaultInputDialog.this.getDialog().cancel();
                }else if(mode==2){
                    switch (position){
                        case 0:
                            db.setClassId(21, "중학교 1학년");
                            DefaultInputDialog.this.getDialog().cancel();
                            break;
                        case 1:
                            db.setClassId(22, "중학교 2학년");
                            DefaultInputDialog.this.getDialog().cancel();
                            break;
                        case 2:
                            db.setClassId(23, "중학교 3학년");
                            DefaultInputDialog.this.getDialog().cancel();
                            break;
                    }
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("isClassConfig", 0);
                    editor.apply();
                    DefaultInputDialog.this.getDialog().cancel();
                }else if(mode==3){
                    switch (position){
                        case 0:
                            db.setClassId(31, "고등학교 1학년");
                            DefaultInputDialog.this.getDialog().cancel();
                            break;
                        case 1:
                            db.setClassId(32, "고등학교 2학년");
                            DefaultInputDialog.this.getDialog().cancel();
                            break;
                        case 2:
                            db.setClassId(33, "고등학교 3학년");
                            DefaultInputDialog.this.getDialog().cancel();
                            break;
                    }
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("isClassConfig", 0);
                    editor.apply();
                    DefaultInputDialog.this.getDialog().cancel();
                }
            }
        }) ;

        builder.setView(dig);
        return builder.create();
    }

    public boolean loadItemsFromDB(ArrayList<ListViewBtnItem> list) {
        ListViewBtnItem item ;

        if (list == null) {
            list = new ArrayList<>() ;
        }

        if(mode==0) {
            item = new ListViewBtnItem() ;
            item.setText("초등학생") ;
            list.add(item) ;
            item = new ListViewBtnItem() ;
            item.setText("중학생") ;
            list.add(item) ;
            item = new ListViewBtnItem() ;
            item.setText("고등학생") ;
            list.add(item) ;
        }else if(mode==1){
            item = new ListViewBtnItem() ;
            item.setText("초등학생 1학년") ;
            list.add(item) ;
            item = new ListViewBtnItem() ;
            item.setText("초등학생 2학년") ;
            list.add(item) ;
            item = new ListViewBtnItem() ;
            item.setText("초등학생 3학년") ;
            list.add(item) ;
            item = new ListViewBtnItem() ;
            item.setText("초등학생 4학년") ;
            list.add(item) ;
            item = new ListViewBtnItem() ;
            item.setText("초등학생 5학년") ;
            list.add(item) ;
            item = new ListViewBtnItem() ;
            item.setText("초등학생 6학년") ;
            list.add(item) ;
        }else if(mode==2){
            item = new ListViewBtnItem() ;
            item.setText("중학생 1학년") ;
            list.add(item) ;
            item = new ListViewBtnItem() ;
            item.setText("중학생 2학년") ;
            list.add(item) ;
            item = new ListViewBtnItem() ;
            item.setText("중학생 3학년") ;
            list.add(item) ;
        }else if(mode==3){
            item = new ListViewBtnItem() ;
            item.setText("고등학생 1학년") ;
            list.add(item) ;
            item = new ListViewBtnItem() ;
            item.setText("고등학생 2학년") ;
            list.add(item) ;
            item = new ListViewBtnItem() ;
            item.setText("고등학생 3학년") ;
            list.add(item) ;
        }
        return true ;
    }

    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult {
        void finish(int _class);
    }
}
