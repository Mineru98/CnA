package com.cna.mineru.cna;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cna.mineru.cna.DB.ImageSQLClass;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class RandomExamSolve extends AppCompatActivity {

    private Button btn_no;
    private Button btn_ok;
    private ImageView btn_cancel;
    private int id;
    private int noteId;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_solve);
        ImageSQLClass db = new ImageSQLClass(RandomExamSolve.this);
        intent = new Intent();
        Intent getIntent = getIntent();

        id = getIntent.getIntExtra("id",0);
        noteId = getIntent.getIntExtra("noteId",1);

        PhotoView imageView = (PhotoView) findViewById(R.id.imageView);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_no = (Button) findViewById(R.id.btn_no);
        btn_cancel = (ImageView) findViewById(R.id.btn_cancel);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("result",1);
                intent.putExtra("id",id);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("result",0);
                intent.putExtra("id",id);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("result",0);
                intent.putExtra("id",id);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        int count = db.getCount(noteId, 1);

        ArrayList<ArrayList<Byte>> image_merge = new ArrayList<>();
        ArrayList<Byte> image_t;
        ArrayList<byte[]> image_arr = db.getImg(noteId, 1);

        int size = 0;
        for (int i = 0; i < count; i++)
            size += image_arr.get(i).length;

        byte[] result = new byte[size];

        ArrayList<Byte> tmp = new ArrayList<>();
        ByteBuffer buf;

        for (int i = 0; i < image_arr.size(); i++) {
            buf = ByteBuffer.wrap(image_arr.get(i));
            for (int j = 0; j < image_arr.get(i).length; j++) {
                tmp.add(buf.get(j));
            }
            buf.clear();
            image_merge.add(tmp);
        }
        count = 0;
        for (int i = 0; i < image_arr.size(); i++) {
            image_t = image_merge.get(i);
            if (i == image_arr.size() - 1) {
                for (int j = i * 1048349; j < image_arr.get(i).length + i * 1048349; j++) {
                    result[count] = image_t.get(j);
                    count++;
                }
            } else {
                for (int j = i * 1048349; j < 1048349 + i * 1048349; j++) {
                    result[count] = image_t.get(j);
                    count++;
                }
            }
        }

        Bitmap bm = BitmapFactory.decodeByteArray(result, 0, result.length);
        saveBitmaptoJpeg(bm, ""+id);
        @SuppressLint("SdCardPath")
        File photo = new File("/sdcard/CnA/"+id+".jpg");
        Uri imageUri = Uri.fromFile(photo);
        String imagePath = imageUri.getPath();
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);
        bm = rotate(bm, exifDegree);
        imageView.setImageBitmap(bm);
        photo.delete();
    }

    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    public static void saveBitmaptoJpeg(Bitmap bitmap, String name){
        String file_name = name+".jpg";
        @SuppressLint("SdCardPath")
        String string_path = "/sdcard/CnA/";

        @SuppressLint("SdCardPath")
        File file_path = new File("/sdcard/CnA");
        try{
            if(!file_path.isDirectory()){
                file_path.mkdir();
            }
            FileOutputStream out = new FileOutputStream(string_path+file_name);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }
}
