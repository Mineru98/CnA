package com.cna.mineru.cna;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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
import com.cna.mineru.cna.Utils.PhotoDialog;
import com.cna.mineru.cna.Utils.PhotoDialog2;
import com.github.chrisbanes.photoview.PhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import static com.cna.mineru.cna.Utils.Network.getWhatKindOfNetwork.getWhatKindOfNetwork;

public class ModifyHomeItem extends AppCompatActivity {
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    
    private HomeSQLClass db;
    private GraphSQLClass gp_db;

    private EditText et_title;
    private EditText et_class;

    private PhotoView imageView;
    private PhotoView imageView2;

    private int tag;
    private int id;
    private int note_id=0;
    private boolean isLeft;

    private Bitmap bm;
    private Bitmap bm2;

    private LoadingDialog loadingDialog;

    private String getServerURL;
    private String getImgURL="";

    private byte[] image;
    private byte[] image2;

    private File tempFile;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifiy_item);
        ScrollView mScrollView = (ScrollView) findViewById(R.id.scrollView);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        TextView btn_ok = (TextView) findViewById(R.id.btn_save);
        ImageView btn_cancel = (ImageView) findViewById(R.id.btn_cancel);
        et_title = (EditText) findViewById(R.id.et_title);
        et_class = (EditText) findViewById(R.id.et_class);
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);

        db = new HomeSQLClass(this);
        gp_db = new GraphSQLClass(this);
        loadingDialog= new LoadingDialog();
        ImageSQLClass img_db = new ImageSQLClass(this);
        note_id = db.getId();

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

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag = Integer.parseInt(et_class.getText().toString());
                db.update_item(id, et_title.getText().toString(), tag);
                gp_db.update_value(id, tag);
                new JSONNote().execute(getString(R.string.ip_set)+"/api/note/update");
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

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PhotoDialog2 myCalendarView = new PhotoDialog2();
                myCalendarView.show(getSupportFragmentManager(),"photo_dialog2");
                myCalendarView.setDialogResult(new PhotoDialog2.OnMyDialogResult() {
                    @Override
                    public void finish(int result) {
                        isLeft = true;
                        if(result==1){
                            takePhoto();
                        }else if(result==2) {
                            goToAlbum();
                        }else if(result==3){
                            Toast.makeText(v.getContext(), "기능 준비중...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return false;
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

        imageView2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PhotoDialog2 myCalendarView = new PhotoDialog2();
                myCalendarView.show(getSupportFragmentManager(),"photo_dialog2");
                myCalendarView.setDialogResult(new PhotoDialog2.OnMyDialogResult() {
                    @Override
                    public void finish(int result) {
                        isLeft = false;
                        if(result==1){
                            takePhoto();
                        }else if(result==2) {
                            goToAlbum();
                        }else if(result==3){
                            Toast.makeText(v.getContext(), "기능 준비중...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return false;
            }
        });

        if("NONE".equals(getWhatKindOfNetwork(this))) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
                            finish();
                        }
                    });
            builder.show();
        }
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
            Uri photoUri = FileProvider.getUriForFile(this,"com.cna.mineru.cna.ModifyHomeItem",tempFile);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_FROM_ALBUM)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                try {
                    loadingDialog.progressON(ModifyHomeItem.this,"Loading...");
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(isLeft){
            if("NONE".equals(getWhatKindOfNetwork(this))) {
                loadingDialog.progressOFF();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
                //Upload
                //uploadFile(getImgURL);
            }
        }
        else{
            if("NONE".equals(getWhatKindOfNetwork(this))) {
                loadingDialog.progressOFF();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
                //Upload
                //uploadFile(getImgURL);
            }
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
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }
}
