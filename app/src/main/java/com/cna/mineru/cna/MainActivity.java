package com.cna.mineru.cna;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.cna.mineru.cna.Adapter.CustomViewPager;
import com.cna.mineru.cna.Adapter.FragmentPagerAdapter;
import com.cna.mineru.cna.DB.UserSQLClass;
import com.cna.mineru.cna.Fragment.GraphFragment;
import com.cna.mineru.cna.Fragment.HomeFragment;
import com.cna.mineru.cna.Fragment.PlanFragment;
import com.cna.mineru.cna.Fragment.ExamFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        db = new UserSQLClass(this);
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
        //return super.onCreateOptionsMenu(menu);
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


}
