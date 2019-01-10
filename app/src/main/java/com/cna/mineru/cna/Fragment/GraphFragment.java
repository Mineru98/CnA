package com.cna.mineru.cna.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class GraphFragment extends Fragment {
    View view;
    GraphSQLClass db;
    ArrayList<GraphData> list;
    private PieChart chart;

    public GraphFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graph, container, false);
        chart = view.findViewById(R.id.chartView);

        db =  new GraphSQLClass(getActivity());

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
        list = new ArrayList<GraphData>();

        list = db.load_values();

        int total = list.size();
        float[] select = {0f,0f,0f,0f,0f,0f,0f,0f};

        for(int i=0;i<total;i++){
            switch (list.get(i).tag){
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
            }
        }
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(0,0,0,0);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(false);
        chart.setHoleColor(Color.RED);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setTransparentCircleRadius(61f);
        chart.animateX(1500);

        for(int i=0;i<8;i++){
            if(i==0&&select[i]>0)
                yValues.add(new PieEntry(select[0]/total*100f,"자연수의 성질"));
            else if(i==1&&select[i]>0)
                yValues.add(new PieEntry(select[1]/total*100f,"정수와 유리수"));
            else if(i==2&&select[i]>0)
                yValues.add(new PieEntry(select[2]/total*100f,"문자와 식"));
            else if(i==3&&select[i]>0)
                yValues.add(new PieEntry(select[3]/total*100f,"함수"));
            else if(i==4&&select[i]>0)
                yValues.add(new PieEntry(select[4]/total*100f,"기본도형"));
            else if(i==5&&select[i]>0)
                yValues.add(new PieEntry(select[5]/total*100f,"평면도형"));
            else if(i==6&&select[i]>0)
                yValues.add(new PieEntry(select[6]/total*100f,"입체도형"));
            else if(i==7&&select[i]>0)
                yValues.add(new PieEntry(select[7]/total*100f,"통계"));
        }

        Description description = new Description();
        description.setText("중학교 수학과정"); //라벨
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
        };
        dataSet.setColors(COLOR_);
        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        chart.setData(data);
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

    }

}
