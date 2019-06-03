package com.cna.mineru.cna;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cna.mineru.cna.DB.ExamSQLClass;
import com.cna.mineru.cna.DB.HomeSQLClass;
import com.cna.mineru.cna.DTO.ExamData;
import com.cna.mineru.cna.DTO.HomeData;
import com.cna.mineru.cna.Utils.MyValueFormatter;
import com.cna.mineru.cna.Utils.XAxisValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

public class ExamResultActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private ExamSQLClass db;

    private BarChart chart;
//    private PieChart CircleView;

    private int RoomId;
    private ArrayList<ExamData> list;
    private HomeSQLClass h_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_result);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        ImageView btn_cancel = (ImageView) findViewById(R.id.btn_cancel);

        {
            chart = findViewById(R.id.chartView);
            chart.setOnChartValueSelectedListener(this);
            chart.setDrawBarShadow(false);
            chart.setDrawValueAboveBar(true);
            chart.getDescription().setEnabled(false);
            chart.animateY(750);
            chart.setMaxVisibleValueCount(60);
            chart.setScaleEnabled(false);
            chart.setDragEnabled(true);
            chart.setDrawGridBackground(false);
        }


        {
            ValueFormatter xAxisFormatter = new XAxisValueFormatter(chart);
            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(7);
            xAxis.setValueFormatter(xAxisFormatter);

            ValueFormatter custom = new MyValueFormatter("초");

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setLabelCount(8, false);
            leftAxis.setValueFormatter(custom);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinimum(0f);

            YAxis rightAxis = chart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setLabelCount(8, false);
            rightAxis.setValueFormatter(custom);
            rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            rightAxis.setSpaceTop(15f);
            rightAxis.setAxisMinimum(0f);

            Legend l = chart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);
            l.setForm(Legend.LegendForm.CIRCLE);
            l.setFormSize(12f);
            l.setTextSize(12f);

        }
        RoomId = getIntent().getIntExtra("RoomId",0);
        db = new ExamSQLClass(this);
        h_db = new HomeSQLClass(this);
        list = db.get_point_values(RoomId);
        setData(list);

        btn_cancel.setOnClickListener(v -> finish());
    }

    private void setData(ArrayList<ExamData> list) {
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i<list.size(); i++)
            values.add(new BarEntry( (i+1)* 1f,list.get(i).TTS / 1000f));

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "정답");
            set1.setDrawIcons(false);
            set1.setColors(rgb("#ff33b5e5"), rgb("#ffff4444"));

            int startColor1 = ContextCompat.getColor(this, android.R.color.holo_blue_light);
            int startColor2 = ContextCompat.getColor(this, android.R.color.holo_red_light);

            List<GradientColor> gradientColors = new ArrayList<>();
            for(int i=0;i<list.size();i++) {
                if (list.get(i).isSolved==1) {
                    gradientColors.add(new GradientColor(startColor1, startColor1));
                } else {
                    gradientColors.add(new GradientColor(startColor2, startColor2));
                }
            }

            set1.setGradientColors(gradientColors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            chart.setData(data);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;

        Handler handler = new Handler();
        handler.postDelayed(new splashHandler(), 2000);
        chart.setTouchEnabled(false);
        HomeData data;
        Intent i = new Intent(this, ModifyHomeItem.class);
        data = h_db.select_item(list.get((int)e.getX()-1).NoteId);
        i.putExtra("id", data.id);
        i.putExtra("title", data.title_text);
        i.putExtra("tag", data.tag);
        startActivity(i);
        this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    @Override
    public void onNothingSelected() {

    }

    private class splashHandler implements Runnable{
        public void run()	{
            chart.setTouchEnabled(true); // 클릭 유효화
        }
    }
}
