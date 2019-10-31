package com.cna.mineru.cna.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.cna.mineru.cna.Adapter.GridAdapter
import com.cna.mineru.cna.AddNote
import com.cna.mineru.cna.DB.GraphSQLClass
import com.cna.mineru.cna.DB.HomeSQLClass
import com.cna.mineru.cna.DB.ImageSQLClass
import com.cna.mineru.cna.DB.UserSQLClass
import com.cna.mineru.cna.DTO.HomeData
import com.cna.mineru.cna.ModifyHomeItem
import com.cna.mineru.cna.R
import com.cna.mineru.cna.Utils.CustomDialog
import com.cna.mineru.cna.Utils.DefaultInputDialog
import com.cna.mineru.cna.Utils.LoadingDialog

import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

import kotlinx.android.synthetic.main.fragment_home.*
class HomeFragment : Fragment() {

    private var db: HomeSQLClass? = null
    private var gp_db: GraphSQLClass? = null
    private var i_db: ImageSQLClass? = null
    private var u_db: UserSQLClass? = null

    private var gv: GridView? = null
    private var mAdapater: GridAdapter? = null
    private var fb: FloatingActionButton? = null
    private var loadingDialog: LoadingDialog? = null

    private var note_id = 0

    private var list = ArrayList<HomeData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater?.inflate(R.layout.fragment_home, container, false)
        fb = view?.findViewById(R.id.fb_add) as FloatingActionButton
        db = HomeSQLClass(activity)
        u_db = UserSQLClass(activity)
        gp_db = GraphSQLClass(activity)
        i_db = ImageSQLClass(activity)
        loadingDialog = LoadingDialog()
        mAdapater = GridAdapter(context, R.layout.home_item, list)

        list = db!!.load_values()

        gv = view.findViewById(R.id.gridView1)
        gv!!.adapter = mAdapater

        fb!!.setOnClickListener {
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
                list.clear()
                fb!!.isEnabled = false
                val h = Handler()
                h.postDelayed(splashHandler(), 1000)
                val i = Intent(activity, AddNote::class.java)
                startActivity(i)
            }
        }

        gv!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            loadingDialog!!.progressON(activity, "Loading...")
            gv!!.isEnabled = false
            val h = Handler()
            h.postDelayed(splashHandler(), 1000)

            val data: HomeData
            data = db!!.select_item(list[position].id)
            val i = Intent(getActivity(), ModifyHomeItem::class.java)
            i.putExtra("id", data.id)
            i.putExtra("title", data.title_text)
            i.putExtra("tag", data.Tag)
            i.putExtra("subtag", data.Subtag)
            i.putExtra("isCalledHome", true)
            startActivity(i)
            activity?.overridePendingTransition(
                R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_right
            )
        }

        gv!!.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { parent, view, position, id ->
                try {
                    val d = CustomDialog(6)
                    activity?.supportFragmentManager?.let { d.show(it, "alter") }
                    d.setDialogResult(object : CustomDialog.OnMyDialogResult {
                        override fun finish(_class: Int) {
                            if (_class == 1) {
                                db!!.delete_item(list[position].id)
                                gp_db!!.delete_value(list[position].id)
                                i_db!!.delete_item(list[position].id)
                                note_id = list[position].id
                                //                                new DelNote().execute(getString(R.string.ip_set)+"/api/note/destroy");
                                onResume()
                            } else
                                d.dismiss()
                        }

                        override fun finish(result: Int, email: String) {

                        }
                    })
                } catch (ignored: Exception) {
                }

                true
            }
        return view
    }

    @Suppress("UNREACHABLE_CODE")
    @SuppressLint("StaticFieldLeak")
    inner class DelNote : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg urls: String): String? {
            try {
                val jsonObject = JSONObject()
                jsonObject.accumulate("id", note_id)
                var con: HttpURLConnection? = null
                var reader: BufferedReader? = null
                val url = URL(urls[0])
                try {
                    con = url.openConnection() as HttpURLConnection
                    con.requestMethod = "DELETE"//DELETE 방식으로 보냄
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

                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            loadingDialog!!.progressOFF()
        }
    }

    private inner class splashHandler : Runnable {
        override fun run() {
            fb!!.setEnabled(true) // 클릭 유효화
            gv!!.isEnabled = true

        }
    }

    override fun onResume() {
        super.onResume()
        list = db!!.load_values()
        mAdapater = GridAdapter(getContext(), R.layout.home_item, list)
        gv!!.adapter = mAdapater
    }

    override fun onStop() {
        super.onStop()
        loadingDialog!!.progressOFF()
    }
}
