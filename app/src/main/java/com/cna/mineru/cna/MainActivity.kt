package com.cna.mineru.cna

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity

import com.cna.mineru.cna.Adapter.CustomViewPager
import com.cna.mineru.cna.Adapter.FragmentPagerAdapter
import com.cna.mineru.cna.DB.NotiSQLClass
import com.cna.mineru.cna.DB.TmpSQLClass
import com.cna.mineru.cna.DB.UserSQLClass
import com.cna.mineru.cna.Fragment.ExamFragment
import com.cna.mineru.cna.Fragment.GraphFragment
import com.cna.mineru.cna.Fragment.HomeFragment
import com.cna.mineru.cna.Fragment.ProfileFragment
import com.cna.mineru.cna.Utils.BottomNavigationViewHelper
import com.cna.mineru.cna.Utils.CustomDialog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

import java.io.File
import java.util.ArrayList

import com.cna.mineru.cna.Utils.Network.getWhatKindOfNetwork.getWhatKindOfNetwork
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var prevMenuItem: MenuItem? = null

    private var viewPager: CustomViewPager? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var db: UserSQLClass? = null

    internal var permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {}

        override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
            Toast.makeText(this@MainActivity, "권한 요청 거절\n$deniedPermissions", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mToolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)

        viewPager = findViewById(R.id.view_pager) as CustomViewPager
        bottomNavigationView = findViewById(R.id.navigation) as BottomNavigationView
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView!!)
        //
        //        if("NONE".equals(getWhatKindOfNetwork(this))) {
        //            CustomDialog dialog2 = new CustomDialog(4);
        //            dialog2.show(getSupportFragmentManager(),"network error");
        //            dialog2.setDialogResult(new CustomDialog.OnMyDialogResult() {
        //                @Override
        //                public void finish(int result) {
        //                    if(result==1){
        //                        Intent intentConfirm = new Intent();
        //                        intentConfirm.setAction("android.settings.WIFI_SETTINGS");
        //                        startActivity(intentConfirm);
        //                    }
        //                }
        //
        //                @Override
        //                public void finish(int result, String email) {
        //
        //                }
        //            });
        //        }

        db = UserSQLClass(this) // User_Info Table
        val t_db = TmpSQLClass(this) // Tmp Table
        val n_db = NotiSQLClass(this)// Notificaiton Table

        run {
            Toast.makeText(this, "환영합니다. " + db!!._Name + " 학생님.", Toast.LENGTH_SHORT).show()
            TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("서비스 이용에 제약이 있을 수 있습니다.\n\n하지만 [설정] > [권한]에서 권한을 허용할 수 있어요.")
                .setPermissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .check()

            @SuppressLint("SdCardPath")
            val outDir = File("/sdcard/CnA")
            if (!outDir.exists())
                outDir.mkdirs()
        }

        bottomNavigationView!!.setOnNavigationItemSelectedListener(
            object : BottomNavigationView.OnNavigationItemSelectedListener {
                override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.navigation_home -> viewPager!!.setCurrentItem(0, false)
                        R.id.navigation_graph -> viewPager!!.setCurrentItem(1, false)
                        R.id.navigation_test -> viewPager!!.setCurrentItem(2, false)
                        R.id.navigation_profile -> viewPager!!.setCurrentItem(3, false)
                    }
                    return false
                }
            })

        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null) {
                    prevMenuItem!!.isChecked = false
                } else {
                    bottomNavigationView!!.getMenu().getItem(0).setChecked(false)
                }
                bottomNavigationView!!.getMenu().getItem(position).setChecked(true)
                prevMenuItem = bottomNavigationView!!.getMenu().getItem(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        setupViewPager(viewPager!!)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = FragmentPagerAdapter(getSupportFragmentManager())
        val homeFragment = HomeFragment()
        val graphFragment = GraphFragment()
        val examFragment = ExamFragment()
        val profileFragment = ProfileFragment()
        adapter.addFragment(homeFragment)
        adapter.addFragment(graphFragment)
        adapter.addFragment(examFragment)
        adapter.addFragment(profileFragment)
        viewPager.setAdapter(adapter)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_notifications -> {
                val i = Intent(this, NotificationActivity::class.java)
                startActivityForResult(i, 3000)
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                3000 -> {
                    val isLogin = data?.getBooleanExtra("isLogin", true)
                    if (!isLogin!!) {
                        val i = Intent(this, LoginActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if ("NONE" == getWhatKindOfNetwork(this)) {
            val dialog2 = CustomDialog(4)
            dialog2.show(getSupportFragmentManager(), "network error")
            dialog2.setDialogResult(object : CustomDialog.OnMyDialogResult {
                override fun finish(result: Int) {
                    if (result == 1) {
                        val intentConfirm = Intent()
                        intentConfirm.action = "android.settings.WIFI_SETTINGS"
                        startActivity(intentConfirm)
                    }
                }

                override fun finish(result: Int, email: String) {

                }
            })
        }
    }
}
