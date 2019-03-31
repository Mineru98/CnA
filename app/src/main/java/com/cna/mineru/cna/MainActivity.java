package com.cna.mineru.cna;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.cna.mineru.cna.Adapter.CustomViewPager;
import com.cna.mineru.cna.Adapter.FragmentPagerAdapter;
import com.cna.mineru.cna.DB.ClassSQLClass;
import com.cna.mineru.cna.DB.TmpSQLClass;
import com.cna.mineru.cna.DB.UserSQLClass;
import com.cna.mineru.cna.Fragment.GraphFragment;
import com.cna.mineru.cna.Fragment.HomeFragment;
import com.cna.mineru.cna.Fragment.PlanFragment;
import com.cna.mineru.cna.Fragment.ExamFragment;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private CustomViewPager viewPager;
    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment;
    PlanFragment planFragment;
    GraphFragment graphFragment;
    ExamFragment examFragment;
    MenuItem prevMenuItem;

    UserSQLClass db;
    TmpSQLClass t_db;
    ClassSQLClass c_db;
    public static final String CONNECTION_CONFIRM_CLIENT_URL = "http://clients3.google.com/generate_204";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if(!isOnline()){ //인터넷 연결 상태에 따라 오프라인 모드, 온라인 모드로 전환하기 위한 콛
            android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        db = new UserSQLClass(this); // User_Info Table
        c_db = new ClassSQLClass(this); // Class Table
        t_db = new TmpSQLClass(this); // Tmp Table

        {
            Toast.makeText(this, "환영합니다. "+db.get_Name() +" 학생님.", Toast.LENGTH_SHORT).show();
            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("서비스 이용에 제약이 있을 수 있습니다.\n\n하지만 [설정] > [권한]에서 권한을 허용할 수 있어요.")
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    .check();

            @SuppressLint("SdCardPath")
            File outDir = new File("/sdcard/CnA");
            if(!outDir.exists())
                outDir.mkdirs();
        }
        viewPager = (CustomViewPager) findViewById(R.id.view_pager);

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                viewPager.setCurrentItem(0,false);
                                break;
                            case R.id.navigation_plan:
                                viewPager.setCurrentItem(1,false);
                                break;
                            case R.id.navigation_graph:
                                viewPager.setCurrentItem(2,false);
                                break;
                            case R.id.navigation_test:
                                viewPager.setCurrentItem(3,false);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager());
        homeFragment = new HomeFragment();
        planFragment = new PlanFragment();
        graphFragment = new GraphFragment();
        examFragment = new ExamFragment();
        adapter.addFragment(homeFragment);
        adapter.addFragment(planFragment);
        adapter.addFragment(graphFragment);
        adapter.addFragment(examFragment);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this,SettingActivity.class);
                startActivityForResult(i,3000);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                i.putExtra("isLogin",true);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(MainActivity.this, "권한 요청 거절\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 3000:
                    boolean isLogin = data.getBooleanExtra("isLogin",true);
                    if(!isLogin){
                        Intent i = new Intent(this,LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                    break;
            }
        }
    }

    private static class CheckConnect extends Thread{
        private boolean success;
        private String host;

        CheckConnect(String host){
            this.host = host;
        }

        @Override
        public void run() {

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection)new URL(host).openConnection();
                conn.setRequestProperty("User-Agent","Android");
                conn.setConnectTimeout(1000);
                conn.connect();
                int responseCode = conn.getResponseCode();
                if(responseCode == 204) success = true;
                else success = false;
            }
            catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
            if(conn != null){
                conn.disconnect();
            }
        }

        public boolean isSuccess(){
            return success;
        }

    }

    public static boolean isOnline() {
        CheckConnect cc = new CheckConnect(CONNECTION_CONFIRM_CLIENT_URL);
        cc.start();
        try{
            cc.join();
            return cc.isSuccess();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
