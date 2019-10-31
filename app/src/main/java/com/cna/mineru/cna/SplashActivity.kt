package com.cna.mineru.cna

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView

import androidx.appcompat.app.AppCompatActivity

import com.cna.mineru.cna.DB.GraphDataSQLClass
import com.cna.mineru.cna.DB.UserSQLClass

import android.view.View

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_page)

        val db = UserSQLClass(this@SplashActivity)
        val c_db = GraphDataSQLClass(this@SplashActivity)

        val animation_view = findViewById<View>(R.id.animation_view) as ImageView

        val hd = Handler()
        val pref = getSharedPreferences("isFirst", MODE_PRIVATE)
        val first = pref.getBoolean("isFirst", false)
        if (!first) {
            val editor = pref.edit()
            editor.putBoolean("isFirst", true)
            editor.apply()
            // user 추가 시 테이블 오류 발생
            db.add_values(1, "게스트", 1, 0)
            c_db.first()
            hd.postDelayed(splash_handler_Login(), 500)
        } else {
            val pref2 = getSharedPreferences("isLogin", MODE_PRIVATE)
            val login = pref2.getBoolean("isLogin", false)
            if (!login) {
                //login Activity 실행
                hd.postDelayed(splash_handler_Login(), 500)
            } else {
                //MainActivity 실행
                hd.postDelayed(splash_handler_Main(), 500)
            }
        }
    }

    private inner class splash_handler_Login : Runnable {
        override fun run() {
            val i = Intent(applicationContext, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    private inner class splash_handler_Main : Runnable {
        override fun run() {
            val i = Intent(applicationContext, MainActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onBackPressed() {

    }
}
