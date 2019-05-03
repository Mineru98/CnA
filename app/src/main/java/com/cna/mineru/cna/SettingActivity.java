package com.cna.mineru.cna;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cna.mineru.cna.DB.UserSQLClass;
import com.cna.mineru.cna.Utils.LoadingDialog;
import com.cna.mineru.cna.Utils.WebViewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SettingActivity extends AppCompatActivity {
    private FirebaseUser mFirebaseUser;
    private UserSQLClass db;
    private boolean isLogin;
    private ImageView btn_back;
    private LoadingDialog loadingDialog;
    private int user_id;

    @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        db = new UserSQLClass(this);
        isLogin = getIntent().getBooleanExtra("isLogin",true);
        loadingDialog = new LoadingDialog();
        btn_back = (ImageView) findViewById(R.id.btn_back);
        TextView tv_ver = (TextView) findViewById(R.id.tv_ver);
        TextView tv_str_ver = (TextView) findViewById(R.id.tv_str_ver);
        PackageInfo packageInfo = null;
        try{
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        tv_str_ver.setText("현재 버전: "+packageInfo.versionName);
        SpannableString content = new SpannableString("Application                                                                                          ");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv_ver.setText(content);

        TextView tv_abouts = (TextView) findViewById(R.id.tv_abouts);
        TextView tv_terms = (TextView) findViewById(R.id.tv_terms);
        TextView tv_privacy = (TextView) findViewById(R.id.tv_privacy);
        content = new SpannableString("Abouts                                                                                                     ");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv_abouts.setText(content);

        TextView tv_account = (TextView) findViewById(R.id.tv_account);
        TextView tv_profile = (TextView) findViewById(R.id.tv_profile);
        Switch sw_profile = (Switch) findViewById(R.id.sw_profile);
        TextView tv_logout = (TextView) findViewById(R.id.tv_logout);
        TextView tv_signout = (TextView) findViewById(R.id.tv_signout);
        content = new SpannableString("Account                                                                                                    ");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv_account.setText(content);

        TextView tv_setting = (TextView) findViewById(R.id.tv_setting);
        TextView tv_wifisync = (TextView) findViewById(R.id.tv_wifisync);
        Switch sw_wifisync = (Switch) findViewById(R.id.sw_wifisync);
        content = new SpannableString("Setting                                                                                                   ");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv_setting.setText(content);

        if(!db.getWifiSync()){
            sw_wifisync.setChecked(true);
        }else{
            sw_wifisync.setChecked(false);
        }

        if(!db.getPremium()){
            sw_profile.setChecked(true);
        }else{
            sw_profile.setChecked(false);
        }

        tv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingActivity.this,WebViewActivity.class);
                i.putExtra("value","terms");
                startActivity(i);
            }
        });

        tv_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingActivity.this,WebViewActivity.class);
                i.putExtra("value","privacy");
                startActivity(i);
            }
        });

        sw_profile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    db.update_isPremium(0);
                }else{
                    db.update_isPremium(1);
                }
            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this);
                dialog.setMessage("로그아웃 하시겠습니까?");
                dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        loadingDialog.progressON(SettingActivity.this,"Loading...");
                        isLogin=false;
                        SharedPreferences pref = getSharedPreferences("isLogin",MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("isLogin",false);
                        editor.apply();
                        FirebaseAuth.getInstance().signOut();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("isLogin",isLogin);
                        setResult(RESULT_OK,resultIntent);
                        finish();
                    }
                });
                dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });

        tv_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this);
                dialog.setMessage("회원탈퇴 하시겠습니까?");
                dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        loadingDialog.progressON(SettingActivity.this,"Loading...");
                        isLogin=false;
                        SharedPreferences pref = getSharedPreferences("isLogin",MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("isLogin",false);
                        editor.apply();
                        FirebaseAuth.getInstance().signOut();
                        boolean isGoogle;
                        isGoogle = db.delete_User();
                        if(isGoogle){
                            mFirebaseUser.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                            }
                                        }
                                    });
                        }else{
                            new JSONTask().execute(getString(R.string.ip_set)+"/api/user/delete");
                        }

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("isLogin",isLogin);
                        setResult(RESULT_OK,resultIntent);
                        finish();
                    }
                });
                dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });

        sw_wifisync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    db.update_isWifiSync(0);
                }else{
                    db.update_isWifiSync(1);
                }
            }
        });
    }
    public class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                user_id = db.getUserId();
                JSONObject jsonObject = new JSONObject();

                HttpURLConnection con = null;
                BufferedReader reader = null;
                URL url = new URL(urls[0]);
                try {
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("DELETE");//POST방식으로 보냄
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

                    //서버로 부터 데이터를 받음
                    InputStreamReader stream = new InputStreamReader(con.getInputStream(), "UTF-8");

                    reader = new BufferedReader(stream);

                    StringBuffer buffer = new StringBuffer();

                    String line = "";

                    while ((line = reader.readLine()) != null) {//(중요)서버로부터 한줄씩 읽어서 문자가 없을때까지 넣어줌
                        buffer.append(line + "\n"); //읽어준 스트링값을 더해준다.
                    }
                    line = buffer.toString();
                    return line;//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임
                } catch (MalformedURLException e) {
                    e.printStackTrace();
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
            loadingDialog.progressOFF();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        loadingDialog.progressOFF();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        loadingDialog.progressOFF();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }
}
