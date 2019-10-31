package com.cna.mineru.cna.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

import com.cna.mineru.cna.DB.ExamSQLClass
import com.cna.mineru.cna.DB.GraphSQLClass
import com.cna.mineru.cna.DB.HomeSQLClass
import com.cna.mineru.cna.DB.ImageSQLClass
import com.cna.mineru.cna.DB.TmpSQLClass
import com.cna.mineru.cna.DB.UserSQLClass
import com.cna.mineru.cna.LoginActivity
import com.cna.mineru.cna.R
import com.cna.mineru.cna.Utils.CustomDialog
import com.cna.mineru.cna.Utils.InsertCodeDialog
import com.cna.mineru.cna.Utils.LoadingDialog
import com.cna.mineru.cna.Utils.WebViewActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import org.json.JSONObject

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

import android.app.Activity.RESULT_OK
import android.content.Context.MODE_PRIVATE
import androidx.fragment.app.Fragment


class ProfileFragment : Fragment() {

    private var mFirebaseUser: FirebaseUser? = null

    private var db: UserSQLClass? = null

    private var loadingDialog: LoadingDialog? = null

    private var user_id: Int = 0
    private var isLogin: Boolean = false

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        db = UserSQLClass(getContext()!!)
        isLogin = true
        loadingDialog = LoadingDialog()
        val tv_ver = view.findViewById(R.id.tv_ver) as TextView
        val tv_str_ver = view.findViewById(R.id.tv_str_ver) as TextView

        var packageInfo: PackageInfo? = null
        try {
            packageInfo =getContext()!!.getPackageManager().getPackageInfo(getContext()!!.getPackageName(), 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        tv_str_ver.text = "현재 버전: " + packageInfo!!.versionName
        var content = SpannableString("Application")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        tv_ver.text = content

        val tv_abouts = view.findViewById(R.id.tv_abouts) as TextView
        val tv_terms = view.findViewById(R.id.tv_terms) as TextView
        val tv_privacy = view.findViewById(R.id.tv_privacy) as TextView
        content = SpannableString("Abouts")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        tv_abouts.text = content

        val tv_account = view.findViewById(R.id.tv_account) as TextView
        val tv_profile = view.findViewById(R.id.tv_profile) as TextView
        val tv_insert_code = view.findViewById(R.id.tv_insert_code) as TextView
        val tv_logout = view.findViewById(R.id.tv_logout) as TextView
        val tv_signout = view.findViewById(R.id.tv_signout) as TextView
        content = SpannableString("Account")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        tv_account.text = content

        val tv_setting = view.findViewById(R.id.tv_setting) as TextView
        val tv_wifisync = view.findViewById(R.id.tv_wifisync) as TextView
        val sw_wifisync = view.findViewById(R.id.sw_wifisync) as Switch
        content = SpannableString("Setting")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        tv_setting.text = content

        sw_wifisync.isChecked = !db!!.wifiSync

        tv_terms.setOnClickListener {
            val i = Intent(getContext(), WebViewActivity::class.java)
            i.putExtra("value", "terms")
            startActivity(i)
        }

        tv_privacy.setOnClickListener {
            val i = Intent(getContext(), WebViewActivity::class.java)
            i.putExtra("value", "privacy")
            startActivity(i)
        }

        tv_profile.setOnClickListener {
            //프로필 설정 Activity
            val d = CustomDialog(16)
            d.show(getActivity()!!.getSupportFragmentManager(), "chang name")
            d.setDialogResult(object : CustomDialog.OnMyDialogResult {
                override fun finish(result: Int) {

                }

                override fun finish(result: Int, email: String) {
                    if (result == 1) {
                        db!!.update_name(email)
                        //server에도 변경 코드 작성 필요
                    }
                }
            })
        }

        tv_insert_code.setOnClickListener {
            if (db!!.isCoupon) {
                val d = CustomDialog(7)
                activity?.getSupportFragmentManager()?.let { it1 -> d.show(it1, "add coupon") }
                d.setDialogResult(object : CustomDialog.OnMyDialogResult {
                    override fun finish(_class: Int) {
                        if (_class == 1) {
                            d.dismiss()
                            val dialog = InsertCodeDialog()
                            activity?.supportFragmentManager?.let { it1 -> dialog.show(it1, "insert code") }
                            dialog.setDialogResult(object : InsertCodeDialog.OnMyDialogResult {
                                override fun finish(_class: Int, code: String, tag: Int) {
                                    if (_class == 1) {
                                        Toast.makeText(getContext(), "인증되었습니다.", Toast.LENGTH_SHORT)
                                            .show()
                                        db!!.update_isPremium(1)
                                        db!!.addCoupon(tag)
                                    } else {
                                        Toast.makeText(
                                            getContext(),
                                            "코드가 일치하지 않습니다..",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            })
                        } else
                            d.dismiss()
                    }

                    override fun finish(result: Int, email: String) {

                    }
                })
            } else {
                val dialog = InsertCodeDialog()
                activity?.supportFragmentManager?.let { it1 -> dialog.show(it1, "insert code") }
                dialog.setDialogResult(object : InsertCodeDialog.OnMyDialogResult {
                    override fun finish(_class: Int, code: String, tag: Int) {
                        if (_class == 1) {
                            Toast.makeText(getContext(), "인증되었습니다.", Toast.LENGTH_SHORT).show()
                            db!!.update_isPremium(1)
                            db!!.setCouponCode(code)
                        } else {
                            Toast.makeText(getContext(), "코드가 일치하지 않습니다..", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                })
            }
        }

        tv_logout.setOnClickListener {
            val dialog2 = CustomDialog(1)
            activity?.getSupportFragmentManager()?.let { it1 -> dialog2.show(it1, "logout code") }
            dialog2.setDialogResult(object : CustomDialog.OnMyDialogResult {
                override fun finish(result: Int) {
                    if (result == 1) {
                        loadingDialog!!.progressON(activity, "Loading...")
                        isLogin = false
                        val pref = context?.getSharedPreferences("isLogin", MODE_PRIVATE)
                        val editor = pref?.edit()
                        editor?.putBoolean("isLogin", false)
                        editor?.apply()
                        reset_app()
                        FirebaseAuth.getInstance().signOut()
                        val resultIntent = Intent(getActivity(), LoginActivity::class.java)
                        resultIntent.putExtra("isLogin", isLogin)
                        activity?.setResult(RESULT_OK, resultIntent)
                        activity?.finish()
                        startActivity(resultIntent)
                    }
                }

                override fun finish(result: Int, email: String) {

                }
            })
        }

        tv_signout.setOnClickListener {
            val dialog2 = CustomDialog(2)
            activity?.getSupportFragmentManager()?.let { it1 -> dialog2.show(it1, "logout code") }
            dialog2.setDialogResult(object : CustomDialog.OnMyDialogResult {
                override fun finish(result: Int) {
                    if (result == 1) {
                        loadingDialog!!.progressON(activity, "Loading...")
                        isLogin = false
                        val pref = context?.getSharedPreferences("isLogin", MODE_PRIVATE)
                        val editor = pref?.edit()
                        editor?.putBoolean("isLogin", false)
                        editor?.apply()
                        reset_app()
                        FirebaseAuth.getInstance().signOut()
                        val isGoogle: Boolean
                        isGoogle = db!!.delete_User()
                        if (isGoogle) {
                            mFirebaseUser!!.delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                    }
                                }
                        } else {
                            JSONTask().execute(getString(R.string.ip_set) + "/api/user/delete")
                        }

                        val resultIntent = Intent(getActivity(), LoginActivity::class.java)
                        resultIntent.putExtra("isLogin", isLogin)
                        activity?.setResult(RESULT_OK, resultIntent)
                        activity?.finish()
                        startActivity(resultIntent)
                    }
                }

                override fun finish(result: Int, email: String) {

                }
            })
        }

        sw_wifisync.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                db!!.update_isWifiSync(0)
            } else {
                db!!.update_isWifiSync(1)
            }
        }
        return view
    }

    @Suppress("UNREACHABLE_CODE")
    @SuppressLint("StaticFieldLeak")
    inner class JSONTask : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg urls: String): String? {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                user_id = db!!.userId
                val jsonObject = JSONObject()

                var con: HttpURLConnection? = null
                var reader: BufferedReader? = null
                val url = URL(urls[0])
                try {
                    //연결을 함
                    con = url.openConnection() as HttpURLConnection
                    con.requestMethod = "DELETE"//DELETE방식으로 보냄
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
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
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
            loadingDialog!!.progressOFF()
        }
    }

    private fun reset_app() {
        val db1 = TmpSQLClass(getContext())
        val db2 = UserSQLClass(getContext())
        val db3 = ExamSQLClass(getContext())
        val db4 = HomeSQLClass(getContext())
        val db5 = GraphSQLClass(getContext())
        val db6 = ImageSQLClass(getContext())

        db1.reset_app()
        db2.reset_app()
        db3.reset_app()
        db4.reset_app()
        db5.reset_app()
        db6.reset_app()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog!!.progressOFF()
    }
}
