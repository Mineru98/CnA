package com.cna.mineru.cna;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.cna.mineru.cna.DB.NotiSQLClass;
import com.cna.mineru.cna.DB.TmpSQLClass;
import com.cna.mineru.cna.DB.UserSQLClass;
import com.cna.mineru.cna.Fragment.GraphFragment;
import com.cna.mineru.cna.Fragment.HomeFragment;
import com.cna.mineru.cna.Fragment.ExamFragment;
import com.cna.mineru.cna.Fragment.ProfileFragment;
import com.cna.mineru.cna.Utils.BottomNavigationViewHelper;
import com.cna.mineru.cna.Utils.DefaultInputDialog;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.io.File;
import java.util.ArrayList;

import static com.cna.mineru.cna.Utils.Network.getWhatKindOfNetwork.getWhatKindOfNetwork;

public class MainActivity extends AppCompatActivity {
    private MenuItem prevMenuItem;

    private CustomViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private UserSQLClass db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        viewPager = (CustomViewPager) findViewById(R.id.view_pager);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

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

        db = new UserSQLClass(this); // User_Info Table
        TmpSQLClass t_db = new TmpSQLClass(this); // Tmp Table
        NotiSQLClass n_db = new NotiSQLClass(this);// Notificaiton Table

        {
            Toast.makeText(this, "환영합니다. "+ db.get_Name() +" 학생님.", Toast.LENGTH_SHORT).show();
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

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                viewPager.setCurrentItem(0,false);
                                break;
                            case R.id.navigation_graph:
                                viewPager.setCurrentItem(1,false);
                                break;
                            case R.id.navigation_test:
                                viewPager.setCurrentItem(2,false);
                                break;
                            case R.id.navigation_profile:
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
                else {
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
        HomeFragment homeFragment = new HomeFragment();
        GraphFragment graphFragment = new GraphFragment();
        ExamFragment examFragment = new ExamFragment();
        ProfileFragment profileFragment = new ProfileFragment();
        adapter.addFragment(homeFragment);
        adapter.addFragment(graphFragment);
        adapter.addFragment(examFragment);
        adapter.addFragment(profileFragment);
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
            case R.id.action_notifications:
                Intent i = new Intent(this,NotificationActivity.class);
                startActivityForResult(i,3000);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
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

    @Override
    protected void onResume(){
        super.onResume();
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
}
