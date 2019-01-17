package com.cna.mineru.cna.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.cna.mineru.cna.AddNote;
import com.cna.mineru.cna.DB.GraphSQLClass;
import com.cna.mineru.cna.DB.HomeSQLClass;
import com.cna.mineru.cna.DTO.HomeData;
import com.cna.mineru.cna.Adapter.GridAdapter;
import com.cna.mineru.cna.ModifyHomeItem;
import com.cna.mineru.cna.R;
import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private GridAdapter mAdapater;
    private ArrayList<HomeData> list  = new ArrayList<HomeData>();
    private GridView gv;
    private HomeSQLClass db;
    private GraphSQLClass gp_db;
    private FloatingActionButton fb;

    public HomeFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fb = (FloatingActionButton)view.findViewById(R.id.fb_add);
        db = new HomeSQLClass(getActivity());
        gp_db = new GraphSQLClass(getActivity());
        list = db.load_values();

        mAdapater = new GridAdapter(getContext(), R.layout.row, list);
        gv = (GridView)view.findViewById(R.id.gridView1);
        gv.setAdapter(mAdapater);

        fb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v){
                list.clear();
                fb.setEnabled(false);
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 1000);
                Intent i =new Intent(getActivity(),AddNote.class);
                startActivity(i);
            }
        });

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gv.setEnabled(false);
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 1000);

                HomeData data;
                data = db.select_item(list.get(position).id);
                Intent i = new Intent(getActivity(),ModifyHomeItem.class);
                i.putExtra("id", data.id);
                i.putExtra("title", data.title_text);
                i.putExtra("tag", data.tag);
                startActivity(i);
            }
        });

        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                try{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("오답노트 삭제");
                    builder.setMessage("정말로 삭제하시겠습니까?");
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    db.delete_item(list.get(position).id);
                                    gp_db.delete_value(list.get(position).id);
                                    onResume();
                                }
                            });
                    builder.setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();

                }
                catch (Exception e) {
                }
                return true;
            }
        });
        return view;
    }

    private class splashHandler implements Runnable{
        public void run()	{
            fb.setEnabled(true); // 클릭 유효화
            gv.setEnabled(true);

        }
    }
    @Override
    public void onResume() {
        super.onResume();
        list = db.load_values();
        mAdapater = new GridAdapter(getContext(), R.layout.row, list);
        gv.setAdapter(mAdapater);
    }
}
