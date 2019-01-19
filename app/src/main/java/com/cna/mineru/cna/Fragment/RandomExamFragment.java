package com.cna.mineru.cna.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cna.mineru.cna.DB.ImageSQLClass;
import com.cna.mineru.cna.ModifyHomeItem;
import com.cna.mineru.cna.R;
import com.cna.mineru.cna.RandomExam;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class RandomExamFragment extends Fragment {

    private int id;
    private ImageSQLClass db;
    private Bitmap bm;

    @SuppressLint("ValidFragment")
    public RandomExamFragment(int id) {
        this.id = id;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_random_exam, container, false);
        PhotoView imageView = view.findViewById(R.id.imageView);


        db = new ImageSQLClass(getContext());
        int count = db.getCount(id, 0);

        ArrayList<ArrayList<Byte>> image_merge = new ArrayList<>();
        ArrayList<Byte> image_t;
        ArrayList<byte[]> image_arr = db.getImg(id, 0);

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

        bm = BitmapFactory.decodeByteArray(result, 0, result.length);
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


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
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
        return view;
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
