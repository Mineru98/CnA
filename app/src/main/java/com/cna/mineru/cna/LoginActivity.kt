package com.cna.mineru.cna

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText

import com.cna.mineru.cna.DB.UserSQLClass
import com.cna.mineru.cna.Utils.CustomDialog
import com.cna.mineru.cna.Utils.LoadingDialog
import com.cna.mineru.cna.Utils.SecurityUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult

import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Objects
import java.util.regex.Pattern
import kotlinx.android.synthetic.main.activity_login.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LoginActivity : AppCompatActivity() {

    private var db: UserSQLClass? = null

    private var et_pw: AppCompatEditText? = null
    private var et_email: AppCompatEditText? = null

    private var layout_pw: TextInputLayout? = null
    private var layout_email: TextInputLayout? = null

    private var btn_signup: Button? = null
    private var btn_login_email: Button? = null

    private var loadingDialog: LoadingDialog? = null

    private var mAuth: FirebaseAuth? = null
    private var btn_login_google: SignInButton? = null
    private var googleSignInClient: GoogleSignInClient? = null

    private var isView: Boolean = false
    private var isVerified: Boolean = false

    private var token = ""
    private var name = "게스트"
    private var google_email: String? = ""
    private var google_name: String? = "게스트"

    private// 이메일 공백
    val isValidEmail: Boolean
        get() = if (et_email!!.text.toString().isEmpty()) {
            false
        } else EMAIL_PATTERN.matcher(et_email!!.text).matches()

    private// 비밀번호 공백
    // 비밀번호 형식 불일치
    val isValidPasswd: Boolean
        get() = if (et_pw!!.text.toString().isEmpty()) {
            false
        } else PASSWORD_PATTERN.matcher(et_pw!!.text).matches()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        db = UserSQLClass(this@LoginActivity)

        isVerified = false
        isView = false
        loadingDialog = LoadingDialog()
        mAuth = FirebaseAuth.getInstance()
        val content = SpannableString("비밀번호를 잃어버리셨나요?")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        tv_lose_pw.text = content

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(
            this@LoginActivity,
            OnSuccessListener<InstanceIdResult> { instanceIdResult ->
                token = instanceIdResult.token
            })

        iv_view.setOnClickListener {
            if (isView) {
                et_pw!!.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                isView = false
                iv_view.setImageResource(R.drawable.ic_view)
            } else {
                et_pw!!.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                isView = true
                iv_view.setImageResource(R.drawable.ic_hide)
            }
        }

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        //        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //        updateUI(account);

        tv_lose_pw.setOnClickListener {
            val dig = CustomDialog(3)
            dig.show(getSupportFragmentManager(), "lost pw")
            dig.setDialogResult(object : CustomDialog.OnMyDialogResult {
                override fun finish(result: Int) {

                }

                override fun finish(result: Int, email: String) {
                    if (result == 1) {
                        //loadingDialog.progressON(LoginActivity.this,"Loading...");
                        Toast.makeText(
                            this@LoginActivity,
                            "비밀번호 재설정을 위하여 " + email + "으로 이메일을 보냈습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(this@LoginActivity, "취소하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        btn_login_email!!.setOnClickListener {
            if (et_email!!.getText().toString().equals("") || et_pw!!.getText().toString().equals("")) {
                val h = Handler()
                h.postDelayed(splashHandler(), 2000)
                if (et_email!!.getText().toString().equals(""))
                    et_email!!.setTextColor(-0x2cd0d1)
                if (et_pw!!.getText().toString().equals(""))
                    et_pw!!.setTextColor(-0x2cd0d1)
            } else {
                btn_login_email!!.isEnabled = false
                val h = Handler()
                h.postDelayed(splashHandler(), 1000)
                loadingDialog!!.progressON(this@LoginActivity, "Loading...")
                if (isValidPasswd && isValidEmail) {
                    loginUser()
                }
            }
        }

        btn_login_google!!.setOnClickListener {
            btn_login_google!!.isEnabled = false
            val h = Handler()
            h.postDelayed(splashHandler(), 1000)
            val signInIntent = googleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        btn_signup!!.setOnClickListener {
            btn_signup!!.isEnabled = false
            val h = Handler()
            h.postDelayed(splashHandler(), 1000)
            val i = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
        }

        if ("NONE" == getWhatKindOfNetwork(this)) {
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

    private fun loginUser() {
        JSONTask().execute(getString(R.string.ip_set) + "/api/user/signin")
    }

    private inner class splashHandler : Runnable {
        override fun run() {
            btn_login_email!!.isEnabled = true // 클릭 유효화
            btn_login_google!!.isEnabled = true // 클릭 유효화
            btn_signup!!.isEnabled = true // 클릭 유효화
            et_pw!!.setTextColor(-0xafafb0)
            et_pw!!.setTextColor(-0xafafb0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 구글로그인 버튼 응답
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // 구글 로그인 성공
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (ignored: ApiException) {

            }

        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    // 로그인 성공
                    google_email =
                        Objects.requireNonNull<FirebaseUser>(FirebaseAuth.getInstance().currentUser)
                            .getEmail()
                    google_name = FirebaseAuth.getInstance().currentUser!!.displayName
                    GoogleJSONTask().execute(getString(R.string.ip_set) + "/api/user/google")
                } else {
                    // 로그인 실패
                    Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            })
    }

    @Suppress("DEPRECATED_IDENTITY_EQUALS", "UNREACHABLE_CODE")
    @SuppressLint("StaticFieldLeak")
    inner class JSONTask : AsyncTask<String, String, String>() {
        @SuppressLint("WrongThread")
        override fun doInBackground(vararg urls: String): String? {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                val jsonObject = JSONObject()
                jsonObject.accumulate("email", et_email!!.text.toString())
                val securityUtil = SecurityUtil()
                val rtn1 = securityUtil.encryptSHA256(et_pw!!.text.toString())
                val pw: String? = rtn1.toString()
                jsonObject.accumulate("password", pw)
                //                jsonObject.accumulate("password", et_pw.getText().toString());
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

                    val buffer = StringBuffer()

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
            var isGoogle = false
            var id = 1
            var error = 0
            var jObject: JSONObject? = null
            try {
                jObject = JSONObject(result)
                error = jObject.optInt("error")
                if (error == 2) {
                    isVerified = false
                } else {
                    isGoogle = jObject.optBoolean("isGoogle")
                    name = jObject.getString("name")
                    id = jObject.getInt("id")
                    isVerified = jObject.optBoolean("isVerified")
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            if (isVerified) {
                db!!.update_isGoogle(isGoogle, name, id)
                loadingDialog!!.progressOFF()
                val pref = getSharedPreferences("isLogin", MODE_PRIVATE)
                val editor = pref.edit()
                editor.putBoolean("isLogin", true)
                editor.apply()
                if (0 === db!!.first) {
                    startActivity(Intent(application, MainActivity::class.java))
                } else {
                    startActivity(Intent(application, MainActivity::class.java))
                    //                    startActivity(new Intent(getApplication(), DefaultInputActivity.class));
                }
                this@LoginActivity.finish()
                //overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            } else {
                loadingDialog!!.progressOFF()
                Toast.makeText(
                    this@LoginActivity,
                    "이메일 또는 비밀번호가 다시 확인하세요.\n" + "등록되지 않은 이메일이거나 이메일 또는 비밀번호를 잘못입력하셨습니다..",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @Suppress("UNREACHABLE_CODE")
    @SuppressLint("StaticFieldLeak")
    inner class GoogleJSONTask : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg urls: String): String? {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                val jsonObject = JSONObject()
                jsonObject.accumulate("email", google_email)
                jsonObject.accumulate("name", google_name)
                jsonObject.accumulate("isGoogle", true)
                jsonObject.accumulate("isVerified", true)
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
            var isGoogle = false
            var id = 1
            var jObject: JSONObject? = null
            try {
                jObject = JSONObject(result)
                isGoogle = jObject.optBoolean("isGoogle")
                name = jObject.getString("name")
                id = jObject.getInt("id")
                isVerified = jObject.optBoolean("isVerified")
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            if (isVerified) {
                db!!.update_isGoogle(isGoogle, name, id)
                loadingDialog!!.progressOFF()
                val pref = getSharedPreferences("isLogin", MODE_PRIVATE)
                val editor = pref.edit()
                editor.putBoolean("isLogin", true)
                editor.apply()
                if (db!!.first === 0) {
                    startActivity(Intent(getApplication(), MainActivity::class.java))
                } else {
                    startActivity(Intent(getApplication(), MainActivity::class.java))
                    //                    startActivity(new Intent(getApplication(), DefaultInputActivity.class));
                }
                this@LoginActivity.finish()
                //overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            } else {
                loadingDialog!!.progressOFF()
            }
        }
    }

    companion object {
        private val EMAIL_PATTERN =
            Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([a-z0-9-]+(\\.[a-z0-9-]+)*?\\.[a-z]{2,6}|(\\d{1,3}\\.){3}\\d{1,3})(:\\d{4})?$")
        private val PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")
        val WIFI_STATE = "WIFE"
        val MOBILE_STATE = "MOBILE"
        val NONE_STATE = "NONE"
        private val RC_SIGN_IN = 900

        fun getWhatKindOfNetwork(context: Context): String {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null) {
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                    return WIFI_STATE
                } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                    return MOBILE_STATE
                }
            }
            return NONE_STATE
        }
    }
}
