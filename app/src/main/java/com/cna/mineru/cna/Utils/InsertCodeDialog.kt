package com.cna.mineru.cna.Utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment

import com.cna.mineru.cna.R

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
import java.util.Objects

@Suppress("UNREACHABLE_CODE")
class InsertCodeDialog : DialogFragment() {
    private var mDialogResult: OnMyDialogResult? = null
    private var loadingDialog: LoadingDialog? = null

    private var et_code: EditText? = null
    private var btn_ok: TextView? = null
    private var btn_cancel: TextView? = null

    private var coupon_code = ""
    @SuppressLint("InflateParams")
    override fun onCreateDialog(saveInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = Objects.requireNonNull(activity)?.layoutInflater
        val dig = inflater?.inflate(R.layout.insertcode_dialog, null)
        val tv_title = dig?.findViewById(R.id.tv_title) as TextView
        et_code = dig.findViewById(R.id.et_code)
        btn_ok = dig.findViewById(R.id.btn_ok)
        btn_cancel = dig.findViewById(R.id.btn_cancel)

        loadingDialog = LoadingDialog()

        btn_ok!!.setOnClickListener {
            if (et_code!!.text.toString() == "") {
                val h = Handler()
                h.postDelayed(splashHandler(), 2000)
                if (et_code!!.text.toString() == "")
                    et_code!!.setHintTextColor(-0x2cd0d1)
            } else {
                loadingDialog!!.progressON(activity, "Loading...")
                coupon_code = et_code!!.text.toString()
                JSONCode().execute(getString(R.string.ip_set) + "/api/coupon/check")
            }
        }

        btn_cancel!!.setOnClickListener { this@InsertCodeDialog.dismiss() }

        builder.setView(dig)
        return builder.create()
    }

    fun setDialogResult(dialogResult: OnMyDialogResult) {
        mDialogResult = dialogResult
    }

    interface OnMyDialogResult {
        fun finish(_class: Int, code: String, tag: Int)
    }

    @SuppressLint("StaticFieldLeak")
    inner class JSONCode : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg urls: String): String? {
            try {
                val jsonObject = JSONObject()
                jsonObject.accumulate("coupon_code", coupon_code)
                var con: HttpURLConnection? = null
                var reader: BufferedReader? = null
                val url = URL(urls[0])
                try {
                    con = url.openConnection() as HttpURLConnection
                    con.requestMethod = "POST"//POST 방식으로 보냄
                    con.setRequestProperty("Connection", "Keep-Alive")
                    con.setRequestProperty("Cache-Control", "no-cache")//캐시 설정
                    con.setRequestProperty(
                        "Content-Type",
                        "application/json"
                    )//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html")//서버에 response 데이터를 html로 받음
                    con.setRequestProperty("Accept-Charset", "UTF-8")
                    con.doOutput = true//Outstream으로 PUT 데이터를 넘겨주겠다는 의미
                    con.doInput = true//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect()

                    val outStream = con.outputStream
                    val writer = BufferedWriter(OutputStreamWriter(outStream))
                    writer.write(jsonObject.toString())
                    writer.flush()
                    writer.close()

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
            var error = 0
            var jObject: JSONObject? = null
            try {
                jObject = JSONObject(result)
                error = jObject.optInt("error")
                if (error == 2) {

                } else {
                    if (jObject.getInt("Result") == 1) {
                        this@InsertCodeDialog.dismiss()
                        val tag = jObject.getInt("coupon_tag")
                        mDialogResult!!.finish(1, coupon_code, tag)
                    } else {
                        mDialogResult!!.finish(0, "", 0)
                    }
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            loadingDialog!!.progressOFF()
        }
    }

    private inner class splashHandler : Runnable {
        override fun run() {
            btn_cancel!!.isEnabled = true // 클릭 유효화
            btn_ok!!.isEnabled = true // 클릭 유효화
            et_code!!.setHintTextColor(-0x242425)
        }
    }
}
