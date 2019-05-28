package com.cna.mineru.cna.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.cna.mineru.cna.AddNote;
import com.cna.mineru.cna.DB.GraphSQLClass;
import com.cna.mineru.cna.DB.HomeSQLClass;
import com.cna.mineru.cna.DB.ImageSQLClass;
import com.cna.mineru.cna.DB.UserSQLClass;
import com.cna.mineru.cna.DTO.HomeData;
import com.cna.mineru.cna.Adapter.GridAdapter;
import com.cna.mineru.cna.ModifyHomeItem;
import com.cna.mineru.cna.R;
import com.cna.mineru.cna.Utils.DefaultInputDialog;
import com.cna.mineru.cna.Utils.LoadingDialog;

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
import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private HomeSQLClass db;
    private GraphSQLClass gp_db;
    private ImageSQLClass i_db;
    private UserSQLClass u_db;

    private GridView gv;
    private GridAdapter mAdapater;
    private FloatingActionButton fb;
    private LoadingDialog loadingDialog;

    private int note_id = 0;

    private ArrayList<HomeData> list  = new ArrayList<>();

    public HomeFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fb = (FloatingActionButton)view.findViewById(R.id.fb_add);
        db = new HomeSQLClass(getActivity());
        u_db= new UserSQLClass(getActivity());
        gp_db = new GraphSQLClass(getActivity());
        i_db = new ImageSQLClass((getActivity()));
        loadingDialog = new LoadingDialog();
        mAdapater = new GridAdapter(getContext(), R.layout.row, list);

        list = db.load_values();

        gv = (GridView)view.findViewById(R.id.gridView1);
        gv.setAdapter(mAdapater);

        fb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v){
                if(!u_db.isClassChecked()){
                    DefaultInputDialog d = new DefaultInputDialog();
                    d.show(getActivity().getSupportFragmentManager(),"setting");
                    d.setDialogResult(new DefaultInputDialog.OnMyDialogResult() {
                        @Override
                        public void finish(int _class) {
                            DefaultInputDialog d2 = new DefaultInputDialog();
                            d2.show(getActivity().getSupportFragmentManager(),"setting2");
                            d2.setDialogResult(new DefaultInputDialog.OnMyDialogResult() {
                                @Override
                                public void finish(int _class) {

                                }
                            });
                        }
                    });
                }else{
                    list.clear();
                    fb.setEnabled(false);
                    Handler h = new Handler();
                    h.postDelayed(new splashHandler(), 1000);
                    Intent i =new Intent(getActivity(),AddNote.class);
                    startActivity(i);
                }
            }
        });

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadingDialog.progressON(getActivity(),"Loading...");
                gv.setEnabled(false);
                Handler h = new Handler();
                h.postDelayed(new splashHandler(), 1000);

                HomeData data;
                data = db.select_item(list.get(position).id);
                Intent i = new Intent(getActivity(),ModifyHomeItem.class);
                i.putExtra("id", data.id);
                i.putExtra("title", data.title_text);
                i.putExtra("tag", data.tag);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });

        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                try{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("오답노트 삭제");
                    builder.setMessage("정말로 삭제하시겠습니까?");
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    db.delete_item(list.get(position).id);
                                    gp_db.delete_value(list.get(position).id);
                                    i_db.delete_item(list.get(position).id);
                                    note_id = list.get(position).id;
                                    new DelNote().execute(getString(R.string.ip_set)+"/api/note/destroy");
                                    onResume();
                                }
                            });
                    builder.setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();

                }
                catch (Exception e) {
                }
                return true;
            }
        });
        return view;
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

    private class splashHandler implements Runnable{
        public void run()	{
            fb.setEnabled(true); // 클릭 유효화
            gv.setEnabled(true);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        list = db.load_values();
        mAdapater = new GridAdapter(getContext(), R.layout.row, list);
        gv.setAdapter(mAdapater);
    }

    @Override
    public void onStop(){
        super.onStop();
        loadingDialog.progressOFF();
    }
}
