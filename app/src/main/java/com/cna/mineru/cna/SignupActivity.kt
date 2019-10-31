package com.cna.mineru.cna

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText

import com.google.android.material.textfield.TextInputLayout
import com.cna.mineru.cna.Utils.CustomDialog
import com.cna.mineru.cna.Utils.LoadingDialog
import com.cna.mineru.cna.Utils.SecurityUtil
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import com.cna.mineru.cna.Utils.Network.getWhatKindOfNetwork

import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern

import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {

    private val mUser: FirebaseUser? = null
    private val googleSignInClient: GoogleSignInClient? = null

    private var btn_next: TextView? = null

    private var et_pw: AppCompatEditText? = null
    private var et_email: AppCompatEditText? = null
    private var et_pw_again: AppCompatEditText? = null

    private var layout_pw: TextInputLayout? = null
    private var layout_email: TextInputLayout? = null
    private var layout_pw_again: TextInputLayout? = null


    private var loadingDialog: LoadingDialog? = null

    private var isView: Boolean = false

    private// 이메일 공백
    // 이메일 형식 불일치
    val isValidEmail: Boolean
        get() = if (et_email!!.text!!.toString().isEmpty()) {
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(et_email!!.text!!).matches()) {
            false
        } else {
            true
        }

    private// 비밀번호 공백
    // 비밀번호 형식 불일치
    val isValidPasswd: Boolean
        get() = if (et_pw!!.text!!.toString().isEmpty()) {
            false
        } else if (!PASSWORD_PATTERN.matcher(et_pw!!.text!!).matches()) {
            false
        } else {
            true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        isView = false
        loadingDialog = LoadingDialog()
        val mAuth = FirebaseAuth.getInstance()

        iv_view.setOnClickListener {
            if (isView) {
                et_pw!!.inputType = 0x00000081
                isView = false
                iv_view.setImageResource(R.drawable.ic_view)
            } else {
                et_pw!!.inputType = 0x00000091
                isView = true
                iv_view.setImageResource(R.drawable.ic_hide)
            }
        }

        btn_back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left)
        }

        btn_next!!.setOnClickListener {
            btn_next!!.isEnabled = false
            val h = Handler()
            h.postDelayed(splashHandler(), 1000)
            val pw = et_pw!!.text!!.toString()
            val pw_again = et_pw_again!!.text!!.toString()
            if (pw == pw_again) {
                if (isValidEmail && isValidPasswd) {
                    createUser(et_email!!.text!!.toString(), et_pw!!.text!!.toString())
                }
            } else {
                val d = CustomDialog(8)
                d.show(supportFragmentManager, "insert error")
                d.setDialogResult(object : CustomDialog.OnMyDialogResult {
                    override fun finish(result: Int) {
                        d.dismiss()
                    }

                    override fun finish(result: Int, email: String) {

                    }
                })
            }
        }

        et_pw_again!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        if ("NONE" == getWhatKindOfNetwork(this).toString()) {
            val dialog2 = CustomDialog(4)
            dialog2.show(supportFragmentManager, "network error")
            dialog2.setDialogResult(object : CustomDialog.OnMyDialogResult {
                override fun finish(result: Int) {
                    if (result == 1) {
                        val intentConfirm = Intent()
                        intentConfirm.action = "android.settings.WIFI_SETTINGS"
                        startActivity(intentConfirm)
                    }
                }

                override fun finish(result: Int, email: String) {

                }
            })
        }
    }

    private fun createUser(email: String, password: String) {
        loadingDialog!!.progressON(this, "Loading...")
        JSONTask().execute(getString(R.string.ip_set) + "/api/user/create")
    }

    @Suppress("UNREACHABLE_CODE")
    @SuppressLint("StaticFieldLeak")
    inner class JSONTask : AsyncTask<String, String, String>() {
        @SuppressLint("WrongThread")
        override fun doInBackground(vararg urls: String): String? {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                val token = FirebaseInstanceId.getInstance().token
                val jsonObject = JSONObject()
                jsonObject.accumulate("name", "게스트")
                val securityUtil = SecurityUtil()
                val rtn1 = securityUtil.encryptSHA256(et_pw!!.text!!.toString())
                val pw = String(rtn1!!)
                jsonObject.accumulate("email", et_email!!.text!!.toString())
                jsonObject.accumulate("password", pw)
                jsonObject.accumulate("uuid", token)
                var con: HttpURLConnection? = null
                var reader: BufferedReader? = null
                val url = URL(urls[0])
                try {
                    //연결을 함
                    con = url.openConnection() as HttpURLConnection
                    con.requestMethod = "POST"//POST방식으로 보냄
                    con.setRequestProperty("Connection", "Keep-Alive")
                    con.setRequestProperty("Cache-Control", "no-cache")//캐시 설정
                    con.setRequestProperty(
                        "Content-Type",
                        "application/json"
                    )//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html")//서버에 response 데이터를 html로 받음
                    con.setRequestProperty("Accept-Charset", "UTF-8")
                    con.doOutput = true//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.doInput = true//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect()

                    val outStream = con.outputStream
                    val writer = BufferedWriter(OutputStreamWriter(outStream))
                    writer.write(jsonObject.toString())
                    writer.flush()
                    writer.close()

                    //서버로 부터 데이터를 받음
                    val stream = InputStreamReader(con.inputStream, "UTF-8")

                    reader = BufferedReader(stream)

                    val buffer = StringBuilder()

                    var line = ""

                    //(중요)서버로부터 한줄씩 읽어서 문자가 없을때까지 넣어줌
                    do{
                        line = reader.readLine()
                        buffer.append(line + "\n")
                        //읽어준 스트링값을 더해준다.
                    }while (true)
                    line = buffer.toString()
                    return line//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    con?.disconnect()
                    try {
                        reader?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            var error = 0
            var jObject: JSONObject? = null

            try {
                jObject = JSONObject(result)
                error = jObject.optInt("error")
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            if (error == 1) {
                loadingDialog!!.progressOFF()
                Toast.makeText(this@SignupActivity, "이미 등록 된 계정입니다.", Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog!!.progressOFF()
                Toast.makeText(this@SignupActivity, "인증 메일을 전송했습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left)
    }

    private inner class splashHandler : Runnable {
        override fun run() {
            btn_next!!.isEnabled = true // 클릭 유효화
        }
    }

    companion object {
        private val PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$")
    }
}

