package com.cna.mineru.cna;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cna.mineru.cna.DB.GraphSQLClass;
import com.cna.mineru.cna.DB.HomeSQLClass;
import com.cna.mineru.cna.DB.ImageSQLClass;
import com.cna.mineru.cna.DB.UserSQLClass;
import com.cna.mineru.cna.Utils.ClassListDialog;
import com.cna.mineru.cna.Utils.CustomDialog;
import com.cna.mineru.cna.Utils.LoadingDialog;
import com.cna.mineru.cna.Utils.PhotoDialog;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddNote extends AppCompatActivity {
    public static final String WIFI_STATE = "WIFE";
    public static final String MOBILE_STATE = "MOBILE";
    public static final String NONE_STATE = "NONE";
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;

    private HomeSQLClass db;
    private ImageSQLClass img_db;
    private GraphSQLClass gp_db;
    private UserSQLClass user_db;

    private TextView tv_1;
    private TextView tv_1_2;

    private TextView tv_2;
    private TextView tv_2_2;

    private TextView btn_ok;
    private Button btn_class;

    private EditText et_title;

    private ImageView imageView;
    private ImageView imageView2;
    private ImageView btn_cancel;

    private LoadingDialog loadingDialog;

    private int divide_t;

    private boolean isLeft;

    private byte[] image;
    private byte[] image2;
    private int Tag;
    private int SubTag;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        db = new HomeSQLClass(this);
        gp_db = new GraphSQLClass(this);
        img_db = new ImageSQLClass(this);
        user_db = new UserSQLClass(this);
        loadingDialog = new LoadingDialog();

        isLeft = true;

        imageView = (ImageView) findViewById(R.id.imageView);
        ImageView iv_exam_num = (ImageView) findViewById(R.id.iv_exam_num);
        ImageView iv_title = (ImageView) findViewById(R.id.iv_title);
        et_title = (EditText) findViewById(R.id.et_title);
        RelativeLayout set_image = (RelativeLayout) findViewById(R.id.set_image);
        RelativeLayout set_image2 = (RelativeLayout) findViewById(R.id.set_image2);
        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        tv_1_2 = (TextView) findViewById(R.id.tv_1_2);
        tv_2_2 = (TextView) findViewById(R.id.tv_2_2);
        TextView tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        TextView tv_title_2 = (TextView) findViewById(R.id.tv_title_2);
        TextView tv_exam_num = (TextView) findViewById(R.id.tv_exam_num);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);

        btn_class = (Button) findViewById(R.id.btn_class);

        btn_ok = (TextView) findViewById(R.id.btn_save);
        btn_cancel = (ImageView) findViewById(R.id.btn_cancel);
        imageView2 = (ImageView)findViewById(R.id.imageView2);

        set_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoDialog myCalendarView = new PhotoDialog();
                myCalendarView.show(getSupportFragmentManager(),"photo_dialog");
                myCalendarView.setDialogResult(new PhotoDialog.OnMyDialogResult() {
                    @Override
                    public void finish(int result) {
                        isLeft = true;
                        if(result==1){
                            takePhoto();
                        }else if(result==2) {
                            goToAlbum();
                        }
                    }
                });
            }
        });

        set_image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoDialog myCalendarView = new PhotoDialog();
                myCalendarView.show(getSupportFragmentManager(),"photo_dialog");
                myCalendarView.setDialogResult(new PhotoDialog.OnMyDialogResult() {
                    @Override
                    public void finish(int result) {
                        isLeft = false;
                        if(result==1){
                            takePhoto();
                        }else if(result==2) {
                            goToAlbum();
                        }
                    }
                });
            }
        });

        btn_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassListDialog d = new ClassListDialog();
                d.show(getSupportFragmentManager(),"select exam num");
                d.setDialogResult(new ClassListDialog.OnMyDialogResult() {
                    @Override
                    public void finish(int Tag, int SubTag, String result) {
                        btn_class.setText(result);
                        AddNote.this.Tag = Tag;
                        AddNote.this.SubTag = SubTag;
                    }
                });
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 2000);
                btn_cancel.setEnabled(false);
                finish();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 2000);
                btn_ok.setEnabled(false);
                if(btn_class.getText().toString().equals("단원 선택")){
                    btn_class.setHintTextColor(0xFFD32F2F);
                    CustomDialog d = new CustomDialog(13);
                    d.show(getSupportFragmentManager(),"insert error2");
                    d.setDialogResult(new CustomDialog.OnMyDialogResult() {
                        @Override
                        public void finish(int result) {

                        }

                        @Override
                        public void finish(int result, String email) {

                        }
                    });
                }else{
//                    int tag = Integer.parseInt(btn_class.getText().toString());
                    int ClassId = user_db.getClassId();
                    db.add_values(et_title.getText().toString(), Tag, ClassId, SubTag);
                    int id = db.getId();
                    gp_db.add_values(id, Tag);

                    for(int solve=0;solve<2;solve++){
                        ArrayList<ArrayList<Byte>> image_divide = new ArrayList<>();
                        Drawable d;
                        if(solve==0){
                            d = (Drawable)((ImageView) findViewById(R.id.imageView)).getDrawable();
                        }else{
                            d = (Drawable)((ImageView) findViewById(R.id.imageView2)).getDrawable();
                        }
                        int count=0;
                        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                        int divide = stream.size() / 1048349 + 1;
                        divide_t = stream.size() % 1048349;
                        ArrayList<Byte> good;

                        byte[] main_byte = stream.toByteArray();
                        ArrayList<Byte> tmp = new ArrayList<>();
                        ByteBuffer buf = ByteBuffer.wrap(main_byte);
                        for(int i=0;i<divide;i++) {
                            for (int j = 0; j < 1048349; j++) {
                                if (count == stream.size()) {
                                    break;
                                }
                                tmp.add(Byte.parseByte(String.valueOf(buf.get(count))));
                                count++;
                            }
                            image_divide.add(tmp);
                            good = image_divide.get(i);
                            if (i == divide-1) {
                                if(solve==0)
                                    image = new byte[divide_t];
                                else
                                    image2 = new byte[divide_t];
                                for (int j = 0; j < divide_t; j++) {
                                    if(solve==0)
                                        image[j] = good.get(j);
                                    else
                                        image2[j] = good.get(j);
                                }
                            } else {
                                if(solve==0)
                                    image = new byte[1048349];
                                else
                                    image2 = new byte[1048349];
                                for (int j = 0; j < 1048349; j++) {
                                    if(solve==0)
                                        image[j] = good.get(j);
                                    else
                                        image2[j] = good.get(j);
                                }
                            }
                            if(solve==0)
                                img_db.add_value(id, 0, solve, image);
                            else
                                img_db.add_value(id, 0, solve, image2);
                            tmp.clear();
                        }
                    }
                    img_db.add_value2();
                    finish();
                }
            }
        });
    }

    @SuppressLint("IntentReset")
    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ignored) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.cna.mineru.cna.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public String getPath(Uri uri) {
        // uri가 null일경우 null반환
        if( uri == null ) {
            return null;
        }
        // 미디어스토어에서 유저가 선택한 사진의 URI를 받아온다.
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // URI경로를 반환한다.
        return uri.getPath();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            loadingDialog.progressON(this,"Loading...");
            try {
                Uri selectedImageUri = data.getData();
                String getImgURL = "";
                if (isLeft) {
                    try {
                        // 비트맵 이미지로 가져온다
                        String imagePath = getPath(selectedImageUri);
                        getImgURL = imagePath;
                        Bitmap image = BitmapFactory.decodeFile(imagePath);
                        ExifInterface exif = new ExifInterface(imagePath);
                        int exifOrientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int exifDegree = exifOrientationToDegrees(exifOrientation);
                        image = rotate(image, exifDegree);
                        imageView.setImageBitmap(image);
                    } catch (Exception e) {
                        Toast.makeText(this, "오류발생: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                    tv_1.setVisibility(View.INVISIBLE);
                    tv_1_2.setVisibility(View.INVISIBLE);
                } else {
                    try {
                        // 비트맵 이미지로 가져온다
                        String imagePath = getPath(selectedImageUri);
                        getImgURL = imagePath;
                        Bitmap image = BitmapFactory.decodeFile(imagePath);
                        ExifInterface exif = new ExifInterface(imagePath);
                        int exifOrientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int exifDegree = exifOrientationToDegrees(exifOrientation);
                        image = rotate(image, exifDegree);
                        imageView2.setImageBitmap(image);
                    } catch (Exception e) {
                        Toast.makeText(this, "오류발생: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                    tv_2.setVisibility(View.INVISIBLE);
                    tv_2_2.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadingDialog.progressOFF();
        } else if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            if (isLeft) {
                try{
                    File file = new File(mCurrentPhotoPath);
                    Bitmap bitmap = MediaStore.Images.Media
                            .getBitmap(getContentResolver(), Uri.fromFile(file));
                    if (bitmap != null) {
                        ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        Bitmap rotatedBitmap = null;
                        switch(orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(bitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = bitmap;
                        }
                        imageView.setImageBitmap(rotatedBitmap);
                        tv_1.setVisibility(View.INVISIBLE);
                        tv_1_2.setVisibility(View.INVISIBLE);
                    }
                }catch (Exception error){
                    error.printStackTrace();
                }
            } else{
                try{
                    File file = new File(mCurrentPhotoPath);
                    Bitmap bitmap = MediaStore.Images.Media
                            .getBitmap(getContentResolver(), Uri.fromFile(file));
                    if (bitmap != null) {
                        ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        Bitmap rotatedBitmap = null;
                        switch(orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(bitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = bitmap;
                        }
                        imageView2.setImageBitmap(rotatedBitmap);
                        tv_2.setVisibility(View.INVISIBLE);
                        tv_2_2.setVisibility(View.INVISIBLE);
                    }
                }catch (Exception error){
                    error.printStackTrace();
                }
            }
        }
    }

    public static String getWhatKindOfNetwork(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return WIFI_STATE;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return MOBILE_STATE;
            }
        }
        return NONE_STATE;
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

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
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

    private class splashHandler implements Runnable{
        public void run()	{
            btn_ok.setEnabled(true); // 클릭 유효화
            btn_cancel.setEnabled(true); // 클릭 유효화
            btn_class.setHintTextColor(0xFF505050);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
