package com.cna.mineru.cna;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cna.mineru.cna.DB.HomeSQLClass;

import java.util.Random;

public class ModifyHomeItem extends AppCompatActivity {

    private EditText et_title;

    HomeSQLClass db;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifiy_item);

        ScrollView mScrollView = (ScrollView) findViewById(R.id.scrollView);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        db = new HomeSQLClass(this);

        TextView btn_ok = (TextView) findViewById(R.id.btn_save);
        TextView btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        ImageView btn_back = (ImageView) findViewById(R.id.btn_back);

        et_title = (EditText) findViewById(R.id.et_title);

        Intent intent = getIntent();

        final int id = intent.getExtras().getInt("id");
        String title = intent.getExtras().getString("title");
        et_title.setText(title);

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
                Random rnd = new Random();
                int tag = rnd.nextInt(10);
                db.update_item(id,et_title.getText().toString(),tag);
                finish();
            }
        });
    }
}
