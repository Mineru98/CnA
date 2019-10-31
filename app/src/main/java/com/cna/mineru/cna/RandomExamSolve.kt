package com.cna.mineru.cna

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager

import com.cna.mineru.cna.Adapter.FragmentExampleAdapter
import com.cna.mineru.cna.DB.ExamSQLClass
import com.cna.mineru.cna.Fragment.ExamFragmentChild.DoExamFragmentChild.RandomExamSolveFragment
import com.cna.mineru.cna.Utils.CustomDialog
import com.cna.mineru.cna.Utils.SignDialog

import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_random_solve.*

class RandomExamSolve : AppCompatActivity() {
    private var db: ExamSQLClass? = null

    private var viewPager: ViewPager? = null

    private var btn_no: TextView? = null
    private var btn_ok: TextView? = null
    private var tv_time: TextView? = null
    private var tv_title: TextView? = null
    private var tv_count: TextView? = null

    private var RoomId: Int = 0
    private var ExamNum: Int = 0
    private var ExamIdArr: IntArray? = null
    private var CurrentViewId: Int = 0

    private var ResultCheckList: IntArray? = null
    private var ResultExamArr: LongArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_solve)
        setSupportActionBar(toolbar as Toolbar?)

        val i_list = ArrayList<Int>()
        val b_list = ArrayList<Int>()
        db = ExamSQLClass(this)
        val dialog = SignDialog(this@RandomExamSolve)

        ExamIdArr = getIntent().getIntArrayExtra("ExamIdArr")
        ExamNum = getIntent().getIntExtra("ExamNum", 0)
        RoomId = getIntent().getIntExtra("RoomId", 0)
        ResultCheckList = IntArray(ExamNum)
        ResultExamArr = getIntent().getLongArrayExtra("ResultExamArr")

        CurrentViewId = 0


        //시간 계산을 한지 얼마 되지 않은 상태에선 시간 측정이
        //제대로 이루어지지 않는 버그로 인해서 10초 미만 동안 문제를 푼 경우엔
        //그냥 10초 고정.
        for (i in 0 until ExamNum) {
            if (ResultExamArr!![i] < 0)
                ResultExamArr!![i] = 10000
        }

        @SuppressLint("DefaultLocale")
        val easy_outTime = String.format(
            "%02d:%02d",
            ResultExamArr!![0] / 1000 / 60 % 60,
            ResultExamArr!![0] / 1000 % 60
        )
        tv_time!!.text = easy_outTime

        for (i in 0 until ExamNum) {
            i_list.add(ExamIdArr!![i])
            b_list.add(0)
            ResultCheckList!![i] = 0
        }

        btn_ok!!.setOnClickListener {
            if (CurrentViewId == ExamNum - 1) {
                val d = CustomDialog(14)
                d.show(supportFragmentManager, "exam finish")
                d.setDialogResult(object : CustomDialog.OnMyDialogResult {
                    override fun finish(result: Int) {
                        if (result == 1) {
                            db!!.update_result(ResultCheckList!!, ResultExamArr!!, RoomId, ExamIdArr!!)
                            val i = Intent(this@RandomExamSolve, ExamResultActivity::class.java)
                            i.putExtra("RoomId", RoomId)
                            startActivity(i)
                            this@RandomExamSolve.finish()
                        } else {
                            d.dismiss()
                        }
                    }

                    override fun finish(result: Int, email: String) {

                    }
                })
            } else {
                dialog.call_btn_ok()
            }
            val h = Handler()
            h.postDelayed(splashHandler(), 1000)
            ResultCheckList!![CurrentViewId] = 1
        }

        btn_no!!.setOnClickListener {
            if (CurrentViewId == ExamNum - 1) {
                val d = CustomDialog(14)
                d.show(getSupportFragmentManager(), "exam finish")
                d.setDialogResult(object : CustomDialog.OnMyDialogResult {
                    override fun finish(result: Int) {
                        if (result == 1) {
                            db!!.update_result(ResultCheckList!!, ResultExamArr!!, RoomId, ExamIdArr!!)
                            val i = Intent(this@RandomExamSolve, ExamResultActivity::class.java)
                            i.putExtra("RoomId", RoomId)
                            startActivity(i)
                            this@RandomExamSolve.finish()
                        } else {
                            d.dismiss()
                        }
                    }

                    override fun finish(result: Int, email: String) {

                    }
                })
            } else {
                dialog.call_btn_x()
            }
            val h = Handler()
            h.postDelayed(splashHandler(), 1000)
            ResultCheckList!![CurrentViewId] = 0
        }

        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(i: Int) {
                val text_count = i + 1
                @SuppressLint("DefaultLocale")
                val easy_outTime_each = String.format(
                    "%02d:%02d",
                    ResultExamArr!![i] / 1000 / 60 % 60,
                    ResultExamArr!![i] / 1000 % 60
                )
                tv_count!!.text = "A$text_count"
                tv_time!!.text = easy_outTime_each
                for (t in 0 until ExamNum) {
                    if (t == ExamNum - 1) {
                        CurrentViewId = i
                    }
                }
            }

            override fun onPageScrollStateChanged(i: Int) {

            }

        })
        setupViewPager(viewPager!!)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = FragmentExampleAdapter(getSupportFragmentManager())
        for (i in 0 until ExamNum) {
            val RandomexamSolveFragment = RandomExamSolveFragment(ExamIdArr!![i])
            adapter.addFragment(RandomexamSolveFragment)
        }
        viewPager.setAdapter(adapter)
    }

    private fun getItem(i: Int): Int {
        return viewPager!!.getCurrentItem() + i
    }

    private inner class splashHandler : Runnable {
        override fun run() {
            btn_ok!!.isEnabled = true // 클릭 유효화
            viewPager!!.setCurrentItem(getItem(+1), true)
        }
    }

    override fun onBackPressed() {
        val d = CustomDialog(10)
        d.show(supportFragmentManager, "exam finish 2")
        d.setDialogResult(object : CustomDialog.OnMyDialogResult {
            override fun finish(result: Int) {
                if (result == 1) {
                    this@RandomExamSolve.finish()
                } else {
                    d.dismiss()
                }
            }

            override fun finish(result: Int, email: String) {

            }
        })
    }
}
