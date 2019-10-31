package com.cna.mineru.cna

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider

import com.cna.mineru.cna.DB.ExamSQLClass
import com.cna.mineru.cna.DB.GraphDataSQLClass
import com.cna.mineru.cna.DB.GraphSQLClass
import com.cna.mineru.cna.DB.HomeSQLClass
import com.cna.mineru.cna.DB.ImageSQLClass
import com.cna.mineru.cna.DB.UserSQLClass
import com.cna.mineru.cna.DTO.ExamData
import com.cna.mineru.cna.Utils.ClassListDialog
import com.cna.mineru.cna.Utils.CustomDialog
import com.cna.mineru.cna.Utils.LoadingDialog
import com.cna.mineru.cna.Utils.MyValueFormatter
import com.cna.mineru.cna.Utils.PhotoDialog2
import com.cna.mineru.cna.Utils.XAxisValueFormatter2
import com.github.chrisbanes.photoview.PhotoView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.model.GradientColor

import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

import com.cna.mineru.cna.Utils.Network.getWhatKindOfNetwork.getWhatKindOfNetwork
import com.github.mikephil.charting.utils.ColorTemplate.rgb
import kotlinx.android.synthetic.main.activity_modifiy_item.*
import kotlinx.android.synthetic.main.include_toolbar_note.*

class ModifyHomeItem : AppCompatActivity(), OnChartValueSelectedListener {

    private var db: HomeSQLClass? = null
    private var gp_db: GraphSQLClass? = null
    private var d_db: GraphDataSQLClass? = null
    private var u_db: UserSQLClass? = null
    private var e_db: ExamSQLClass? = null

    private var et_title: EditText? = null

    private var imageView: PhotoView? = null
    private var imageView2: PhotoView? = null

    private var btn_class: Button? = null
    private var bChart: BarChart? = null

    private var Tag: Int = 0
    private var id: Int = 0
    private var note_id = 0
    private var isLeft: Boolean = false
    private var Subtag: Int = 0

    private var bm: Bitmap? = null
    private var bm2: Bitmap? = null

    private var loadingDialog: LoadingDialog? = null

    private val getServerURL: String? = null
    private var getImgURL: String? = ""

    private val image: ByteArray? = null
    private val image2: ByteArray? = null

    private var tempFile: File? = null
    private var list: ArrayList<ExamData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifiy_item)
        setSupportActionBar(toolbar as Toolbar?)

        db = HomeSQLClass(this)
        gp_db = GraphSQLClass(this)
        d_db = GraphDataSQLClass(this)
        u_db = UserSQLClass(this)
        e_db = ExamSQLClass(this)
        val img_db = ImageSQLClass(this)

        loadingDialog = LoadingDialog()
        note_id = db!!.id

        val intent = intent
        id = intent.extras!!.getInt("id")
        val title = intent.extras!!.getString("title")
        Tag = intent.extras!!.getInt("tag")
        Subtag = intent.extras!!.getInt("subtag")
        btn_class!!.text = d_db!!.getData(Tag, Subtag, u_db!!.classId)

        btn_class!!.setOnClickListener {
            val d = ClassListDialog()
            d.show(supportFragmentManager, "select exam num")
            d.setDialogResult(object : ClassListDialog.OnMyDialogResult {
                override fun finish(Tag: Int, SubTag: Int, result: String) {
                    btn_class!!.text = result
                    this@ModifyHomeItem.Tag = Tag
                    this@ModifyHomeItem.Subtag = SubTag
                }
            })
        }

        run {
            bChart = findViewById(R.id.chartView)
            bChart!!.setOnChartValueSelectedListener(this)
            bChart!!.setDrawBarShadow(false)
            bChart!!.setDrawValueAboveBar(true)
            bChart!!.description.isEnabled = false
            bChart!!.animateY(750)
            bChart!!.animateX(750)
            bChart!!.setMaxVisibleValueCount(60)
            bChart!!.setScaleEnabled(false)
            bChart!!.isDragEnabled = true
            bChart!!.setDrawGridBackground(false)
        }

        run {
            val xAxisFormatter = bChart?.let { XAxisValueFormatter2(it) }
            val xAxis = bChart!!.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            xAxis.labelCount = 7
            xAxis.valueFormatter = xAxisFormatter

            val custom = MyValueFormatter("분")

            val leftAxis = bChart!!.axisLeft
            leftAxis.setLabelCount(4, false)
            leftAxis.valueFormatter = custom
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            leftAxis.spaceTop = 15f
            leftAxis.axisMinimum = 0f

            val rightAxis = bChart!!.axisRight
            rightAxis.setDrawGridLines(false)
            rightAxis.setLabelCount(4, false)
            rightAxis.valueFormatter = custom
            rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            rightAxis.spaceTop = 15f
            rightAxis.axisMinimum = 0f

            val l = bChart!!.legend
            l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            l.orientation = Legend.LegendOrientation.HORIZONTAL
            l.setDrawInside(false)
            l.form = Legend.LegendForm.CIRCLE
            l.formSize = 12f
            l.textSize = 12f

        }
        val legend = bChart!!.legend
        legend.isEnabled = false
        list = e_db!!.getEachExam(id)
        b_setData(list!!)

        var count: Int
        for (solve in 0..1) {
            count = img_db.getCount(id, solve)
            val image_merge = ArrayList<ArrayList<Byte>>()
            var image_t: ArrayList<Byte>
            val image_arr = img_db.getImg(id, solve)

            var size = 0
            for (i in 0 until count)
                size += image_arr.get(i).size

            val result = ByteArray(size)

            val tmp = ArrayList<Byte>()
            var buf: ByteBuffer

            for (i in image_arr.indices) {
                buf = ByteBuffer.wrap(image_arr.get(i))
                for (j in 0 until image_arr.get(i).size) {
                    tmp.add(buf.get(j))
                }
                buf.clear()
                image_merge.add(tmp)
            }
            count = 0
            for (i in image_arr.indices) {
                image_t = image_merge[i]
                if (i == image_arr.size - 1) {
                    for (j in i * 1048349 until image_arr.get(i).size + i * 1048349) {
                        result[count] = image_t[j]
                        count++
                    }
                } else {
                    for (j in i * 1048349 until 1048349 + i * 1048349) {
                        result[count] = image_t[j]
                        count++
                    }
                }
            }
            if (solve == 0) {
                bm = BitmapFactory.decodeByteArray(result, 0, result.size)
                saveBitmaptoJpeg(bm, "1")
                @SuppressLint("SdCardPath")
                val photo = File("/sdcard/CnA/1.jpg")
                val imageUri = Uri.fromFile(photo)
                val imagePath = imageUri.path
                var exif: ExifInterface? = null
                try {
                    exif = ExifInterface(imagePath!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val exifOrientation = exif!!.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
                )
                val exifDegree = exifOrientationToDegrees(exifOrientation)
                bm = rotate(bm, exifDegree)
                imageView!!.setImageBitmap(bm)
                photo.delete()
            } else {
                bm2 = BitmapFactory.decodeByteArray(result, 0, result.size)
                saveBitmaptoJpeg(bm2, "2")
                @SuppressLint("SdCardPath")
                val photo = File("/sdcard/CnA/2.jpg")
                val imageUri = Uri.fromFile(photo)
                val imagePath = imageUri.path
                var exif: ExifInterface? = null
                try {
                    exif = ExifInterface(imagePath!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val exifOrientation = exif!!.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
                )
                val exifDegree = exifOrientationToDegrees(exifOrientation)
                bm2 = rotate(bm2, exifDegree)
                imageView2!!.setImageBitmap(bm2)
                photo.delete()
            }
        }

        et_title!!.setText(title)

        btn_cancel.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left)
        }

        btn_save.setOnClickListener {
            if (intent.getExtras()!!.getBoolean("isCalledHome")) {
                //                    tag = Integer.parseInt(btn_class.getText().toString());
                db!!.update_item(id, et_title!!.text.toString(), Tag, Subtag)
                gp_db!!.update_value(id, Tag)
                //                    new JSONNote().execute(getString(R.string.ip_set)+"/api/note/update");
            }
            finish()
        }

        imageView!!.setOnClickListener {
            val mBuilder = AlertDialog.Builder(this@ModifyHomeItem)
            val mView = layoutInflater.inflate(R.layout.dialog_image_layout, null)
            val photoView = mView.findViewById<PhotoView>(R.id.imageView)
            photoView.setImageBitmap(bm)
            mBuilder.setView(mView)
            val mDialog = mBuilder.create()
            mDialog.show()
            mView.setOnClickListener(View.OnClickListener { mDialog.cancel() })
        }

        imageView!!.setOnLongClickListener { v ->
            val myCalendarView = PhotoDialog2()
            myCalendarView.show(supportFragmentManager, "photo_dialog2")
            myCalendarView.setDialogResult(object : PhotoDialog2.OnMyDialogResult {
                override fun finish(result: Int) {
                    isLeft = true
                    if (result == 1) {
                        takePhoto()
                    } else if (result == 2) {
                        goToAlbum()
                    } else if (result == 3) {
                        Toast.makeText(v.context, "기능 준비중...", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            false
        }

        imageView2!!.setOnClickListener {
            val mBuilder = AlertDialog.Builder(this@ModifyHomeItem)
            val mView = getLayoutInflater().inflate(R.layout.dialog_image_layout, null)
            imageView!!.setImageBitmap(bm2)
            mBuilder.setView(mView)
            val mDialog = mBuilder.create()
            mDialog.show()
            mView.setOnClickListener(View.OnClickListener { mDialog.cancel() })
        }

        imageView2!!.setOnLongClickListener { v ->
            val myCalendarView = PhotoDialog2()
            myCalendarView.show(getSupportFragmentManager(), "photo_dialog2")
            myCalendarView.setDialogResult(object : PhotoDialog2.OnMyDialogResult {
                override fun finish(result: Int) {
                    isLeft = false
                    if (result == 1) {
                        takePhoto()
                    } else if (result == 2) {
                        goToAlbum()
                    } else if (result == 3) {
                        Toast.makeText(v.context, "기능 준비중...", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            false
        }

        if ("NONE" == getWhatKindOfNetwork(this)) {
            val dialog2 = CustomDialog(4)
            dialog2.show(getSupportFragmentManager(), "network error")
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

    private fun b_setData(list: ArrayList<ExamData>) {
        val values = ArrayList<BarEntry>()

        for (i in list.indices)
            values.add(BarEntry((i + 1) * 1f, list[i].TTS / 1000f))

        val set1: BarDataSet

        if (bChart!!.data != null && bChart!!.data.dataSetCount > 0) {
            set1 = bChart!!.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            bChart!!.data.notifyDataChanged()
            bChart!!.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(values, "정답")
            set1.setDrawIcons(false)
            set1.setColors(rgb("#ff33b5e5"), rgb("#ffff4444"))

            val startColor1 = ContextCompat.getColor(this, R.color.main_color)
            val startColor2 = ContextCompat.getColor(this, R.color.second_color)

            val gradientColors = ArrayList<GradientColor>()
            for (i in list.indices) {
                if (list[i].isSolved === 1) {
                    gradientColors.add(GradientColor(startColor1, startColor1))
                } else {
                    gradientColors.add(GradientColor(startColor2, startColor2))
                }
            }

            set1.gradientColors = gradientColors

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.barWidth = 0.9f

            bChart!!.data = data
        }
    }

    override fun onValueSelected(e: Entry?, h: Highlight) {
        if (e == null)
            return
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {

        val chartView = this.findViewById(R.id.chartView) as BarChart
        val layout = RelativeLayout.LayoutParams(chartView.width, (chartView.width * 1.5).toInt())
        bChart!!.layoutParams = layout
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {

    }

    override fun onNothingSelected() {

    }

    @SuppressLint("IntentReset")
    private fun goToAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, PICK_FROM_ALBUM)
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            tempFile = createImageFile()
        } catch (e: IOException) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            finish()
            e.printStackTrace()
        }

        if (tempFile != null) {
            val photoUri =
                FileProvider.getUriForFile(this, "com.cna.mineru.cna.ModifyHomeItem",
                    tempFile!!
                )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(intent, PICK_FROM_CAMERA)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        @SuppressLint("SimpleDateFormat")
        val timeStamp = SimpleDateFormat("yyyyMMdd_kkmmss").format(Date())
        val imageFileName = "CnA_$timeStamp"
        val storageDir = File("sdcard/CnA/Img")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    fun getPath(uri: Uri?): String? {
        // uri가 null일경우 null반환
        if (uri == null) {
            return null
        }
        // 미디어스토어에서 유저가 선택한 사진의 URI를 받아온다.
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        if (cursor != null) {
            val column_index = cursor!!
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor!!.moveToFirst()
            return cursor!!.getString(column_index)
        }
        // URI경로를 반환한다.
        return uri.path
    }

    fun exifOrientationToDegrees(exifOrientation: Int): Int {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270
        }
        return 0
    }

    fun rotate(bitmap: Bitmap?, degrees: Int): Bitmap? {
        var bitmap = bitmap
        if (degrees != 0 && bitmap != null) {
            val m = Matrix()
            m.setRotate(
                degrees.toFloat(), bitmap.width.toFloat() / 2,
                bitmap.height.toFloat() / 2
            )

            try {
                val converted = Bitmap.createBitmap(
                    bitmap, 0, 0,
                    bitmap.width, bitmap.height, m, true
                )
                if (bitmap != converted) {
                    bitmap.recycle()
                    bitmap = converted
                }
            } catch (ex: OutOfMemoryError) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }

        }
        return bitmap
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    loadingDialog!!.progressON(this@ModifyHomeItem, "Loading...")
                    val selectedImageUri = data?.data
                    if (isLeft) {
                        try {
                            // 비트맵 이미지로 가져온다
                            val imagePath = getPath(selectedImageUri)
                            getImgURL = imagePath
                            var image: Bitmap? = BitmapFactory.decodeFile(imagePath)
                            val exif = ExifInterface(imagePath!!)
                            val exifOrientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
                            )
                            val exifDegree = exifOrientationToDegrees(exifOrientation)
                            image = rotate(image, exifDegree)
                            imageView!!.setImageBitmap(image)
                        } catch (e: Exception) {
                            Toast.makeText(this, "오류발생: " + e.localizedMessage!!, Toast.LENGTH_LONG)
                                .show()
                        }

                    } else {
                        try {
                            // 비트맵 이미지로 가져온다
                            val imagePath = getPath(selectedImageUri)
                            getImgURL = imagePath
                            var image: Bitmap? = BitmapFactory.decodeFile(imagePath)
                            val exif = ExifInterface(imagePath!!)
                            val exifOrientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
                            )
                            val exifDegree = exifOrientationToDegrees(exifOrientation)
                            image = rotate(image, exifDegree)
                            imageView2!!.setImageBitmap(image)
                        } catch (e: Exception) {
                            Toast.makeText(this, "오류발생: " + e.localizedMessage!!, Toast.LENGTH_LONG)
                                .show()
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        if (isLeft) {
            if ("NONE" == getWhatKindOfNetwork(this)) {
                loadingDialog!!.progressOFF()
                val dialog2 = CustomDialog(4)
                dialog2.show(getSupportFragmentManager(), "network error")
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
            } else {
                //Upload
                //uploadFile(getImgURL);
            }
        } else {
            if ("NONE" == getWhatKindOfNetwork(this)) {
                loadingDialog!!.progressOFF()
                val dialog2 = CustomDialog(4)
                dialog2.show(getSupportFragmentManager(), "network error")
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
            } else {
                //Upload
                //uploadFile(getImgURL);
            }
        }
    }

    @Suppress("UNREACHABLE_CODE")
    @SuppressLint("StaticFieldLeak")
    inner class JSONNote : AsyncTask<String, String, String>() {
        @SuppressLint("WrongThread")
        override fun doInBackground(vararg urls: String): String? {
            try {
                val jsonObject = JSONObject()
                jsonObject.accumulate("id", note_id)
                jsonObject.accumulate("note_type", Tag)
                jsonObject.accumulate("title", et_title!!.text.toString())
                var con: HttpURLConnection? = null
                var reader: BufferedReader? = null
                val url = URL(urls[0])
                try {
                    con = url.openConnection() as HttpURLConnection
                    con.requestMethod = "PUT"//PUT방식으로 보냄
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
                    jObject.getBoolean("Success")
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left)
    }

    companion object {
        private val PICK_FROM_ALBUM = 1
        private val PICK_FROM_CAMERA = 2

        fun saveBitmaptoJpeg(bitmap: Bitmap?, name: String) {
            val file_name = "$name.jpg"
            @SuppressLint("SdCardPath")
            val string_path = "/sdcard/CnA/"

            @SuppressLint("SdCardPath")
            val file_path = File("/sdcard/CnA")
            try {
                if (!file_path.isDirectory) {
                    file_path.mkdir()
                }
                val out = FileOutputStream(string_path + file_name)
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.close()
            } catch (exception: FileNotFoundException) {
                Log.e("FileNotFoundException", exception.message)
            } catch (exception: IOException) {
                Log.e("IOException", exception.message)
            }

        }
    }
}
