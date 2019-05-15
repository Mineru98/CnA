package com.cna.mineru.cna;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.cna.mineru.cna.DB.UserSQLClass;
import com.cna.mineru.cna.Utils.LoadingDialog;
import com.cna.mineru.cna.Utils.Network.NetworkService;
import com.cna.mineru.cna.Utils.Network.NormalNetworkService;
import com.cna.mineru.cna.Utils.PhotoDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private TextView tv_2;
    private TextView btn_ok;
    private TextView et_class;
    private TextView btn_cancel;

    private EditText et_title;

    private ImageView imageView;
    private ImageView imageView2;

    private LoadingDialog loadingDialog;

    private int divide_t;

    private boolean isLeft;
    private boolean isUpload;
    private boolean isPremium;

    private String getServerURL;
    private String getImgURL="";
    private String note_id="";

    private byte[] image;
    private byte[] image2;

    private File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        loadingDialog = new LoadingDialog();

        db = new HomeSQLClass(this);
        gp_db = new GraphSQLClass(this);
        img_db = new ImageSQLClass(this);
        user_db = new UserSQLClass(this);

        isLeft = true;
        isUpload = false;

        if(!user_db.getPremium()){
            getServerURL = getString(R.string.ip_set) + "/api/file/p/upload/";
            isPremium=true;
        }else{
            getServerURL = getString(R.string.ip_set) + "/api/file/n/upload/";
            isPremium=false;
        }

        imageView = (ImageView) findViewById(R.id.imageView);
        et_title = (EditText) findViewById(R.id.et_title);
        RelativeLayout set_image = (RelativeLayout) findViewById(R.id.set_image);
        RelativeLayout set_image2 = (RelativeLayout) findViewById(R.id.set_image2);
        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        et_class = (EditText) findViewById(R.id.et_class);

        btn_ok = (TextView) findViewById(R.id.btn_save);
        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
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

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 2000);
                btn_cancel.setEnabled(false);
                if(isUpload)
                    new DelNote().execute(getString(R.string.ip_set)+"/api/note/destroy");
                finish();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 2000);
                btn_ok.setEnabled(false);
                if(et_class.getText().toString().equals("")){
                    et_class.setHintTextColor(0xFFD32F2F);
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddNote.this);
                    builder.setTitle("입력 오류");
                    builder.setMessage("단원을 입력해주세요.");
                    builder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }else{
                    int tag = Integer.parseInt(et_class.getText().toString());
                    int ClassId = user_db.getClassId();
                    db.add_values(note_id, et_title.getText().toString(), tag, ClassId);
                    int id = db.getId();
                    gp_db.add_values(id, tag);

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
                    new JSONNote().execute(getString(R.string.ip_set)+"/api/note/update");
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
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_kkmmss").format(new Date());
        String imageFileName = "CnA_" + timeStamp + "";
        File storageDir = new File("sdcard/CnA/Img");
        if (!storageDir.exists()){
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
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
        if(requestCode == PICK_FROM_ALBUM)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                try {
                    loadingDialog.progressON(AddNote.this,"Loading...");
                    Uri selectedImageUri = data.getData();
                    if(isLeft){
                        try
                        {
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
                        }
                        catch(Exception e) {
                            Toast.makeText(this, "오류발생: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                        tv_1.setVisibility(View.INVISIBLE);
                    }else{
                        try
                        {
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
                        }
                        catch(Exception e) {
                            Toast.makeText(this, "오류발생: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                        tv_2.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(isLeft){
            if("NONE".equals(getWhatKindOfNetwork(this))) {
                loadingDialog.progressOFF();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("오류");
            builder.setMessage("인터넷 연결 상태를 확인해 주세요.\n인터넷 설정으로 이동하시겠습니까?");
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intentConfirm = new Intent();
                            intentConfirm.setAction("android.settings.WIFI_SETTINGS");
                            startActivity(intentConfirm);
                        }
                    });
            builder.setNegativeButton("아니요",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
            }else{
                uploadFile(getImgURL);
            }
        }
        else{
            if("NONE".equals(getWhatKindOfNetwork(this))) {
                loadingDialog.progressOFF();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("오류");
            builder.setMessage("인터넷 연결 상태를 확인해 주세요.\n인터넷 설정으로 이동하시겠습니까?");
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intentConfirm = new Intent();
                            intentConfirm.setAction("android.settings.WIFI_SETTINGS");
                            startActivity(intentConfirm);
                        }
                    });
            builder.setNegativeButton("아니요",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
            }else{
                uploadFile(getImgURL);
            }
        }
    }

    private void uploadFile(String ImgURL) {
        String url = getServerURL;
        isUpload = true;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .build();

        if(isPremium){
            NetworkService service = retrofit.create(NetworkService.class);
            File photo = new File(ImgURL);
            RequestBody photoBody = RequestBody.create(MediaType.parse("multipart/form-data"), photo);
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
                    note_id = jsonString;
//                    et_title.setText(jsonString);
                    loadingDialog.progressOFF();
                    //서버에 다시 요청해서 사진 데이터 받아오기.
                    new JSONTask().execute(getString(R.string.ip_set)+"/api/file/img");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    loadingDialog.progressOFF();
                }
            });
        }else{
            NormalNetworkService service = retrofit.create(NormalNetworkService.class);
            File photo = new File(ImgURL);
            RequestBody photoBody = RequestBody.create(MediaType.parse("multipart/form-data"), photo);
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
                    note_id = jsonString;
//                    et_title.setText(jsonString);
                    loadingDialog.progressOFF();
                    //서버에 다시 요청해서 사진 데이터 받아오기.
                    new JSONTask().execute(getString(R.string.ip_set)+"/api/file/img");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    loadingDialog.progressOFF();
                }
            });
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("id", note_id);
                jsonObject.accumulate("isLeft", isLeft);
                jsonObject.accumulate("isPremium", isPremium);
                HttpURLConnection con = null;
                BufferedReader reader = null;
                URL url = new URL(urls[0]);
                try {
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Connection", "Keep-Alive");
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setRequestProperty("Accept-Charset", "UTF-8");
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    OutputStream outStream = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    InputStreamReader stream = new InputStreamReader(con.getInputStream(), "UTF-8");

                    reader = new BufferedReader(stream);

                    StringBuffer buffer = new StringBuffer();

                    String line = "";

                    while ((line = reader.readLine()) != null) {//(중요)서버로부터 한줄씩 읽어서 문자가 없을때까지 넣어줌
                        buffer.append(line + "\n"); //읽어준 스트링값을 더해준다.
                    }
                    line = buffer.toString();
                    return line;//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String prob = "";
            String solv = "";
            String data = "";
            int error = 0;
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(result);
                error = jObject.optInt("error");
                if(error==2){

                }else {
                    if(isPremium){
                        prob = jObject.getString("prob");
                        byte[] bytePlainOrg = Base64.decode(prob, 0);
                        ByteArrayInputStream inStream = new ByteArrayInputStream(bytePlainOrg);
                        Bitmap bm = BitmapFactory.decodeStream(inStream);
                        imageView.setImageBitmap(bm);

                        tv_2.setVisibility(View.INVISIBLE);
                        solv = jObject.getString("solv");
                        bytePlainOrg = Base64.decode(solv, 0);
                        inStream = new ByteArrayInputStream(bytePlainOrg);
                        bm = BitmapFactory.decodeStream(inStream);
                        imageView2.setImageBitmap(bm);
                    }
                    else{
                        if(isLeft) {
                            data = jObject.getString("data");
                            byte[] bytePlainOrg = Base64.decode(data, 0);
                            ByteArrayInputStream inStream = new ByteArrayInputStream(bytePlainOrg);
                            Bitmap bm = BitmapFactory.decodeStream(inStream);
                            imageView.setImageBitmap(bm);
                        }
                        else{
                            data = jObject.getString("data");
                            byte[] bytePlainOrg = Base64.decode(data, 0);
                            ByteArrayInputStream inStream = new ByteArrayInputStream(bytePlainOrg);
                            Bitmap bm = BitmapFactory.decodeStream(inStream);
                            imageView2.setImageBitmap(bm);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            loadingDialog.progressOFF();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class JSONNote extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("id", note_id);
                jsonObject.accumulate("note_type", et_class.getText().toString());
                jsonObject.accumulate("title", et_title.getText().toString());
                HttpURLConnection con = null;
                BufferedReader reader = null;
                URL url = new URL(urls[0]);
                try {
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("PUT");//PUT방식으로 보냄
                    con.setRequestProperty("Connection", "Keep-Alive");
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setRequestProperty("Accept-Charset", "UTF-8");
                    con.setDoOutput(true);//Outstream으로 PUT 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    OutputStream outStream = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    InputStreamReader stream = new InputStreamReader(con.getInputStream(), "UTF-8");

                    reader = new BufferedReader(stream);

                    StringBuffer buffer = new StringBuffer();

                    String line = "";

                    while ((line = reader.readLine()) != null) {//(중요)서버로부터 한줄씩 읽어서 문자가 없을때까지 넣어줌
                        buffer.append(line + "\n"); //읽어준 스트링값을 더해준다.
                    }
                    line = buffer.toString();
                    return line;//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            int error = 0;
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(result);
                error = jObject.optInt("error");
                if(error==2){

                }else {
                    jObject.getBoolean("Success");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            loadingDialog.progressOFF();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class DelNote extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("id", note_id);
                HttpURLConnection con = null;
                BufferedReader reader = null;
                URL url = new URL(urls[0]);
                try {
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("DELETE");//DELETE 방식으로 보냄
                    con.setRequestProperty("Connection", "Keep-Alive");
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setRequestProperty("Accept-Charset", "UTF-8");
                    con.setDoOutput(true);//Outstream으로 PUT 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    OutputStream outStream = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    InputStreamReader stream = new InputStreamReader(con.getInputStream(), "UTF-8");

                    reader = new BufferedReader(stream);

                    StringBuffer buffer = new StringBuffer();

                    String line = "";

                    while ((line = reader.readLine()) != null) {//(중요)서버로부터 한줄씩 읽어서 문자가 없을때까지 넣어줌
                        buffer.append(line + "\n"); //읽어준 스트링값을 더해준다.
                    }
                    line = buffer.toString();
                    return line;//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            int error = 0;
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(result);
                error = jObject.optInt("error");
                if(error==2){

                }else {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            loadingDialog.progressOFF();
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
            et_class.setHintTextColor(0xFF505050);
        }
    }

    @Override
    public void onBackPressed() {
        if(isUpload){
            new DelNote().execute(getString(R.string.ip_set)+"/api/note/destroy");
        }
        finish();
    }
}
