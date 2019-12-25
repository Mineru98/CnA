package com.cna.mineru.cna.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cna.mineru.cna.AddNote;
import com.cna.mineru.cna.DB.ExamSQLClass;
import com.cna.mineru.cna.DB.GraphSQLClass;
import com.cna.mineru.cna.DB.HomeSQLClass;
import com.cna.mineru.cna.DB.ImageSQLClass;
import com.cna.mineru.cna.DB.TmpSQLClass;
import com.cna.mineru.cna.DB.UserSQLClass;
import com.cna.mineru.cna.LoginActivity;
import com.cna.mineru.cna.R;
import com.cna.mineru.cna.Utils.CustomDialog;
import com.cna.mineru.cna.Utils.InsertCodeDialog;
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

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.cna.mineru.cna.Utils.Network.getWhatKindOfNetwork.getWhatKindOfNetwork;


public class ProfileFragment extends Fragment {

    private FirebaseUser mFirebaseUser;

    private UserSQLClass db;

    private LoadingDialog loadingDialog;

    private int user_id;
    private boolean isLogin;

    public ProfileFragment(){

    }

    @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = new UserSQLClass(getContext());
        isLogin = true;
        loadingDialog = new LoadingDialog();
        TextView tv_ver = (TextView) view.findViewById(R.id.tv_ver);
        TextView tv_str_ver = (TextView) view.findViewById(R.id.tv_str_ver);

        PackageInfo packageInfo = null;
        try{
            packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }

        tv_str_ver.setText("현재 버전: "+packageInfo.versionName);
        SpannableString content = new SpannableString("Application");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv_ver.setText(content);

        TextView tv_abouts = (TextView) view.findViewById(R.id.tv_abouts);
        TextView tv_terms = (TextView) view.findViewById(R.id.tv_terms);
        TextView tv_privacy = (TextView) view.findViewById(R.id.tv_privacy);
        content = new SpannableString("Abouts");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv_abouts.setText(content);

        TextView tv_account = (TextView) view.findViewById(R.id.tv_account);
        TextView tv_profile = (TextView) view.findViewById(R.id.tv_profile);
        TextView tv_insert_code = (TextView) view.findViewById(R.id.tv_insert_code);
        TextView tv_logout = (TextView) view.findViewById(R.id.tv_logout);
        TextView tv_signout = (TextView) view.findViewById(R.id.tv_signout);
        content = new SpannableString("Account");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv_account.setText(content);

        TextView tv_setting = (TextView) view.findViewById(R.id.tv_setting);
        TextView tv_wifisync = (TextView) view.findViewById(R.id.tv_wifisync);
        Switch sw_wifisync = (Switch) view.findViewById(R.id.sw_wifisync);
        content = new SpannableString("Setting");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv_setting.setText(content);

        if(!db.getWifiSync()){
            sw_wifisync.setChecked(true);
        }else{
            sw_wifisync.setChecked(false);
        }

        tv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), WebViewActivity.class);
                i.putExtra("value","policy/terms");
                startActivity(i);
            }
        });

        tv_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), WebViewActivity.class);
                i.putExtra("value","policy/privacy");
                startActivity(i);
            }
        });

        tv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //프로필 설정 Activity
                CustomDialog d = new CustomDialog(16);
                d.show(getActivity().getSupportFragmentManager(),"chang name");
                d.setDialogResult(new CustomDialog.OnMyDialogResult() {
                    @Override
                    public void finish(int result) {

                    }

                    @Override
                    public void finish(int result, String email) {
                        if(result==1){
                            db.update_name(email);
                            //server에도 변경 코드 작성 필요
                        }
                    }
                });
            }
        });

        tv_insert_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.isCoupon()){
                    CustomDialog d = new CustomDialog(7);
                    d.show(getActivity().getSupportFragmentManager(),"add coupon");
                    d.setDialogResult(new CustomDialog.OnMyDialogResult() {
                        @Override
                        public void finish(int _class) {
                            if(_class==1){
                                d.dismiss();
                                InsertCodeDialog dialog = new InsertCodeDialog();
                                dialog.show(getActivity().getSupportFragmentManager(),"insert code");
                                dialog.setDialogResult(new InsertCodeDialog.OnMyDialogResult() {
                                    @Override
                                    public void finish(int _class, String code, int tag) {
                                        if(_class==1){
                                            Toast.makeText(getContext(), "인증되었습니다.", Toast.LENGTH_SHORT).show();
                                            db.update_isPremium(1);
                                            db.addCoupon(tag);
                                        }
                                        else{
                                            Toast.makeText(getContext(), "코드가 일치하지 않습니다..", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else
                                d.dismiss();
                        }

                        @Override
                        public void finish(int result, String email) {

                        }
                    });
                }else{
                    InsertCodeDialog dialog = new InsertCodeDialog();
                    dialog.show(getActivity().getSupportFragmentManager(),"insert code");
                    dialog.setDialogResult(new InsertCodeDialog.OnMyDialogResult() {
                        @Override
                        public void finish(int _class, String code, int tag) {
                            if(_class==1){
                                Toast.makeText(getContext(), "인증되었습니다.", Toast.LENGTH_SHORT).show();
                                db.update_isPremium(1);
                                db.setCouponCode(code);
                            }
                            else{
                                Toast.makeText(getContext(), "코드가 일치하지 않습니다..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog2 = new CustomDialog(1);
                dialog2.show(getActivity().getSupportFragmentManager(),"logout code");
                dialog2.setDialogResult(new CustomDialog.OnMyDialogResult() {
                    @Override
                    public void finish(int result) {
                        if(result==1){
                            loadingDialog.progressON(getActivity(),"Loading...");
                            isLogin=false;
                            SharedPreferences pref = getContext().getSharedPreferences("isLogin", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("isLogin",false);
                            editor.apply();
                            reset_app();
                            FirebaseAuth.getInstance().signOut();
                            Intent resultIntent = new Intent(getActivity(), LoginActivity.class);
                            resultIntent.putExtra("isLogin",isLogin);
                            getActivity().setResult(RESULT_OK,resultIntent);
                            getActivity().finish();
                            startActivity(resultIntent);
                        }
                    }

                    @Override
                    public void finish(int result, String email) {

                    }
                });
            }
        });

        tv_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog2 = new CustomDialog(2);
                dialog2.show(getActivity().getSupportFragmentManager(),"logout code");
                dialog2.setDialogResult(new CustomDialog.OnMyDialogResult() {
                    @Override
                    public void finish(int result) {
                        if(result==1){
                            loadingDialog.progressON(getActivity(),"Loading...");
                            isLogin=false;
                            SharedPreferences pref = getContext().getSharedPreferences("isLogin",MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("isLogin",false);
                            editor.apply();
                            reset_app();
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

                            Intent resultIntent = new Intent(getActivity(), LoginActivity.class);
                            resultIntent.putExtra("isLogin",isLogin);
                            getActivity().setResult(RESULT_OK,resultIntent);
                            getActivity().finish();
                            startActivity(resultIntent);
                        }
                    }

                    @Override
                    public void finish(int result, String email) {

                    }
                });
            }
        });

        sw_wifisync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    db.update_isWifiSync(0);
                }else{
                    db.update_isWifiSync(1);
                }
            }
        });
        return view;
    }
    @SuppressLint("StaticFieldLeak")
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
                    con.setRequestMethod("DELETE");//DELETE방식으로 보냄
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

    private void reset_app(){
        TmpSQLClass db1 = new TmpSQLClass(getContext());
        UserSQLClass db2 = new UserSQLClass(getContext());
        ExamSQLClass db3 = new ExamSQLClass(getContext());
        HomeSQLClass db4 = new HomeSQLClass(getContext());
        GraphSQLClass db5 = new GraphSQLClass(getContext());
        ImageSQLClass db6 = new ImageSQLClass(getContext());

        db1.reset_app();
        db2.reset_app();
        db3.reset_app();
        db4.reset_app();
        db5.reset_app();
        db6.reset_app();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        loadingDialog.progressOFF();
    }
}
