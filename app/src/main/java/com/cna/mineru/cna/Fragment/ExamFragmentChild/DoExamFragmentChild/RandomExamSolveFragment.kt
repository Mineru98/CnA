package com.cna.mineru.cna.Fragment.ExamFragmentChild.DoExamFragmentChild

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment

import com.cna.mineru.cna.DB.ImageSQLClass
import com.cna.mineru.cna.R

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.ArrayList

@SuppressLint("ValidFragment")
class RandomExamSolveFragment(var i: Int) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_random_exam, container, false)
        val imageView = view.findViewById<ImageView>(R.id.imageView)

        val db = ImageSQLClass(getContext())
        var count = db.getCount(id, 1)

        val image_merge = ArrayList<ArrayList<Byte>>()
        var image_t: ArrayList<Byte>
        val image_arr = db.getImg(id, 1)

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

        var bm: Bitmap? = BitmapFactory.decodeByteArray(result, 0, result.size)
        saveBitmaptoJpeg(bm, "" + id)
        @SuppressLint("SdCardPath")
        val photo = File("/sdcard/CnA/$id.jpg")
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
        imageView.setImageBitmap(bm)
        photo.delete()
        return view
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

    companion object {

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
