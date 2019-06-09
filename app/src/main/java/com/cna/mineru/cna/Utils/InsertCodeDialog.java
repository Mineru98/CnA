package com.cna.mineru.cna.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cna.mineru.cna.R;

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

public class InsertCodeDialog extends DialogFragment {
    private OnMyDialogResult mDialogResult;
    private LoadingDialog loadingDialog;

    private EditText et_code;
    private TextView btn_ok;
    private TextView btn_cancel;

    private String coupon_code = "";
    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View dig = inflater.inflate(R.layout.insertcode_dialog, null);
        TextView tv_title = (TextView) dig.findViewById(R.id.tv_title);
        et_code = (EditText) dig.findViewById(R.id.et_code);
        btn_ok = (TextView) dig.findViewById(R.id.btn_ok);
        btn_cancel = (TextView) dig.findViewById(R.id.btn_cancel);

        loadingDialog = new LoadingDialog();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_code.getText().toString().equals("")){
                    Handler h = new Handler();
                    h.postDelayed(new splashHandler(), 2000);
                    if(et_code.getText().toString().equals(""))
                        et_code.setHintTextColor(0xFFD32F2F);
                }
                else{
                    loadingDialog.progressON(getActivity(),"Loading...");
                    coupon_code = et_code.getText().toString();
                    new JSONCode().execute(getString(R.string.ip_set)+"/api/coupon/check");
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertCodeDialog.this.dismiss();
            }
        });

        builder.setView(dig);
        return builder.create();
    }

    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult {
        void finish(int _class, String code, int tag);
    }

    @SuppressLint("StaticFieldLeak")
    public class JSONCode extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("coupon_code", coupon_code);
                HttpURLConnection con = null;
                BufferedReader reader = null;
                URL url = new URL(urls[0]);
                try {
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST 방식으로 보냄
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
                if (error == 2) {

                } else {
                    if(jObject.getInt("Result")==1){
                        InsertCodeDialog.this.dismiss();
                        int tag = jObject.getInt("coupon_tag");
                        mDialogResult.finish(1, coupon_code, tag);
                    }else{
                        mDialogResult.finish(0,"", 0);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            loadingDialog.progressOFF();
        }
    }

    private class splashHandler implements Runnable{
        public void run()	{
            btn_cancel.setEnabled(true); // 클릭 유효화
            btn_ok.setEnabled(true); // 클릭 유효화
            et_code.setHintTextColor(0xFFDBDBDB);
        }
    }
}
