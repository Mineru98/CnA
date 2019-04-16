package com.cna.mineru.cna;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cna.mineru.cna.DB.GraphSQLClass;
import com.cna.mineru.cna.DB.HomeSQLClass;
import com.cna.mineru.cna.DB.ImageSQLClass;
import com.cna.mineru.cna.Utils.LoadingDialog;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ModifyHomeItem extends AppCompatActivity {

    private EditText et_title;
    private EditText et_class;
    private HomeSQLClass db;
    private GraphSQLClass gp_db;
    private int tag;
    private int id;
    private Bitmap bm;
    private Bitmap bm2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifiy_item);
        ScrollView mScrollView = (ScrollView) findViewById(R.id.scrollView);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        db = new HomeSQLClass(this);
        gp_db = new GraphSQLClass(this);
        ImageSQLClass img_db = new ImageSQLClass(this);

        TextView btn_ok = (TextView) findViewById(R.id.btn_save);
        TextView btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        ImageView btn_back = (ImageView) findViewById(R.id.btn_back);
        et_title = (EditText) findViewById(R.id.et_title);
        et_class = (EditText) findViewById(R.id.et_class);
        PhotoView imageView = findViewById(R.id.imageView);
        PhotoView imageView2 = findViewById(R.id.imageView2);

        Intent intent = getIntent();
        id = intent.getExtras().getInt("id");
        String title = intent.getExtras().getString("title");
        tag = intent.getExtras().getInt("tag");
        int count;

        for(int solve=0;solve<2;solve++){
            count = img_db.getCount(id, solve);
            ArrayList<ArrayList<Byte>> image_merge = new ArrayList<>();
            ArrayList<Byte> image_t;
            ArrayList<byte[]> image_arr = img_db.getImg(id, solve);

            int size= 0;
            for(int i=0;i<count;i++)
                size += image_arr.get(i).length;

            byte[] result = new byte[size];

            ArrayList<Byte> tmp = new ArrayList<>();
            ByteBuffer buf;

            for(int i=0;i<image_arr.size();i++){
                buf = ByteBuffer.wrap(image_arr.get(i));
                for(int j=0;j<image_arr.get(i).length;j++){
                    tmp.add(buf.get(j));
                }
                buf.clear();
                image_merge.add(tmp);
            }
            count = 0;
            for(int i=0;i<image_arr.size();i++){
                image_t = image_merge.get(i);
                if(i==image_arr.size()-1){
                    for(int j = i * 1048349;j<image_arr.get(i).length + i * 1048349;j++){
                        result[count] = image_t.get(j);
                        count++;
                    }
                }else{
                    for(int j = i * 1048349;j<1048349 + i * 1048349;j++){
                        result[count] = image_t.get(j);
                        count++;
                    }
                }
            }
            if(solve==0){
                bm = BitmapFactory.decodeByteArray(result,0,result.length);
                saveBitmaptoJpeg(bm,"1");
                @SuppressLint("SdCardPath")
                File photo = new File("/sdcard/CnA/1.jpg");
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
            }else{
                bm2 = BitmapFactory.decodeByteArray(result,0,result.length);
                saveBitmaptoJpeg(bm2,"2");
                @SuppressLint("SdCardPath")
                File photo = new File("/sdcard/CnA/2.jpg");
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
                bm2 = rotate(bm2, exifDegree);
                imageView2.setImageBitmap(bm2);
                photo.delete();
            }
        }

        et_title.setText(title);
        et_class.setText(String.valueOf(tag));

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag = Integer.parseInt(et_class.getText().toString());
                db.update_item(id, et_title.getText().toString(), tag);
                gp_db.update_value(id, tag);
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ModifyHomeItem.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_image_layout, null);
                PhotoView photoView = mView.findViewById(R.id.imageView);
                photoView.setImageBitmap(bm);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.cancel();
                    }
                });
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ModifyHomeItem.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_image_layout, null);
                PhotoView photoView = mView.findViewById(R.id.imageView);
                photoView.setImageBitmap(bm2);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.cancel();
                    }
                });
            }
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }
}
