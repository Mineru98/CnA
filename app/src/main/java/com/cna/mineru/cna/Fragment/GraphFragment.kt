package com.cna.mineru.cna.Fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.cna.mineru.cna.DB.GraphDataSQLClass
import com.cna.mineru.cna.DB.GraphSQLClass
import com.cna.mineru.cna.DB.UserSQLClass
import com.cna.mineru.cna.DTO.GraphData
import com.cna.mineru.cna.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

import java.util.ArrayList
import java.util.Calendar


class GraphFragment : Fragment() {
    internal lateinit var view: View
    internal lateinit var u_db: UserSQLClass
    internal lateinit var db: GraphSQLClass
    internal lateinit var d_db: GraphDataSQLClass
    internal lateinit var list: ArrayList<GraphData>
    internal lateinit var s_list: ArrayList<String>
    private var chart: PieChart? = null
    private var month: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_graph, container, false)
        chart = view.findViewById(R.id.chartView)

        month = Calendar.getInstance().get(Calendar.MONTH) + 1

        list = ArrayList<GraphData>()
        s_list = ArrayList()

        db = GraphSQLClass(getActivity())
        u_db = UserSQLClass(getActivity())
        d_db = GraphDataSQLClass(getActivity())
        drawGraph()
        return view
    }

    private fun drawGraph() {
        val yValues = ArrayList<PieEntry>()
        list.clear()
        s_list.clear()
        list = db.load_values()
        s_list = d_db.get_title(u_db.classId, month)

        val total = list.size
        val select = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

        for (i in 0 until total) {
            when (list[i].note_type) {
                1 -> select[0] += 1f
                2 -> select[1] += 1f
                3 -> select[2] += 1f
                4 -> select[3] += 1f
                5 -> select[4] += 1f
                6 -> select[5] += 1f
                7 -> select[6] += 1f
                8 -> select[7] += 1f
                9 -> select[8] += 1f
                10 -> select[9] += 1f
                11 -> select[10] += 1f
                12 -> select[11] += 1f
                13 -> select[12] += 1f
                14 -> select[13] += 1f
                15 -> select[14] += 1f
                16 -> select[15] += 1f
            }
        }

        chart!!.description.isEnabled = false
        chart!!.setExtraOffsets(0f, 0f, 0f, 0f)

        chart!!.dragDecelerationFrictionCoef = 0.95f
        chart!!.setTouchEnabled(false)
        chart!!.isDrawHoleEnabled = false
        chart!!.setHoleColor(Color.RED)
        chart!!.setEntryLabelColor(Color.BLACK)
        chart!!.transparentCircleRadius = 61f
        chart!!.animateX(1500)

        for (i in 0..15) {
            if (select[i] > 0)
                yValues.add(PieEntry(select[i] / total * 100f, s_list[i]))
        }

        val description = Description()
        when (u_db.classId) {
            11 -> if (month > 7)
                description.text = "초등학교 1학년 2학기 수학과정"
            else
                description.text = "초등학교 1학년 1학기 수학과정"
            12 -> if (month > 7)
                description.text = "초등학교 2학년 2학기 수학과정"
            else
                description.text = "초등학교 2학년 1학기 수학과정"
            13 -> if (month > 7)
                description.text = "초등학교 3학년 2학기 수학과정"
            else
                description.text = "초등학교 3학년 1학기 수학과정"
            14 -> if (month > 7)
                description.text = "초등학교 4학년 2학기 수학과정"
            else
                description.text = "초등학교 4학년 1학기 수학과정"
            15 -> if (month > 7)
                description.text = "초등학교 5학년 2학기 수학과정"
            else
                description.text = "초등학교 5학년 1학기 수학과정"
            16 -> if (month > 7)
                description.text = "초등학교 6학년 2학기 수학과정"
            else
                description.text = "초등학교 6학년 1학기 수학과정"
            21 -> if (month > 7)
                description.text = "중학교 1학년 2학기 수학과정"
            else
                description.text = "중학교 1학년 1학기 수학과정"
            22 -> if (month > 7)
                description.text = "중학교 2학년 2학기 수학과정"
            else
                description.text = "중학교 2학년 1학기 수학과정"
            23 -> if (month > 7)
                description.text = "중학교 3학년 2학기 수학과정"
            else
                description.text = "중학교 3학년 1학기 수학과정"
            31 -> if (month > 7)
                description.text = "고등학교 1학년 2학기 수학과정"
            else
                description.text = "고등학교 1학년 1학기 수학과정"
            32 -> if (month > 7)
                description.text = "고등학교 2학년 2학기 수학과정"
            else
                description.text = "고등학교 2학년 1학기 수학과정"
            else -> description.text = "수학과정"
        }
        description.textSize = 15f
        chart!!.description = description

        chart!!.animateY(2000)

        val dataSet = PieDataSet(yValues, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val COLOR_ = intArrayOf(
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
        )
        dataSet.setColors(*COLOR_)
        val data = PieData(dataSet)
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.BLACK)

        chart!!.data = data
    }

    override fun onResume() {
        super.onResume()
        drawGraph()
    }

}
