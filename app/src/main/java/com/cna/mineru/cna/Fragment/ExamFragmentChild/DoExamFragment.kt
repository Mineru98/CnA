package com.cna.mineru.cna.Fragment.ExamFragmentChild

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.ListView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment

import com.cna.mineru.cna.Adapter.ExamChipAdapter
import com.cna.mineru.cna.Adapter.ExpandableListAdapter
import com.cna.mineru.cna.DB.ExamSQLClass
import com.cna.mineru.cna.DB.GraphDataSQLClass
import com.cna.mineru.cna.DB.HomeSQLClass
import com.cna.mineru.cna.DB.UserSQLClass
import com.cna.mineru.cna.DTO.ChipData
import com.cna.mineru.cna.DTO.ClassData
import com.cna.mineru.cna.R
import com.cna.mineru.cna.RandomExam
import com.cna.mineru.cna.Utils.CustomDialog
import com.cna.mineru.cna.Utils.DefaultInputDialog
import com.xw.repo.BubbleSeekBar

import java.util.Calendar
import java.util.HashMap

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.ArrayList as ArrayList1

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class DoExamFragment : Fragment() {


    private var mAdapater: ExamChipAdapter? = null
    private var view = null
    private var btn_start: TextView? = null
    private var tv_lvE: TextView? = null

    private var seekBar_1: BubbleSeekBar? = null
    private var seekBar_2: BubbleSeekBar? = null
    private var seekBar_3: BubbleSeekBar? = null

    private var listView: ListView? = null
    private var sv: NestedScrollView? = null
    private var lvExp: ExpandableListView? = null

    private var h_db: HomeSQLClass? = null
    private var c_db: GraphDataSQLClass? = null
    private var db: ExamSQLClass? = null
    private var u_db: UserSQLClass? = null

    private var max_value: Int = 0
    private val isNext = false
    private var time = 0
    private var problem_num = 0
    private var ClassId = 0
    private var month: Int = 0

    private var t_count = 1

    private val list = ArrayList1<ChipData>()
    private var listAdapter: ExpandableListAdapter? = null
    private var listDataHeader: MutableList<String>? = null
    private var listDataChild: HashMap<String, List<String>>? = null

    @SuppressLint("ClickableViewAccessibility", "RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = ExamSQLClass(context)
        h_db = HomeSQLClass(context)
        c_db = GraphDataSQLClass(context)
        u_db = UserSQLClass(context)
        ClassId = u_db!!.classId
        time = 0
        problem_num = 0
        max_value = h_db!!.count
        month = Calendar.getInstance().get(Calendar.MONTH) + 1

        if (max_value < 10) {
            max_value = 10
        }

        view = inflater.inflate(R.layout.fragment_do_exam, container, false) as Nothing?

        mAdapater = ExamChipAdapter(activity!!, R.layout.chip_item, list)
        prepareListData(ClassId)
        when (ClassId) {
            11 -> if (month > 7)
                tv_lvE!!.text = "초등학교 1학년 2학기"
            else
                tv_lvE!!.text = "초등학교 1학년 1학기"
            12 -> if (month > 7)
                tv_lvE!!.text = "초등학교 2학년 2학기"
            else
                tv_lvE!!.text = "초등학교 2학년 1학기"
            13 -> if (month > 7)
                tv_lvE!!.text = "초등학교 3학년 2학기"
            else
                tv_lvE!!.text = "초등학교 3학년 1학기"
            14 -> if (month > 7)
                tv_lvE!!.text = "초등학교 4학년 2학기"
            else
                tv_lvE!!.text = "초등학교 4학년 1학기"
            15 -> if (month > 7)
                tv_lvE!!.text = "초등학교 5학년 2학기"
            else
                tv_lvE!!.text = "초등학교 5학년 1학기"
            16 -> if (month > 7)
                tv_lvE!!.text = "초등학교 6학년 2학기"
            else
                tv_lvE!!.text = "초등학교 6학년 1학기"
            21 -> if (month > 7)
                tv_lvE!!.text = "중학교 1학년 2학기"
            else
                tv_lvE!!.text = "중학교 1학년 1학기"
            22 -> if (month > 7)
                tv_lvE!!.text = "중학교 2학년 2학기"
            else
                tv_lvE!!.text = "중학교 2학년 1학기"
            23 -> if (month > 7)
                tv_lvE!!.text = "중학교 3학년 2학기"
            else
                tv_lvE!!.text = "중학교 3학년 1학기"
            31 -> if (month > 7)
                tv_lvE!!.text = "고등학교 1학년 2학기"
            else
                tv_lvE!!.text = "고등학교 1학년 1학기"
            32 -> if (month > 7)
                tv_lvE!!.text = "고등학교 2학년 2학기"
            else
                tv_lvE!!.text = "고등학교 2학년 1학기"
            33 -> if (month > 7)
                tv_lvE!!.text = "고등학교 3학년 2학기"
            else
                tv_lvE!!.text = "고등학교 3학년 1학기"
            else -> if (month > 7)
                tv_lvE!!.text = "2학기"
            else
                tv_lvE!!.text = "1학기"
        }

        listAdapter = ExpandableListAdapter(context, listDataHeader, listDataChild)
        listView!!.adapter = mAdapater
        lvExp!!.setAdapter(listAdapter)

        listView!!.setOnTouchListener { v, event ->
            sv!!.requestDisallowInterceptTouchEvent(true)
            false
        }

        lvExp!!.setOnTouchListener { v, event ->
            sv!!.requestDisallowInterceptTouchEvent(true)
            false
        }

        lvExp!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            list.add(
                ChipData(
                    t_count++,
                    listDataChild!![listDataHeader!![groupPosition]]!![childPosition]
                )
            )
            mAdapater = ExamChipAdapter(v.context, R.layout.chip_item, list)
            val tv = parent.findViewById<View>(R.id.lblListItem) as TextView
            listView!!.adapter = mAdapater
            false
        }

        lvExp!!.setOnGroupExpandListener { }

        lvExp!!.setOnGroupCollapseListener { }

        seekBar_1!!.setProgress(25f)
        seekBar_1!!.configBuilder
            .min(1f)
            .max(max_value.toFloat())
            .build()

        seekBar_1!!.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
            override fun onProgressChanged(
                bubbleSeekBar: BubbleSeekBar,
                progress: Int,
                progressFloat: Float,
                fromUser: Boolean
            ) {
                seekBar_3!!.setProgress((seekBar_2!!.progress * seekBar_1!!.progress).toFloat())
            }

            override fun getProgressOnActionUp(
                bubbleSeekBar: BubbleSeekBar,
                progress: Int,
                progressFloat: Float
            ) {

            }

            override fun getProgressOnFinally(
                bubbleSeekBar: BubbleSeekBar,
                progress: Int,
                progressFloat: Float,
                fromUser: Boolean
            ) {
            }
        }

        seekBar_2!!.setProgress(25f)
        seekBar_2!!.configBuilder
            .min(1f)
            .max(10f)
            .build()
        seekBar_2!!.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
            override fun onProgressChanged(
                bubbleSeekBar: BubbleSeekBar,
                progress: Int,
                progressFloat: Float,
                fromUser: Boolean
            ) {
                seekBar_3!!.setProgress((seekBar_2!!.progress * seekBar_1!!.progress).toFloat())
            }

            override fun getProgressOnActionUp(
                bubbleSeekBar: BubbleSeekBar,
                progress: Int,
                progressFloat: Float
            ) {

            }

            override fun getProgressOnFinally(
                bubbleSeekBar: BubbleSeekBar,
                progress: Int,
                progressFloat: Float,
                fromUser: Boolean
            ) {

            }
        }

        seekBar_3!!.setProgress(25f)
        seekBar_3!!.configBuilder
            .min(0f)
            .max(60f)
            .build()
        seekBar_3!!.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
            override fun onProgressChanged(
                bubbleSeekBar: BubbleSeekBar,
                progress: Int,
                progressFloat: Float,
                fromUser: Boolean
            ) {

            }

            override fun getProgressOnActionUp(
                bubbleSeekBar: BubbleSeekBar,
                progress: Int,
                progressFloat: Float
            ) {

            }

            override fun getProgressOnFinally(
                bubbleSeekBar: BubbleSeekBar,
                progress: Int,
                progressFloat: Float,
                fromUser: Boolean
            ) {

            }
        }

        sv!!.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            seekBar_1!!.correctOffsetWhenContainerOnScrolling()
            seekBar_2!!.correctOffsetWhenContainerOnScrolling()
            seekBar_3!!.correctOffsetWhenContainerOnScrolling()
        })

        btn_start!!.setOnClickListener {
            if (!u_db!!.isClassChecked) {
                val d = DefaultInputDialog()
                activity?.supportFragmentManager?.let { it1 -> d.show(it1, "setting") }
                d.setDialogResult(object : DefaultInputDialog.OnMyDialogResult {
                    override fun finish(_class: Int) {
                        val d2 = DefaultInputDialog()
                        activity?.supportFragmentManager?.let { it1 -> d2.show(it1, "setting2") }
                        d2.setDialogResult(object : DefaultInputDialog.OnMyDialogResult {
                            override fun finish(_class: Int) {

                            }
                        })
                    }
                })
            } else {
                val d = CustomDialog(5, seekBar_3!!.progress)
                activity?.supportFragmentManager?.let { it1 -> d.show(it1, "alter") }
                d.setDialogResult(object : CustomDialog.OnMyDialogResult {
                    override fun finish(result: Int) {
                        if (result == 1) {
                            val time = seekBar_3!!.progress
                            val ExamNum = seekBar_1!!.progress
                            val make = db!!.make_Exam(ExamNum)
                            if (make.size > 0) {
                                val RoomId = db!!._Exam_RoomId
                                d.dismiss()
                                val ExamIdArr = IntArray(ExamNum)
                                for (k in 0 until ExamNum)
                                    ExamIdArr[k] = make.get(k).NoteId

                                val i = Intent(getActivity(), RandomExam::class.java)
                                i.putExtra("time", time * 60 * 1000)
                                i.putExtra("ExamIdArr", ExamIdArr)
                                i.putExtra("ExamNum", ExamNum)
                                i.putExtra("RoomId", RoomId)
                                startActivity(i)
                            }
                        } else
                            d.dismiss()
                    }

                    override fun finish(result: Int, email: String) {

                    }
                })
            }
        }
        return view
    }

    private fun prepareListData(classId: Int) {
        var sub_list: ArrayList<ArrayList<String>>? = null
        val t_list: ArrayList1<ClassData>
        val t_list2: ArrayList1<ClassData>
        var count = 1

        listDataHeader = ArrayList1()
        listDataChild = HashMap()

        t_list = c_db!!.set_title(classId, 0, month)
        for (i in t_list.indices) {
            listDataHeader!!.add(t_list[i].Title)
        }

        t_list2 = c_db!!.set_title(classId, 1, month)
        sub_list = ArrayList(c_db!!.get_size(classId,1,month))
        for (i in sub_list.indices)
            sub_list[i] = ArrayList()

        var i = 0
        var j = 0
        while (c_db!!.get_size(classId, 0, month) > j) {
            if (c_db!!.get_size(classId, 1, month) - 1 === i) {
                sub_list[j].add(t_list2[i].Title)
                listDataChild!![listDataHeader!![j]] = sub_list[j]
                break
            }

            if (count == t_list2[i].Tag) {
                sub_list[j].add(t_list2[i].Title)
                i++
            } else {
                listDataChild!![listDataHeader!![j]] = sub_list[j]
                count++
                j++
            }
        }
    }
}
