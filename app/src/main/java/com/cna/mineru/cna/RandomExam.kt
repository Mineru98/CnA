package com.cna.mineru.cna


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.TextView

import com.cna.mineru.cna.Adapter.FragmentExampleAdapter
import com.cna.mineru.cna.Adapter.RandomViewPager
import com.cna.mineru.cna.DB.ExamSQLClass
import com.cna.mineru.cna.Fragment.ExamFragmentChild.DoExamFragmentChild.RandomExamFragment
import com.cna.mineru.cna.Utils.CustomDialog
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_random.*

class RandomExam : AppCompatActivity() {

    lateinit var eachCount: IntArray
    lateinit var eachBaseTime: LongArray
    lateinit var eachPauseTime: LongArray
    var CurrentViewId: Int = 0

    private var viewPager: ViewPager? = null
    private var time_out: TextView? = null
    private var tv_count: TextView? = null
    private var btn_ok: TextView? = null
    private var mAdView: AdView? = null

    private var setting_time: Int = 0

    private var ExamIdArr: IntArray? = null
    private var ResultExamArr: LongArray? = null
    private var myBaseTime: Long = 0
    private var count = 0
    private var RoomId = 0
    private var timeOut = false
    var ExamNum: Int = 0
    private var db: ExamSQLClass? = null
    private var b_list: ArrayList<Int>? = null

    var myTimer:MyTimer? = null
    var eachTimer:EachTimer? = null

    @SuppressLint("HandlerLeak")
    inner class MyTimer: Handler() {
        override fun handleMessage(msg: Message) {
            if (!timeOut) {
                time_out!!.text = getTimeOut()
                this.sendEmptyMessage(0)
            } else {
                eachTimer?.removeMessages(0)
                //현재 화면의 Index의 타이머만 run 상태로
                eachPauseTime[CurrentViewId] = SystemClock.elapsedRealtime() + 990
                //                for (int i = 0; i < ExamNum; i++) {
                //                    long outTime = eachPauseTime[i] - eachBaseTime[i];
                //                    @SuppressLint("DefaultLocale")
                //                    String easy_outTime = String.format("%02d:%02d:%02d", outTime / 1000 / 60, (outTime / 1000) % 60, (outTime % 1000) / 10);
                //                }
                val d = CustomDialog(11)
                d.show(getSupportFragmentManager(), "over time")
                d.setDialogResult(object : CustomDialog.OnMyDialogResult {
                    override fun finish(result: Int) {
                        if (result == 0) {
                            myTimer?.removeMessages(0)
                            eachTimer?.removeMessages(0)
                            for (i in 0 until ExamNum)
                                ResultExamArr!![i] = eachPauseTime[i] - eachBaseTime[i]
                            val i = Intent(this@RandomExam, RandomExamSolve::class.java)
                            i.putExtra("ExamIdArr", ExamIdArr)
                            i.putExtra("ExamNum", ExamNum)
                            i.putExtra("ResultExamArr", ResultExamArr)
                            i.putExtra("RoomId", RoomId)
                            startActivity(i)
                            this@RandomExam.finish()
                        } else {
                            myTimer?.sendEmptyMessage(0)
                            d.dismiss()
                        }
                    }

                    override fun finish(result: Int, email: String) {

                    }
                })
            }
        }
    }

    @SuppressLint("HandlerLeak")
    inner class EachTimer: Handler() {
        override fun handleMessage(msg: Message) {
            this.sendEmptyMessage(0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random)
        setSupportActionBar(toolbar as Toolbar)

        myTimer = MyTimer()
        eachTimer = EachTimer()

        val i_list = ArrayList<Int>()
        b_list = ArrayList()
        db = ExamSQLClass(this)
        MobileAds.initialize(this, "ca-app-pub-2774747966830250~5467102140")
        //        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        val adRequest = AdRequest.Builder().build()
        mAdView!!.loadAd(adRequest)

        setting_time = getIntent().getIntExtra("time", 0)
        ExamIdArr = getIntent().getIntArrayExtra("ExamIdArr")
        ExamNum = getIntent().getIntExtra("ExamNum", 0)
        RoomId = getIntent().getIntExtra("RoomId", 0)

        eachCount = IntArray(ExamNum)
        eachBaseTime = LongArray(ExamNum)
        eachPauseTime = LongArray(ExamNum)
        ResultExamArr = LongArray(ExamNum)
        CurrentViewId = 0

        for (i in 0 until ExamNum) {
            i_list.add(ExamIdArr!![i])
            b_list!!.add(0)
            eachCount[i] = 1
            eachBaseTime[i] = 0
            eachPauseTime[i] = 0
        }

        eachBaseTime[0] = SystemClock.elapsedRealtime()
        eachTimer?.sendEmptyMessage(0)

        myBaseTime = SystemClock.elapsedRealtime()//처음 시간 기록
        myTimer?.sendEmptyMessage(0)

        btn_ok!!.setOnClickListener {
            eachTimer?.sendEmptyMessage(0)
            if (CurrentViewId == ExamNum - 1) {
                val d = CustomDialog(9)
                d.show(supportFragmentManager, "exam finish 2")
                d.setDialogResult(object : CustomDialog.OnMyDialogResult {
                    override fun finish(result: Int) {
                        if (result == 1) {
                            myTimer?.removeMessages(0)
                            eachTimer?.removeMessages(0)
                            for (i in 0 until ExamNum)
                                ResultExamArr!![i] = eachPauseTime[i] - eachBaseTime[i]
                            val i = Intent(this@RandomExam, RandomExamSolve::class.java)
                            i.putExtra("ExamIdArr", ExamIdArr)
                            i.putExtra("ExamNum", ExamNum)
                            i.putExtra("ResultExamArr", ResultExamArr)
                            i.putExtra("RoomId", RoomId)
                            startActivity(i)
                            this@RandomExam.finish()
                        } else {
                            myTimer?.sendEmptyMessage(0)
                            d.dismiss()
                        }
                    }

                    override fun finish(result: Int, email: String) {

                    }
                })
            }
            val h = Handler()
            h.postDelayed(splashHandler(), 1000)
            count++
        }


        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(i: Int) {
                val text_count = i + 1
                tv_count!!.text = "Q$text_count"
                if (i == ExamNum - 1) {
                    btn_ok!!.text = "시험종료"
                } else {
                    btn_ok!!.text = "다음문제"
                }
                for (t in 0 until ExamNum) {
                    if (t == 0)
                        eachTimer?.removeMessages(0)//핸들러 메세지 제거

                    if (t == i) {
                        //현재 화면의 Index의 타이머만 run 상태로
                        if (eachBaseTime[t] != 0L) {
                            val now = SystemClock.elapsedRealtime()
                            myTimer?.sendEmptyMessage(0)
                            eachBaseTime[t] += now - eachPauseTime[t]
                        } else {
                            eachBaseTime[t] = SystemClock.elapsedRealtime()
                        }
                    } else if (t == CurrentViewId) {
                        //현재 화면의 Index가 아닌 타이머는 Pause 상태로
                        eachPauseTime[t] = SystemClock.elapsedRealtime()
                    }

                    if (t == ExamNum - 1) {
                        eachTimer?.sendEmptyMessage(0)
                        CurrentViewId = i
                    }
                }
            }

            override fun onPageScrollStateChanged(i: Int) {

            }


        })

        mAdView!!.setAdListener(object : AdListener() {
            override  fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        })

        setupViewPager(viewPager as RandomViewPager)
    }

    //현재시간을 계속 구해서 출력하는 메소드
    internal fun getTimeOut(): String {
        val now = SystemClock.elapsedRealtime() //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        val outTime = setting_time.toLong() - (now - myBaseTime)
        @SuppressLint("DefaultLocale")
        val easy_outTime = String.format(
            "%02d:%02d:%02d",
            outTime / 1000 / 60 / 60,
            outTime / 1000 / 60 % 60,
            outTime / 1000 % 60
        )
        if (easy_outTime == "00:00:00") {
            myTimer?.removeCallbacksAndMessages(0)
            myTimer?.removeMessages(0)
            timeOut = true
        }
        return easy_outTime
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = FragmentExampleAdapter(getSupportFragmentManager())
        for (i in 0 until ExamNum) {
            val randomexamFragment = RandomExamFragment(ExamIdArr!![i])
            adapter.addFragment(randomexamFragment)
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

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1000 -> {
                    myTimer?.removeMessages(0)
                    eachTimer?.removeMessages(0)
                    val result = data?.getIntExtra("result", 0)
                    val id = data?.getIntExtra("id", 0)
                    b_list!![viewPager!!.getCurrentItem() - 1] = result!!
                    if (id == 3) {
                        val d = CustomDialog(12)
                        d.show(supportFragmentManager, "exam finish 3")
                        d.setDialogResult(object : CustomDialog.OnMyDialogResult {
                            override fun finish(result: Int) {
                                if (result == 1) {
                                    for (i in 0 until ExamNum)
                                        ResultExamArr!![i] = eachPauseTime[i] - eachBaseTime[i]
                                    val i = Intent(this@RandomExam, RandomExamSolve::class.java)
                                    i.putExtra("randomArr", ExamIdArr)
                                    i.putExtra("ExamNum", ExamNum)
                                    i.putExtra("ResultArr", ResultExamArr)
                                    i.putExtra("RoomId", RoomId)
                                    startActivity(i)
                                    this@RandomExam.finish()
                                } else {
                                    d.dismiss()
                                    this@RandomExam.finish()
                                }
                            }

                            override fun finish(result: Int, email: String) {

                            }
                        })
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        val d = CustomDialog(15)
        d.show(supportFragmentManager, "exam finish 2")
        d.setDialogResult(object : CustomDialog.OnMyDialogResult {
            override fun finish(result: Int) {
                if (result == 1) {
                    //각 문제 문항 수 하나 빼기
                    db!!.delete_exam(RoomId)
                    this@RandomExam.finish()
                } else {
                    d.dismiss()
                }
            }

            override fun finish(result: Int, email: String) {

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        db!!.delete_exam(RoomId)
    }
}

