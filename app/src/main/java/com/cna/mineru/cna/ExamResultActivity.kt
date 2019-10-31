package com.cna.mineru.cna

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.cna.mineru.cna.DB.ExamSQLClass
import com.cna.mineru.cna.DB.HomeSQLClass
import com.cna.mineru.cna.DB.UserSQLClass
import com.cna.mineru.cna.DTO.ExamData
import com.cna.mineru.cna.DTO.HomeData
import com.cna.mineru.cna.Utils.MyValueFormatter
import com.cna.mineru.cna.Utils.XAxisValueFormatter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.model.GradientColor

import java.util.ArrayList

import jp.wasabeef.glide.transformations.CropCircleTransformation

import com.github.mikephil.charting.utils.ColorTemplate.rgb
import kotlinx.android.synthetic.main.activity_exam_result.*
import kotlinx.android.synthetic.main.include_toolbar_exam_result.*

@SuppressLint("Registered")
class ExamResultActivity : AppCompatActivity(), OnChartValueSelectedListener {

    private var db: ExamSQLClass? = null
    private var u_db: UserSQLClass? = null
    private var pChart: PieChart? = null
    private var bChart: BarChart? = null

    private var iv_profile: ImageView? = null

    private var RoomId: Int = 0
    private var list: ArrayList<ExamData>? = null
    private var h_db: HomeSQLClass? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_result)
        setSupportActionBar(toolbar as Toolbar)

        Glide.with(this)
            .load(R.drawable.ic_profile)
            .centerCrop()
            .crossFade()
            .bitmapTransform(CropCircleTransformation(this))
            .into(iv_profile!!)

        run {
            val desc = Description()
            desc.text = ""

            pChart = findViewById(R.id.chart1) as PieChart
            pChart!!.description = desc
            pChart!!.setUsePercentValues(true)
            pChart!!.setExtraOffsets(5f, 10f, 5f, 5f)
            pChart!!.dragDecelerationFrictionCoef = 0.95f
            pChart!!.setExtraOffsets(5f, 5f, 5f, 5f)
            pChart!!.isDrawHoleEnabled = false
            pChart!!.setTransparentCircleColor(Color.WHITE)
            pChart!!.setTransparentCircleAlpha(110)
            pChart!!.holeRadius = 58f
            pChart!!.transparentCircleRadius = 61f
            pChart!!.setDrawCenterText(false)
            pChart!!.rotationAngle = 0f
            pChart!!.isRotationEnabled = true
            pChart!!.isHighlightPerTapEnabled = true

            pChart!!.setOnChartValueSelectedListener(this)
            pChart!!.animateY(1400, Easing.EaseInOutQuad)
            pChart!!.animateX(1400, Easing.EaseInOutQuad)
        }

        run {
            bChart = findViewById(R.id.chartView)
            bChart!!.setOnChartValueSelectedListener(this)
            bChart!!.setDrawBarShadow(false)
            bChart!!.setDrawValueAboveBar(true)
            bChart!!.description.isEnabled = false
            bChart!!.animateY(750)
            bChart!!.animateX(750)
            bChart!!.setMaxVisibleValueCount(60)
            bChart!!.setScaleEnabled(false)
            bChart!!.isDragEnabled = true
            bChart!!.setDrawGridBackground(false)
        }

        run {
            val xAxisFormatter = bChart?.let { XAxisValueFormatter(it) }
            val xAxis = bChart!!.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            xAxis.labelCount = 7
            xAxis.valueFormatter = xAxisFormatter

            val custom = MyValueFormatter("분")

            val leftAxis = bChart!!.axisLeft
            leftAxis.setLabelCount(4, false)
            leftAxis.valueFormatter = custom
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            leftAxis.spaceTop = 15f
            leftAxis.axisMinimum = 0f

            val rightAxis = bChart!!.axisRight
            rightAxis.setDrawGridLines(false)
            rightAxis.setLabelCount(4, false)
            rightAxis.valueFormatter = custom
            rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            rightAxis.spaceTop = 15f
            rightAxis.axisMinimum = 0f

            val l = bChart!!.legend
            l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            l.orientation = Legend.LegendOrientation.HORIZONTAL
            l.setDrawInside(false)
            l.form = Legend.LegendForm.CIRCLE
            l.formSize = 12f
            l.textSize = 12f

        }

        val l = pChart!!.legend
        l.isEnabled = false
        val l2 = bChart!!.legend
        l2.isEnabled = false

        RoomId = getIntent().getIntExtra("RoomId", 0)
        db = ExamSQLClass(this)
        h_db = HomeSQLClass(this)
        u_db = UserSQLClass(this)
        list = db!!.get_point_values(RoomId)
        setData(list!!)
        b_setData(list!!)

        var score = 0
        for (i in list!!.indices)
            if (list!![i].isSolved === 1)
                score++

        val all_score = (score.toFloat() / list!!.size * 100).toInt()
        tv_score.text = "" + all_score
        tv_name.setText(u_db!!._Name)
        et_exam_num.setText("" + list!!.size)
        et_exam_time.setText("2")
        et_exam_all_time.setText("3")

        et_exam_all_time.isEnabled = false
        et_exam_num.isEnabled = false
        et_exam_time.isEnabled = false
        btn_cancel.setOnClickListener { v -> finish() }
    }

    private fun setData(list: ArrayList<ExamData>) {
        val values = ArrayList<PieEntry>()

        var all_time = 0f
        for (i in list.indices)
            all_time += list[i].TTS

        for (i in list.indices)
            values.add(PieEntry(all_time / list[i].TTS, list[i].Title))

        var set1 = PieDataSet(values, "Election Results")
        set1.sliceSpace = 3f
        set1.selectionShift = 5f

        if (pChart!!.data != null && pChart!!.data.dataSetCount > 0) {
            set1 = pChart!!.data.getDataSetByIndex(0) as PieDataSet
            set1.values = values
            pChart!!.data.notifyDataChanged()
            pChart!!.notifyDataSetChanged()
        } else {

            val COLOR_ = intArrayOf(
                Color.rgb(123, 158, 211),
                Color.rgb(240, 90, 148),
                Color.rgb(249, 161, 91),
                Color.rgb(235, 235, 235)
            )

            set1.setColors(*COLOR_)
            set1.valueLinePart1OffsetPercentage = 80f
            set1.valueLinePart1Length = 0.3f
            set1.valueLinePart2Length = 0.4f
            set1.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

            val data = PieData(set1)
            data.setValueFormatter(PercentFormatter())
            data.setValueTextSize(11f)
            data.setValueTextColor(Color.BLACK)
            pChart!!.data = data
            pChart!!.highlightValues(null)

            pChart!!.invalidate()
        }
    }

    private fun b_setData(list: ArrayList<ExamData>) {
        val values = ArrayList<BarEntry>()

        for (i in list.indices)
            values.add(BarEntry((i + 1) * 1f, list[i].TTS / 1000f))

        val set1: BarDataSet

        if (bChart!!.data != null && bChart!!.data.dataSetCount > 0) {
            set1 = bChart!!.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            bChart!!.data.notifyDataChanged()
            bChart!!.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(values, "정답")
            set1.setDrawIcons(false)
            set1.setColors(rgb("#ff33b5e5"), rgb("#ffff4444"))

            val startColor1 = ContextCompat.getColor(this, R.color.main_color)
            val startColor2 = ContextCompat.getColor(this, R.color.second_color)

            val gradientColors = ArrayList<GradientColor>()
            for (i in list.indices) {
                if (list[i].isSolved === 1) {
                    gradientColors.add(GradientColor(startColor1, startColor1))
                } else {
                    gradientColors.add(GradientColor(startColor2, startColor2))
                }
            }

            set1.gradientColors = gradientColors

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.barWidth = 0.9f

            bChart!!.data = data
        }
    }

    override fun onValueSelected(e: Entry?, h: Highlight) {
        if (e == null)
            return
        if (e.x.toDouble() != 0.0) {
            val handler = Handler()
            handler.postDelayed(splashHandler(), 2000)
            bChart!!.setTouchEnabled(false)
            val data: HomeData
            val i = Intent(this, ModifyHomeItem::class.java)
            data = h_db!!.select_item(list!![e.x.toInt() - 1].NoteId)
            i.putExtra("id", data.id)
            i.putExtra("title", data.title_text)
            i.putExtra("tag", data.Tag)
            i.putExtra("subtag", data.Subtag)
            startActivity(i)
            this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {

        val chart2 = this.findViewById(R.id.chart1) as PieChart
        val chartView = this.findViewById(R.id.chartView) as BarChart
        val test = LinearLayout.LayoutParams(chart2.width, chart2.width)
        val test2 = RelativeLayout.LayoutParams(chartView.width, (chartView.width * 1.5).toInt())
        pChart!!.layoutParams = test
        bChart!!.layoutParams = test2
    }

    override fun onNothingSelected() {

    }

    private inner class splashHandler : Runnable {
        override fun run() {
            bChart!!.setTouchEnabled(true) // 클릭 유효화
        }
    }
}
