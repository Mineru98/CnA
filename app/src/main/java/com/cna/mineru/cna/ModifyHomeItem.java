package com.cna.mineru.cna;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cna.mineru.cna.DB.GraphSQLClass;
import com.cna.mineru.cna.DB.HomeSQLClass;
import com.cna.mineru.cna.DB.ImageSQLClass;
import com.github.chrisbanes.photoview.PhotoView;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ModifyHomeItem extends AppCompatActivity {

    private EditText et_title;
    private EditText et_class;
    private HomeSQLClass db;
    private GraphSQLClass gp_db;
    private ImageSQLClass img_db;
    private int tag;
    private int id;
    private String title;
    private Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifiy_item);

        ScrollView mScrollView = (ScrollView) findViewById(R.id.scrollView);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        db = new HomeSQLClass(this);
        gp_db = new GraphSQLClass(this);
        img_db = new ImageSQLClass(this);

        TextView btn_ok = (TextView) findViewById(R.id.btn_save);
        TextView btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        ImageView btn_back = (ImageView) findViewById(R.id.btn_back);
        et_title = (EditText) findViewById(R.id.et_title);
        et_class = (EditText) findViewById(R.id.et_class);
        PhotoView imageView = findViewById(R.id.imageView);
        PhotoView imageView2 = findViewById(R.id.imageView2);

        Intent intent = getIntent();
        id = intent.getExtras().getInt("id");
        title = intent.getExtras().getString("title");
        tag = intent.getExtras().getInt("tag");

        ArrayList<ArrayList<Byte>> image_merge = new ArrayList<>();
        ArrayList<Byte> image_t;
        ArrayList<byte[]> image_arr = img_db.getImg(id);

        int count = img_db.getCount(id);
        int size= 0;
        for(int i=0;i<count;i++){
            size += image_arr.get(i).length;
        }

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

//        byte[] image;
//        image = db.getImg(id);

        et_title.setText(title);
        et_class.setText(String.valueOf(tag));

        if(result==null){
            imageView.setImageResource(R.drawable.ic_camera);
        }else{
            bm = BitmapFactory.decodeByteArray(result,0,result.length);
            //bm = BitmapFactory.decodeByteArray(image,0,image.length);
            imageView.setImageBitmap(bm);
            imageView2.setImageBitmap(bm);
        }

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
                photoView.setImageBitmap(bm);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }
}
