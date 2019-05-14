package com.cna.mineru.cna;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cna.mineru.cna.DB.UserSQLClass;
import com.cna.mineru.cna.Utils.LoadingDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

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
import java.util.Objects;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");
    public static final String WIFI_STATE = "WIFE";
    public static final String MOBILE_STATE = "MOBILE";
    public static final String NONE_STATE = "NONE";
    private static final int RC_SIGN_IN = 900;

    private UserSQLClass db;

    private EditText et_pw;
    private EditText et_email;

    private Button btn_signup;
    private Button btn_login_email;

    private LoadingDialog loadingDialog;

    private FirebaseAuth mAuth;
    private SignInButton btn_login_google;
    private GoogleSignInClient googleSignInClient;

    private boolean isView;
    private boolean isVerified;

    private String token = "";
    private String name ="게스트";
    private String google_email = "";
    private String google_name = "게스트";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new UserSQLClass(LoginActivity.this);

        et_email = (EditText) findViewById(R.id.et_email);
        et_pw = (EditText) findViewById(R.id.et_pw);
        TextView tv_lose_pw = (TextView) findViewById(R.id.tv_lose_pw);
        TextView tv_or = (TextView) findViewById(R.id.tv_or);
        btn_login_email = (Button) findViewById(R.id.btn_login_email);
        btn_login_google = (SignInButton) findViewById(R.id.btn_login_google);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        ImageView iv_view = (ImageView) findViewById(R.id.iv_view);
        ImageView imageView = (ImageView) findViewById(R.id.img_view);

        isVerified = false;
        isView = false;
        loadingDialog = new LoadingDialog();
        mAuth = FirebaseAuth.getInstance();
        SpannableString content = new SpannableString("비밀번호를 잃어버리셨나요?");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0); tv_lose_pw.setText(content);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
            }
        });

        iv_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isView){
                    et_pw.setInputType(0x00000081);
                    isView = false;
                }
                else {
                    et_pw.setInputType(0x00000091);
                    isView = true;
                }
            }
        });

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        tv_lose_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.progressON(LoginActivity.this,"Loaing...");
                Toast.makeText(LoginActivity.this, "비밀번호 재설정을 위하여 이메일을 보냈습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        btn_login_email.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(et_email.getText().toString().equals("")||et_pw.getText().toString().equals("")){
                    Handler h = new Handler();
                    h.postDelayed(new splashHandler(), 2000);
                    if(et_email.getText().toString().equals(""))
                        et_email.setHintTextColor(0xFFD32F2F);
                    if(et_pw.getText().toString().equals(""))
                        et_pw.setHintTextColor(0xFFD32F2F);
                }else{
                    btn_login_email.setEnabled(false);
                    Handler h = new Handler();
                    h.postDelayed(new splashHandler(), 1000);
                    loadingDialog.progressON(LoginActivity.this,"Loading...");
                    if(isValidPasswd()&&isValidEmail()){
                        loginUser();
                    }
                }
            }
        });

        btn_login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_login_google.setEnabled(false);
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 1000);
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_signup.setEnabled(false);
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 1000);
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });

        if("NONE".equals(getWhatKindOfNetwork(this))) {
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
        }
    }

    private boolean isValidEmail() {
        if (et_email.getText().toString().isEmpty()) {
            // 이메일 공백
            return false;
        } else return Patterns.EMAIL_ADDRESS.matcher(et_email.getText()).matches();
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

    private void loginUser() {
        new JSONTask().execute(getString(R.string.ip_set)+"/api/user/signin");
    }

    private class splashHandler implements Runnable{
        public void run()	{
            btn_login_email.setEnabled(true); // 클릭 유효화
            btn_login_google.setEnabled(true); // 클릭 유효화
            btn_signup.setEnabled(true); // 클릭 유효화
            et_email.setHintTextColor(0xFF505050);
            et_pw.setHintTextColor(0xFF505050);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 구글로그인 버튼 응답
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // 구글 로그인 성공
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException ignored) {
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            google_email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                            google_name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                            new GoogleJSONTask().execute(getString(R.string.ip_set)+"/api/user/google");
                        } else {
                            // 로그인 실패
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @SuppressLint("StaticFieldLeak")
    public class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", et_email.getText().toString());
                jsonObject.accumulate("password", et_pw.getText().toString());
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
            boolean isGoogle = false;
            int id = 1;
            int error = 0;
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(result);
                error = jObject.optInt("error");
                if(error==2){
                    isVerified= false;
                }else{
                    isGoogle = jObject.optBoolean("isGoogle");
                    name = jObject.getString("name");
                    id = jObject.getInt("id");
                    isVerified =  jObject.optBoolean("isVerified");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(isVerified){
                db.update_isGoogle(isGoogle,name,id);
                loadingDialog.progressOFF();
                SharedPreferences pref = getSharedPreferences("isLogin", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("isLogin", true);
                editor.apply();
                if(db.getFirst()==0){
                    startActivity(new Intent(getApplication(), MainActivity.class));
                }else{
                    startActivity(new Intent(getApplication(), MainActivity.class));
//                    startActivity(new Intent(getApplication(), DefaultInputActivity.class));
                }
                LoginActivity.this.finish();
                //overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }else{
                loadingDialog.progressOFF();
                Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호가 다시 확인하세요.\n" +
                        "등록되지 않은 이메일이거나 이메일 또는 비밀번호를 잘못입력하셨습니다..", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class GoogleJSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email",google_email);
                jsonObject.accumulate("name",google_name);
                jsonObject.accumulate("isGoogle",true);
                jsonObject.accumulate("isVerified",true);
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
            boolean isGoogle = false;
            int id = 1;
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(result);
                isGoogle = jObject.optBoolean("isGoogle");
                name = jObject.getString("name");
                id = jObject.getInt("id");
                isVerified = jObject.optBoolean("isVerified");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (isVerified) {
                db.update_isGoogle(isGoogle, name, id);
                loadingDialog.progressOFF();
                SharedPreferences pref = getSharedPreferences("isLogin", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("isLogin", true);
                editor.apply();
                if(db.getFirst()==0){
                    startActivity(new Intent(getApplication(), MainActivity.class));
                }else{
                    startActivity(new Intent(getApplication(), MainActivity.class));
//                    startActivity(new Intent(getApplication(), DefaultInputActivity.class));
                }
                LoginActivity.this.finish();
                //overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            } else {
                loadingDialog.progressOFF();
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
}
