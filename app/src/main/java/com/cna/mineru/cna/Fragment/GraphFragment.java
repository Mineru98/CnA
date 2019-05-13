package com.cna.mineru.cna.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cna.mineru.cna.DB.GraphDataSQLClass;
import com.cna.mineru.cna.DB.UserSQLClass;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.cna.mineru.cna.DB.GraphSQLClass;
import com.cna.mineru.cna.DTO.GraphData;
import com.cna.mineru.cna.R;

import java.util.ArrayList;
import java.util.Calendar;


public class GraphFragment extends Fragment {
    View view;
    UserSQLClass u_db;
    GraphSQLClass db;
    GraphDataSQLClass d_db;
    ArrayList<GraphData> list;
    ArrayList<String> s_list;
    private PieChart chart;

    public GraphFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graph, container, false);
        chart = view.findViewById(R.id.chartView);
//        Calendar cal = Calendar.getInstance();
//        int year = cal.get(cal.YEAR);
//        int month = cal.get(cal.MONTH) + 1;
//        int date = cal.get(cal.DATE);

        list = new ArrayList<>();
        s_list = new ArrayList<>();

        db = new GraphSQLClass(getActivity());
        u_db = new UserSQLClass(getActivity());
        d_db = new GraphDataSQLClass(getActivity());
        drawGraph();
        return view;
    }

    private void drawGraph(){
        ArrayList<PieEntry> yValues = new ArrayList<>();
        list.clear();
        s_list.clear();
        list = db.load_values();
        s_list = d_db.get_title(u_db.getClassId());

        int total = list.size();
        float[] select = {0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f};

        for(int i=0;i<total;i++){
            switch (list.get(i).note_type){
                case 1:
                    select[0]+=1;
                    break;
                case 2:
                    select[1]+=1;
                    break;
                case 3:
                    select[2]+=1;
                    break;
                case 4:
                    select[3]+=1;
                    break;
                case 5:
                    select[4]+=1;
                    break;
                case 6:
                    select[5]+=1;
                    break;
                case 7:
                    select[6]+=1;
                    break;
                case 8:
                    select[7]+=1;
                    break;
                case 9:
                    select[8]+=1;
                    break;
                case 10:
                    select[9]+=1;
                    break;
                case 11:
                    select[10]+=1;
                    break;
                case 12:
                    select[11]+=1;
                    break;
                case 13:
                    select[12]+=1;
                    break;
                case 14:
                    select[13]+=1;
                    break;
                case 15:
                    select[14]+=1;
                    break;
                case 16:
                    select[15]+=1;
                    break;
            }
        }

        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(0,0,0,0);

        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setTouchEnabled(false);
        chart.setDrawHoleEnabled(false);
        chart.setHoleColor(Color.RED);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setTransparentCircleRadius(61f);
        chart.animateX(1500);

        for(int i=0;i<16;i++) {
            if (select[i] > 0)
                yValues.add(new PieEntry(select[i] / total * 100f, s_list.get(i)));
        }

        Description description = new Description();
        if(10<u_db.getClassId()&&u_db.getClassId()<14)
            description.setText("초등학교 수학과정");
        else if(20<u_db.getClassId()&&u_db.getClassId()<24)
            description.setText("중학교 수학과정");
        else if(30<u_db.getClassId()&&u_db.getClassId()<40)
            description.setText("고등학교 수학과정");
        else
            description.setText("수학과정");
        description.setTextSize(15);
        chart.setDescription(description);

        chart.animateY(2000, Easing.EasingOption.EaseInOutCubic);

        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        final int[] COLOR_ = {
                Color.rgb(0xff, 0x3d, 0x00),
                Color.rgb(0xff, 0x91, 0x00),
                Color.rgb(0xff, 0xc4, 0x00),
                Color.rgb(0xff, 0xea, 0x00),
                Color.rgb(0xc6, 0xff, 0x00),
                Color.rgb(0x76, 0xff, 0x03),
                Color.rgb(0x00, 0xe6, 0x76),
                Color.rgb(0x1d, 0xe9, 0xb6),
                Color.rgb(0x00, 0xe5, 0xff),
                Color.rgb(0x00, 0xb0, 0xff),
                Color.rgb(0x29, 0x79, 0xff),
                Color.rgb(0x3d, 0x5a, 0xfe),
                Color.rgb(0x65, 0x1f, 0xff),
                Color.rgb(0xd5, 0x00, 0xf9),
                Color.rgb(0xf5, 0x00, 0x57),
                Color.rgb(0xff, 0x17, 0x44),
                Color.rgb(0x00, 0xe6, 0x76)
        };
        dataSet.setColors(COLOR_);
        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        chart.setData(data);
    }

    @Override
    public void onResume(){
        super.onResume();
        drawGraph();
    }

}
