package com.cna.mineru.cna;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cna.mineru.cna.DB.HomeSQLClass;
import com.cna.mineru.cna.Utils.LoadingDialog;
import com.cna.mineru.cna.Utils.NetworkService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddNote extends AppCompatActivity {

    private ImageView imageView;
    private Button btn_gallery;
    private Button btn_camera;
    private LinearLayout llBottomNav;

    private byte[] image;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private File tempFile;
    private HomeSQLClass db;
    private boolean isOk;
    private LoadingDialog loadingDialog;
    private Bitmap bitmap_t;
    private EditText et_title;

    String boundary = "*****";
    String crlf = "\r\n";
    String twoHyphens = "--";
    String getServerURL;
    String getImgURL="";
    String getImgName="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        getServerURL = getString(R.string.ip_set)+"/api/file/upload/";
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        loadingDialog = new LoadingDialog();
        db = new HomeSQLClass(this);

        imageView = (ImageView) findViewById(R.id.imageView);
        btn_gallery = (Button) findViewById(R.id.btnGallery);
        btn_camera = (Button) findViewById(R.id.btnCamera);
        llBottomNav = (LinearLayout)findViewById(R.id.llBottomNav);
        et_title = (EditText) findViewById(R.id.et_title);

        TextView btn_ok = (TextView) findViewById(R.id.btn_save);
        TextView btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        ImageView btn_back = (ImageView) findViewById(R.id.btn_back);
        image= new byte[]{0};
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbum();
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
                //et_title 다시 분석해서 tag 수정
                //임시적으로 랜덤값으로 수정
                Drawable d = (Drawable)((ImageView) findViewById(R.id.imageView)).getDrawable();
                getByteArrayFromDrawable(d);
                image = getByteArrayFromDrawable(d);
                Random rnd = new Random();
                int tag = rnd.nextInt(10);
                db.add_values(et_title.getText().toString(),tag,image);
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

    private byte[] getByteArrayFromDrawable(Drawable d) {
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        if (stream.size() < 2096683) {
            isOk = true;
        } else
            isOk = false;
        byte[] data = stream.toByteArray();
        return data;
    }

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
                    //Uri에서 이미지 이름을 얻어온다.
                    String name_Str = getImageNameToUri(data.getData());
                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView)findViewById(R.id.imageView);
                    //배치해놓은 ImageView에 set
                    image.setImageBitmap(image_bitmap);
                    //Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        uploadFile(getImgURL , getImgName);
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
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayBitmapStream);
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
