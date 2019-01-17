package com.cna.mineru.cna;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cna.mineru.cna.DB.GraphSQLClass;
import com.cna.mineru.cna.DB.HomeSQLClass;
import com.cna.mineru.cna.DB.ImageSQLClass;
import com.cna.mineru.cna.Utils.LoadingDialog;
import com.cna.mineru.cna.Utils.NetworkService;
import com.cna.mineru.cna.Utils.PhotoDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddNote extends AppCompatActivity {

    private byte[] image;
    private byte[] image2;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private File tempFile;
    private HomeSQLClass db;
    private ImageSQLClass img_db;
    private GraphSQLClass gp_db;
    private boolean isOk;
    private boolean isLeft;
    private LoadingDialog loadingDialog;
    private Bitmap bitmap_t;
    private EditText et_title;
    private RelativeLayout set_image;
    private RelativeLayout set_image2;
    private TextView tv_1;
    private TextView tv_2;
    private TextView et_class;

    String boundary = "*****";
    String crlf = "\r\n";
    String twoHyphens = "--";
    String getServerURL;
    String getImgURL="";
    String getImgName="";
    private int divide_t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        getServerURL = getString(R.string.ip_set)+"/api/file/upload/";
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        loadingDialog = new LoadingDialog();
        db = new HomeSQLClass(this);
        gp_db = new GraphSQLClass(this);
        img_db = new ImageSQLClass(this);

        isLeft = true;
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        et_title = (EditText) findViewById(R.id.et_title);
        set_image = (RelativeLayout) findViewById(R.id.set_image);
        set_image2 = (RelativeLayout) findViewById(R.id.set_image2);
        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        et_class = (EditText) findViewById(R.id.et_class);

        TextView btn_ok = (TextView) findViewById(R.id.btn_save);
        TextView btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        ImageView btn_back = (ImageView) findViewById(R.id.btn_back);
        //image= new byte[]{0};

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

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int tag = Integer.parseInt(et_class.getText().toString());
                db.add_values(et_title.getText().toString(), tag);
                int id = db.getId();
                gp_db.add_values(id, tag);

                ArrayList<ArrayList<Byte>> image_divide = new ArrayList<>();
                Drawable d = (Drawable)((ImageView) findViewById(R.id.imageView)).getDrawable();

                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                int divide = stream.size() / 1048349 + 1;
                divide_t = stream.size() % 1048349;
                isOk = true;
                ArrayList<Byte> good;

                byte[] main_byte = stream.toByteArray();
                ArrayList<Byte> tmp = new ArrayList<>();
                ByteBuffer buf = ByteBuffer.wrap(main_byte);
                int count=0;
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
                        image = new byte[divide_t];
                        for (int j = 0; j < divide_t; j++) {
                            image[j] = good.get(j);
                        }
                    } else {
                        image = new byte[1048349];
                        for (int j = 0; j < 1048349; j++) {
                            image[j] = good.get(j);
                        }
                    }
                    img_db.add_value(id, 0, 0, image);
                    tmp.clear();
                }
                img_db.add_value2();
                finish();
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

//    private byte[] getByteArrayFromDrawable(Drawable d) {
//        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//
//        int divide = stream.size() / 2096683;
//        ArrayList<byte[]> image_divde = new ArrayList<byte[]>();
//        byte[] tmp = stream.toByteArray();
//        ByteBuffer buf = ByteBuffer.wrap(tmp);
//        for (int i = 0; i < 2096683; i++) {
//            buf.position(i);
//        }
//        isOk = true;
//        return data;
//    }


    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {
            Uri photoUri = FileProvider.getUriForFile(this,"com.cna.mineru.cna.AddNote",tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_kkmmss").format(new Date());
        String imageFileName = "CnA_" + timeStamp + "";
        File storageDir = new File("sdcard/CnA/Img");
        if (!storageDir.exists()){
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        getImgURL = imgPath;
        getImgName = imgName;
        return "success";
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_FROM_ALBUM)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                try {
                    loadingDialog.progressON(AddNote.this,"Loading...");
                    String name_Str = getImageNameToUri(data.getData());
                    Bitmap image_bitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    if(isLeft){
                        ImageView image = (ImageView)findViewById(R.id.imageView);
                        image.setImageBitmap(image_bitmap);
                        tv_1.setVisibility(View.INVISIBLE);
                    }else{
                        ImageView image = (ImageView)findViewById(R.id.imageView2);
                        image.setImageBitmap(image_bitmap);
                        loadingDialog.progressOFF();
                        tv_2.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(isLeft){
            uploadFile(getImgURL , getImgName);
        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != Activity.RESULT_OK) {
//            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
//            if(tempFile != null) {
//                if (tempFile.exists()) {
//                    if (tempFile.delete()) {
//                        Log.e("AddNote", tempFile.getAbsolutePath() + " 삭제 성공");
//                        tempFile = null;
//                    }
//                }
//            }
//            return;
//        }
//        if (requestCode == PICK_FROM_ALBUM) {
//            Uri photoUri = data.getData();
//            Cursor cursor = null;
//            try {
//                String[] proj = { MediaStore.Images.Media.DATA };
//                assert photoUri != null;
//                cursor = getContentResolver().query(photoUri, proj, null, null, null);
//                assert cursor != null;
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToFirst();
//                tempFile = new File(cursor.getString(column_index));
//            } finally {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//            loadingDialog.progressON(this,"Loading...");
//            setImage();
//
//            Drawable d = (Drawable)((ImageView) findViewById(R.id.imageView)).getDrawable();
//            getByteArrayFromDrawable(d);
//            image = getByteArrayFromDrawable(d);
//            if(isOk){
//                //db.add_values("",1,image);
//                Log.d("TAG", "Mineru : upload");
//                new JSONTask().execute("http://192.168.219.102:3000/api/file/upload");
//                //new JSONTask().execute("http://122.34.12.104/api/file/upload");
//            }
//            else{
//                AlertDialog.Builder dialog = new AlertDialog.Builder(AddNote.this);
//                dialog.setTitle("이미지 크기 오류");
//                dialog.setMessage("현재 이미지 크기가 큽니다. (최대 1.9MB)");
//                dialog.setPositiveButton("취소", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        AddNote.this.finish();
//                    }
//                });
//                dialog.show();
//            }
//        }
//        else if (requestCode == PICK_FROM_CAMERA) {
//            loadingDialog.progressON(this,"Loading...");
//            setImage();
//            Drawable d = (Drawable)((ImageView) findViewById(R.id.imageView)).getDrawable();
//            getByteArrayFromDrawable(d);
//            byte[] image = getByteArrayFromDrawable(d);
//            if(isOk){
//                //db.add_values("",1,image);
//                Log.d("TAG", "Mineru : upload");
//                new JSONTask().execute("http://192.168.219.102:3000/api/file/upload");
//                //new JSONTask().execute("http://122.34.12.104/api/file/upload");
//            }
//            else{
//                AlertDialog.Builder dialog = new AlertDialog.Builder(AddNote.this);
//                dialog.setTitle("이미지 크기 오류");
//                dialog.setMessage("현재 이미지 크기가 큽니다. (최대 1.9MB)");
//                dialog.setPositiveButton("취소", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        AddNote.this.finish();
//                    }
//                });
//                dialog.show();
//            }
//        }
//    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, 0, byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadFile(String ImgURL, String ImgName) {

        String url = getServerURL;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .build();

        NetworkService service = retrofit.create(NetworkService.class);

        File photo = new File(ImgURL);
        RequestBody photoBody = RequestBody.create(MediaType.parse("multipart/form-data"), photo);
        //RequestBody photoBody = RequestBody.create(MediaType.parse("image/jpg"), photo);
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", photo.getName(), photoBody);

        String descriptionString = "userfile";

        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        Call<ResponseBody> call = service.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String jsonString = "";
                try {
                    jsonString = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                et_title.setText(jsonString);
                loadingDialog.progressOFF();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                loadingDialog.progressOFF();
            }
        });
    }
}
