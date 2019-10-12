package com.cna.mineru.cna;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cna.mineru.cna.DB.ExamSQLClass;
import com.cna.mineru.cna.DB.HomeSQLClass;
import com.cna.mineru.cna.DB.UserSQLClass;
import com.cna.mineru.cna.DTO.ExamData;
import com.cna.mineru.cna.DTO.HomeData;
import com.cna.mineru.cna.Utils.MyValueFormatter;
import com.cna.mineru.cna.Utils.XAxisValueFormatter;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

public class ExamResultActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private ExamSQLClass db;
    private UserSQLClass u_db;
    private PieChart pChart;
    private BarChart bChart;

    private ImageView iv_profile;

    private int RoomId;
    private ArrayList<ExamData> list;
    private HomeSQLClass h_db;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_result);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        RelativeLayout top_left = findViewById(R.id.top_left);
        RelativeLayout top_right = findViewById(R.id.top_right);
        RelativeLayout top = findViewById(R.id.top);
        RelativeLayout top2 = findViewById(R.id.top2);

        TextView tv_1 = (TextView) findViewById(R.id.tv_1);
        TextView tv_2 = (TextView) findViewById(R.id.tv_2);
        TextView tv_3 = (TextView) findViewById(R.id.tv_3);
        TextView tv_score = (TextView) findViewById(R.id.tv_score);
        TextView tv_type = (TextView) findViewById(R.id.tv_type);
        TextView tv_name = (TextView) findViewById(R.id.tv_name);
        TextView tv_day = (TextView) findViewById(R.id.tv_day);
        TextView tv_time = (TextView) findViewById(R.id.tv_time);
        TextView tv_year = (TextView) findViewById(R.id.tv_year);

        EditText et_exam_num = (EditText) findViewById(R.id.et_exam_num);
        EditText et_exam_all_time = (EditText) findViewById(R.id.et_exam_all_time);
        EditText et_exam_time = (EditText) findViewById(R.id.et_exam_time);

        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        ImageView btn_cancel = (ImageView) findViewById(R.id.btn_cancel);

        Glide.with(this)
                .load(R.drawable.ic_profile)
                .centerCrop()
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(this))
                .into(iv_profile);

        {
            Description desc = new Description();
            desc.setText("");

            pChart = (PieChart) findViewById(R.id.chart1);
            pChart.setDescription(desc);
            pChart.setUsePercentValues(true);
            pChart.setExtraOffsets(5, 10, 5, 5);
            pChart.setDragDecelerationFrictionCoef(0.95f);
            pChart.setExtraOffsets(5.f, 5.f, 5.f, 5.f);
            pChart.setDrawHoleEnabled(false);
            pChart.setTransparentCircleColor(Color.WHITE);
            pChart.setTransparentCircleAlpha(110);
            pChart.setHoleRadius(58f);
            pChart.setTransparentCircleRadius(61f);
            pChart.setDrawCenterText(false);
            pChart.setRotationAngle(0);
            pChart.setRotationEnabled(true);
            pChart.setHighlightPerTapEnabled(true);

            pChart.setOnChartValueSelectedListener(this);
            pChart.animateY(1400, Easing.EaseInOutQuad);
            pChart.animateX(1400, Easing.EaseInOutQuad);
        }

        {
            bChart = findViewById(R.id.chartView);
            bChart.setOnChartValueSelectedListener(this);
            bChart.setDrawBarShadow(false);
            bChart.setDrawValueAboveBar(true);
            bChart.getDescription().setEnabled(false);
            bChart.animateY(750);
            bChart.animateX(750);
            bChart.setMaxVisibleValueCount(60);
            bChart.setScaleEnabled(false);
            bChart.setDragEnabled(true); // 이 코드는 직접 실행해봐야 알 수 있을 것 같다. false로 할 가능성이 있음.
            bChart.setTouchEnabled(false); // 현재 이 부분을 클릭 시 스클롤이 됨으로 비활성화해준다.
            bChart.setDrawGridBackground(false);
        }

        {
            ValueFormatter xAxisFormatter = new XAxisValueFormatter(bChart);
            XAxis xAxis = bChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(7);
            xAxis.setValueFormatter(xAxisFormatter);

            ValueFormatter custom = new MyValueFormatter("분");

            YAxis leftAxis = bChart.getAxisLeft();
            leftAxis.setLabelCount(4, false);
            leftAxis.setValueFormatter(custom);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinimum(0f);

            YAxis rightAxis = bChart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setLabelCount(4, false);
            rightAxis.setValueFormatter(custom);
            rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            rightAxis.setSpaceTop(15f);
            rightAxis.setAxisMinimum(0f);

            Legend l = bChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);
            l.setForm(Legend.LegendForm.CIRCLE);
            l.setFormSize(12f);
            l.setTextSize(12f);

        }

        Legend l = pChart.getLegend();
        l.setEnabled(false);
        Legend l2 = bChart.getLegend();
        l2.setEnabled(false);

        RoomId = getIntent().getIntExtra("RoomId",0);
        db = new ExamSQLClass(this);
        h_db = new HomeSQLClass(this);
        u_db = new UserSQLClass(this);
        list = db.get_point_values(RoomId);
        setData(list);
        b_setData(list);

        int score = 0;
        for(int i=0;i<list.size();i++)
            if(list.get(i).isSolved==1)
                score++;

        int all_score = (int)(((float)score/list.size()) * 100);
        tv_score.setText(""+all_score);
        tv_name.setText(u_db.get_Name());
        et_exam_num.setText(""+list.size());
        et_exam_time.setText("2");
        et_exam_all_time.setText("3");

        et_exam_all_time.setEnabled(false);
        et_exam_num.setEnabled(false);
        et_exam_time.setEnabled(false);
        btn_cancel.setOnClickListener(v -> finish());
    }

    private void setData(ArrayList<ExamData> list) {
        ArrayList<PieEntry> values = new ArrayList<>();

        float all_time = 0f;
        for (int i = 0; i<list.size(); i++)
            all_time += list.get(i).TTS;

        for (int i = 0; i<list.size(); i++)
            values.add(new PieEntry( all_time/list.get(i).TTS,list.get(i).Title));

        PieDataSet set1 = new PieDataSet(values, "Election Results");
        set1.setSliceSpace(3f);
        set1.setSelectionShift(5f);

        if (pChart.getData() != null &&
                pChart.getData().getDataSetCount() > 0) {
            set1 = (PieDataSet) pChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            pChart.getData().notifyDataChanged();
            pChart.notifyDataSetChanged();
        } else {

            final int[] COLOR_ = {
                    Color.rgb(123, 158, 211),
                    Color.rgb(240, 90, 148),
                    Color.rgb(249, 161, 91),
                    Color.rgb(235, 235, 235)
            };

            set1.setColors(COLOR_);
            set1.setValueLinePart1OffsetPercentage(80.f);
            set1.setValueLinePart1Length(0.3f);
            set1.setValueLinePart2Length(0.4f);
            set1.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            PieData data = new PieData(set1);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.BLACK);
            pChart.setData(data);
            pChart.highlightValues(null);

            pChart.invalidate();
        }
    }

    private void b_setData(ArrayList<ExamData> list) {
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i<list.size(); i++)
            values.add(new BarEntry( (i+1)* 1f,list.get(i).TTS / 1000f));

        BarDataSet set1;

        if (bChart.getData() != null &&
                bChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) bChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            bChart.getData().notifyDataChanged();
            bChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "정답");
            set1.setDrawIcons(false);
            set1.setColors(rgb("#ff33b5e5"), rgb("#ffff4444"));

            int startColor1 = ContextCompat.getColor(this, R.color.main_color);
            int startColor2 = ContextCompat.getColor(this, R.color.second_color);

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

            bChart.setData(data);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        /* 기존에는 ModifyItemActivity로 넘어갔지만 이 부분은 UX 적으로 문제가 있음으로 주석 처리.
        if(e.getX()!=0.0){
            Handler handler = new Handler();
            handler.postDelayed(new splashHandler(), 2000);
            bChart.setTouchEnabled(false);
            HomeData data;
            Intent i = new Intent(this, ModifyHomeItem.class);
            data = h_db.select_item(list.get((int)e.getX()-1).NoteId);
            i.putExtra("id", data.id);
            i.putExtra("title", data.title_text);
            i.putExtra("tag", data.Tag);
            i.putExtra("subtag", data.Subtag);
            startActivity(i);
            this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
        */
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        PieChart chart2 = (PieChart)this.findViewById(R.id.chart1);
        BarChart chartView = (BarChart)this.findViewById(R.id.chartView);
        LinearLayout.LayoutParams test = new LinearLayout.LayoutParams(chart2.getWidth(),chart2.getWidth());
        RelativeLayout.LayoutParams test2 = new RelativeLayout.LayoutParams(chartView.getWidth(),(int)(chartView.getWidth()*1.5));
        pChart.setLayoutParams(test);
        bChart.setLayoutParams(test2);
    }

    @Override
    public void onNothingSelected() {

    }

    private class splashHandler implements Runnable{
        public void run()	{
            bChart.setTouchEnabled(false); // 위의 코드와 동일하게 여전히 false를 유지해준다.
            // bChart.setTouchEnabled(true); // 클릭 유효화
        }
    }
}
