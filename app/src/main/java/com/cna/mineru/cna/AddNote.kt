package com.cna.mineru.cna

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.cna.mineru.cna.DB.GraphSQLClass
import com.cna.mineru.cna.DB.HomeSQLClass
import com.cna.mineru.cna.DB.ImageSQLClass
import com.cna.mineru.cna.DB.UserSQLClass
import com.cna.mineru.cna.Utils.ClassListDialog
import com.cna.mineru.cna.Utils.CustomDialog
import com.cna.mineru.cna.Utils.LoadingDialog
import com.cna.mineru.cna.Utils.PhotoDialog

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import kotlinx.android.synthetic.main.activity_note.*

class AddNote : AppCompatActivity() {

    private var db: HomeSQLClass? = null
    private var img_db: ImageSQLClass? = null
    private var gp_db: GraphSQLClass? = null
    private var user_db: UserSQLClass? = null

    private var tv_1: TextView? = null
    private var tv_1_2: TextView? = null

    private var tv_2: TextView? = null
    private var tv_2_2: TextView? = null

    private var btn_ok: TextView? = null
    private var btn_class: Button? = null

    private var et_title: EditText? = null

    private var imageView: ImageView? = null
    private var imageView2: ImageView? = null
    private var btn_cancel: ImageView? = null

    private var loadingDialog: LoadingDialog? = null

    private var divide_t: Int = 0

    private var isLeft: Boolean = false

    private var image: ByteArray? = null
    private var image2: ByteArray? = null
    private var Tag: Int = 0
    private var SubTag: Int = 0
    private var mCurrentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        val mToolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)

        db = HomeSQLClass(this)
        gp_db = GraphSQLClass(this)
        img_db = ImageSQLClass(this)
        user_db = UserSQLClass(this)
        loadingDialog = LoadingDialog()

        isLeft = true

        set_image.setOnClickListener {
            val myCalendarView = PhotoDialog()
            myCalendarView.show(getSupportFragmentManager(), "photo_dialog")
            myCalendarView.setDialogResult(object : PhotoDialog.OnMyDialogResult {
                override fun finish(result: Int) {
                    isLeft = true
                    if (result == 1) {
                        takePhoto()
                    } else if (result == 2) {
                        goToAlbum()
                    }
                }
            })
        }

        set_image2.setOnClickListener {
            val myCalendarView = PhotoDialog()
            myCalendarView.show(getSupportFragmentManager(), "photo_dialog")
            myCalendarView.setDialogResult(object : PhotoDialog.OnMyDialogResult {
                override fun finish(result: Int) {
                    isLeft = false
                    if (result == 1) {
                        takePhoto()
                    } else if (result == 2) {
                        goToAlbum()
                    }
                }
            })
        }

        btn_class!!.setOnClickListener {
            val d = ClassListDialog()
            d.show(getSupportFragmentManager(), "select exam num")
            d.setDialogResult(object : ClassListDialog.OnMyDialogResult {
                override fun finish(Tag: Int, SubTag: Int, result: String) {
                    btn_class!!.text = result
                    this@AddNote.Tag = Tag
                    this@AddNote.SubTag = SubTag
                }
            })
        }

        btn_cancel!!.setOnClickListener {
            val h = Handler()
            h.postDelayed(splashHandler(), 2000)
            btn_cancel!!.isEnabled = false
            finish()
        }

        btn_ok!!.setOnClickListener {
            val h = Handler()
            h.postDelayed(splashHandler(), 2000)
            btn_ok!!.isEnabled = false
            if (btn_class!!.text.toString() == "단원 선택") {
                btn_class!!.setHintTextColor(-0x2cd0d1)
                val d = CustomDialog(13)
                d.show(getSupportFragmentManager(), "insert error2")
                d.setDialogResult(object : CustomDialog.OnMyDialogResult {
                    override fun finish(result: Int) {

                    }

                    override fun finish(result: Int, email: String) {

                    }
                })
            } else {
                //                    int tag = Integer.parseInt(btn_class.getText().toString());
                val ClassId = user_db!!.classId
                db!!.add_values(et_title!!.text.toString(), Tag, ClassId, SubTag)
                val id = db!!.id
                gp_db!!.add_values(id, Tag)

                for (solve in 0..1) {
                    val image_divide = ArrayList<ArrayList<Byte>>()
                    val d: Drawable
                    if (solve == 0) {
                        d = (findViewById(R.id.imageView) as ImageView).drawable as Drawable
                    } else {
                        d = (findViewById(R.id.imageView2) as ImageView).drawable as Drawable
                    }
                    var count = 0
                    val bitmap = (d as BitmapDrawable).bitmap
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

                    val divide = stream.size() / 1048349 + 1
                    divide_t = stream.size() % 1048349
                    var good: ArrayList<Byte>

                    val main_byte = stream.toByteArray()
                    val tmp = ArrayList<Byte>()
                    val buf = ByteBuffer.wrap(main_byte)
                    for (i in 0 until divide) {
                        for (j in 0..1048348) {
                            if (count == stream.size()) {
                                break
                            }
                            tmp.add(java.lang.Byte.parseByte(buf.get(count).toString()))
                            count++
                        }
                        image_divide.add(tmp)
                        good = image_divide[i]
                        if (i == divide - 1) {
                            if (solve == 0)
                                image = ByteArray(divide_t)
                            else
                                image2 = ByteArray(divide_t)
                            for (j in 0 until divide_t) {
                                if (solve == 0)
                                    image?.set(j, good[j])
                                else
                                    image2?.set(j, good[j])
                            }
                        } else {
                            if (solve == 0)
                                image = ByteArray(1048349)
                            else
                                image2 = ByteArray(1048349)
                            for (j in 0..1048348) {
                                if (solve == 0)
                                    image?.set(j, good[j])
                                else
                                    image2?.set(j, good[j])
                            }
                        }
                        if (solve == 0)
                            img_db!!.add_value(id, 0, solve, image)
                        else
                            img_db!!.add_value(id, 0, solve, image2)
                        tmp.clear()
                    }
                }
                img_db!!.add_value2()
                finish()
            }
        }
    }

    @SuppressLint("IntentReset")
    private fun goToAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, PICK_FROM_ALBUM)
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ignored: IOException) {
            }

            if (photoFile != null) {
                val photoURI =
                    FileProvider.getUriForFile(this, "com.cna.mineru.cna.fileprovider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, PICK_FROM_CAMERA)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        @SuppressLint("SimpleDateFormat")
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        mCurrentPhotoPath = image.absolutePath
        return image
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

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            loadingDialog!!.progressON(this, "Loading...")
            try {
                val selectedImageUri = data?.data
                var getImgURL: String? = ""
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

                    tv_1!!.visibility = View.INVISIBLE
                    tv_1_2!!.visibility = View.INVISIBLE
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

                    tv_2!!.visibility = View.INVISIBLE
                    tv_2_2!!.visibility = View.INVISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            loadingDialog!!.progressOFF()
        } else if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            if (isLeft) {
                try {
                    val file = File(mCurrentPhotoPath!!)
                    val bitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), Uri.fromFile(file))
                    if (bitmap != null) {
                        val ei = ExifInterface(mCurrentPhotoPath!!)
                        val orientation = ei.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED
                        )

                        var rotatedBitmap: Bitmap? = null
                        when (orientation) {

                            ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap =
                                rotateImage(bitmap, 90f)

                            ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap =
                                rotateImage(bitmap, 180f)

                            ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap =
                                rotateImage(bitmap, 270f)

                            ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
                            else -> rotatedBitmap = bitmap
                        }
                        imageView!!.setImageBitmap(rotatedBitmap)
                        tv_1!!.visibility = View.INVISIBLE
                        tv_1_2!!.visibility = View.INVISIBLE
                    }
                } catch (error: Exception) {
                    error.printStackTrace()
                }

            } else {
                try {
                    val file = File(mCurrentPhotoPath!!)
                    val bitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), Uri.fromFile(file))
                    if (bitmap != null) {
                        val ei = ExifInterface(mCurrentPhotoPath!!)
                        val orientation = ei.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED
                        )

                        var rotatedBitmap: Bitmap? = null
                        when (orientation) {

                            ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap =
                                rotateImage(bitmap, 90f)

                            ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap =
                                rotateImage(bitmap, 180f)

                            ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap =
                                rotateImage(bitmap, 270f)

                            ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
                            else -> rotatedBitmap = bitmap
                        }
                        imageView2!!.setImageBitmap(rotatedBitmap)
                        tv_2!!.visibility = View.INVISIBLE
                        tv_2_2!!.visibility = View.INVISIBLE
                    }
                } catch (error: Exception) {
                    error.printStackTrace()
                }

            }
        }
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

    private inner class splashHandler : Runnable {
        override fun run() {
            btn_ok!!.isEnabled = true // 클릭 유효화
            btn_cancel!!.isEnabled = true // 클릭 유효화
            btn_class!!.setHintTextColor(-0xafafb0)
        }
    }

    override fun onBackPressed() {
        finish()
    }

    companion object {
        val WIFI_STATE = "WIFE"
        val MOBILE_STATE = "MOBILE"
        val NONE_STATE = "NONE"
        private val PICK_FROM_ALBUM = 1
        private val PICK_FROM_CAMERA = 2

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

        fun rotateImage(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(
                source, 0, 0, source.width, source.height,
                matrix, true
            )
        }
    }
}
