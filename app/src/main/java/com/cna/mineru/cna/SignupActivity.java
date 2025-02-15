package com.cna.mineru.cna;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cna.mineru.cna.Utils.CustomDialog;
import com.cna.mineru.cna.Utils.LoadingDialog;
import com.cna.mineru.cna.Utils.SecurityUtil;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import static com.cna.mineru.cna.Utils.Network.getWhatKindOfNetwork.getWhatKindOfNetwork;

public class SignupActivity extends AppCompatActivity {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    private FirebaseUser mUser;
    private GoogleSignInClient googleSignInClient;

    private TextView btn_next;

    private AppCompatEditText et_pw;
    private AppCompatEditText et_email;
    private AppCompatEditText et_pw_again;

    private TextInputLayout layout_pw;
    private TextInputLayout layout_email;
    private TextInputLayout layout_pw_again;


    private LoadingDialog loadingDialog;

    private boolean isView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        isView = false;
        loadingDialog = new LoadingDialog();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        layout_pw = (TextInputLayout) findViewById(R.id.layout_pw);
        layout_email = (TextInputLayout) findViewById(R.id.layout_email);
        layout_pw_again = (TextInputLayout) findViewById(R.id.layout_pw_again);

        et_pw = (AppCompatEditText) findViewById(R.id.et_pw);
        et_email = (AppCompatEditText) findViewById(R.id.et_email);
        et_pw_again = (AppCompatEditText) findViewById(R.id.et_pw_again);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        TextView tv_subtitle = (TextView) findViewById(R.id.tv_subtitle);
        btn_next = (TextView) findViewById(R.id.btn_next);
        ImageView btn_back = (ImageView) findViewById(R.id.btn_back);
        ImageView iv_view = (ImageView) findViewById(R.id.iv_view);
        ImageView iv_check = (ImageView) findViewById(R.id.iv_check);

        iv_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isView){
                    et_pw.setInputType(0x00000081);
                    isView = false;
                    iv_view.setImageResource(R.drawable.ic_view);
                }
                else {
                    et_pw.setInputType(0x00000091);
                    isView = true;
                    iv_view.setImageResource(R.drawable.ic_hide);
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_next.setEnabled(false);
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 1000);
                String pw = et_pw.getText().toString();
                String pw_again = et_pw_again.getText().toString();
                if (pw.toString().equals(pw_again.toString())) {
                    if (isValidEmail() && isValidPasswd()) {
                        createUser(et_email.getText().toString(), et_pw.getText().toString());
                    }
                } else {
                    CustomDialog d = new CustomDialog(8);
                    d.show(getSupportFragmentManager(),"insert error");
                    d.setDialogResult(new CustomDialog.OnMyDialogResult() {
                        @Override
                        public void finish(int result) {
                            d.dismiss();
                        }

                        @Override
                        public void finish(int result, String email) {

                        }
                    });
                }

            }
        });

        et_pw_again.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if("NONE".equals(getWhatKindOfNetwork(this))) {
            CustomDialog dialog2 = new CustomDialog(4);
            dialog2.show(getSupportFragmentManager(),"network error");
            dialog2.setDialogResult(new CustomDialog.OnMyDialogResult() {
                @Override
                public void finish(int result) {
                    if(result==1){
                        Intent intentConfirm = new Intent();
                        intentConfirm.setAction("android.settings.WIFI_SETTINGS");
                        startActivity(intentConfirm);
                    }
                }

                @Override
                public void finish(int result, String email) {

                }
            });
        }
    }

    private boolean isValidEmail() {
        if (et_email.getText().toString().isEmpty()) {
            // 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(et_email.getText()).matches()) {
            // 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidPasswd() {
        if (et_pw.getText().toString().isEmpty()) {
            // 비밀번호 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(et_pw.getText()).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    private void createUser(String email, String password) {
        loadingDialog.progressON(this, "Loading...");
        new JSONTask().execute(getString(R.string.ip_set) + "/api/user/create");
    }

    @SuppressLint("StaticFieldLeak")
    public class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                String token = FirebaseInstanceId.getInstance().getToken();
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Name", "게스트");
                SecurityUtil securityUtil = new SecurityUtil();
                byte[] rtn1 = securityUtil.encryptSHA256(String.valueOf(et_pw.getText()));
                String pw = new String(rtn1);
                jsonObject.accumulate("Email", et_email.getText().toString());
                jsonObject.accumulate("Password", pw);
                jsonObject.accumulate("uuid", token);
                HttpURLConnection con = null;
                BufferedReader reader = null;
                URL url = new URL(urls[0]);
                try {
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Connection", "Keep-Alive");
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    //새로운 api 인증을 위한 코드
                    con.setRequestProperty("authorization","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJtYXN0ZXIiOiIxMjM0NSIsIkVtYWlsIjoiYWRtaW5AY25hcGx1cy5zaG9wIiwiaWF0IjoxNTcxNTgzMjY0LCJleHAiOjE2MDMxNDA4NjR9.NNGcd76WBD4C0cLq7Qh86fEV83snbT4ZwKa_LBVFse-J35YAYLS0OZRH3tnvXl9U9e2GfSAEOt4RI0SWzAbl9G3AD9tYNuKu5VXUS-DECCM-1VRQEWtRQeBUG5EqSY9Sqtnoue6MnOgPlFXXuTPxHpyv2HRhJO8YCQxwg-Py0rlbakN5QA6Kkmif6OkF-Kdi5Bz0B2fkaT5szCZxAMPi_TiJpuN6OzGp-XmIiVMMAcHxFuN-DMUaUBoRzTUsnoneI3oEq0PyPdOcQZZz8L5qbzzwyCu4LE4MtahWNhNC-kWs4wmQvFFvdOHt055tImVmwCbICm6ItV2dOGUQr_Ggtg");
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

                    StringBuilder buffer = new StringBuilder();

                    String line = "";

                    while ((line = reader.readLine()) != null) {//(중요)서버로부터 한줄씩 읽어서 문자가 없을때까지 넣어줌
                        buffer.append(line).append("\n"); //읽어준 스트링값을 더해준다.
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(error == 1){
                loadingDialog.progressOFF();
                Toast.makeText(SignupActivity.this, "이미 등록 된 계정입니다.", Toast.LENGTH_SHORT).show();
            }else{
                loadingDialog.progressOFF();
                Toast.makeText(SignupActivity.this, "인증 메일을 전송했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    private class splashHandler implements Runnable{
        public void run()	{
            btn_next.setEnabled(true); // 클릭 유효화
        }
    }
}
