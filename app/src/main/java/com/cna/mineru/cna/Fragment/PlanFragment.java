package com.cna.mineru.cna.Fragment;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import com.cna.mineru.cna.AddPlan;
import com.cna.mineru.cna.DB.PlanSQLClass;
import com.cna.mineru.cna.DTO.PlanData;
import com.cna.mineru.cna.MainActivity;
import com.cna.mineru.cna.R;
import com.cna.mineru.cna.Adapter.RcAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PlanFragment extends Fragment {

    View view;
    PlanSQLClass db;
    public RcAdapter mAdapater;
    private TextView tv_left;
    public RecyclerView rv;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<PlanData> list  = new ArrayList<PlanData>();
    private CalendarView calendarView;
    private FloatingActionButton fb;

    public PlanFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_plan, container, false);
        fb = (FloatingActionButton)view.findViewById(R.id.fb_add);
        tv_left = (TextView)view.findViewById(R.id.tv_left);
        calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        rv = (RecyclerView)view.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        rv.addItemDecoration(new DividerItemDecoration(getActivity(),linearLayoutManager.getOrientation()));
        rv.setLayoutManager(linearLayoutManager);

        db = new PlanSQLClass(getActivity());
        list = db.load_values(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));

        mAdapater = new RcAdapter(getContext(), R.layout.item, list);
        rv.setAdapter(mAdapater);
        tv_left.setText(new SimpleDateFormat("yyyy년 MM월 dd일").format(new Date(System.currentTimeMillis())));
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                month++;
                tv_left.setText(year+"년 " + month + "월 " + dayOfMonth + "일");
                list.clear();
                list = db.load_values(year+"-" + month + "-" + dayOfMonth);
                mAdapater = new RcAdapter(getContext(), R.layout.item, list);
                rv.setAdapter(mAdapater);
            }
        });

        fb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v){
                fb.setEnabled(false);
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 1000);
                MainActivity activity = (MainActivity)getActivity();
                Intent intent = new Intent(activity,AddPlan.class);
                startActivity(intent);
            }
        });

        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        list = db.load_values(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
        mAdapater = new RcAdapter(getContext(), R.layout.item, list);
        rv.setAdapter(mAdapater);
        tv_left.setText(new SimpleDateFormat("yyyy년 MM월 dd일").format(new Date(System.currentTimeMillis())));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date(System.currentTimeMillis()))));
        calendar.set(Calendar.MONTH,Integer.parseInt(new SimpleDateFormat("MM").format(new Date(System.currentTimeMillis())))-1);
        calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(new SimpleDateFormat("dd").format(new Date(System.currentTimeMillis()))));
        calendarView.setDate(calendar.getTimeInMillis(),true,true);
    }
    private class splashHandler implements Runnable{
        public void run()	{
            fb.setEnabled(true); // 클릭 유효화
        }
    }
}
