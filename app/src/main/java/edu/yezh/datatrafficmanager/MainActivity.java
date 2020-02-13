package edu.yezh.datatrafficmanager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.List;

import edu.yezh.datatrafficmanager.ui.main.SectionsPagerAdapter;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!hasPermission()) {
            System.out.println("权限情况:"+hasPermission());
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("需要一些必要权限");
            dialog.setMessage("本应用需要您的一些手动操作用于获取权限访问本机已安装应用的信息\n\n首先打开系统\"设置\"\n进入\"安全\"设置页\n寻找\"使用情况访问权限\"设置项\n给予本应用\"使用情况访问权限\"\n然后点击确定");
            dialog.setCancelable(false);    //设置是否可以通过点击对话框外区域或者返回按键关闭对话框
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (hasPermission()) {
                        System.out.println("权限情况:"+"挡下了确定按钮");
                    System.out.println("权限情况:"+hasPermission());
                    Toast.makeText(MainActivity.this, "确定", Toast.LENGTH_SHORT).show();
                    getPermission();
                    //initial();
                    }else {
                        final AlertDialog.Builder dialog1 = new AlertDialog.Builder(MainActivity.this);
                        dialog1.setTitle("似乎没有成功获取权限");
                        dialog1.setMessage("");
                        dialog1.setCancelable(false);
                        dialog1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               finish();
                            }
                        });
                        dialog1.show();
                    }
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "取消", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            dialog.show();
        }else{
            System.out.println("权限情况:"+hasPermission());
            getPermission();
            //initial();
        }
    }
    public void initial(){

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        //toolbar.inflateMenu(R.menu.main_menu);

        toolbar.setNavigationIcon(R.drawable.ic_action_menu);
          final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
          final NavigationView navigationView = (NavigationView)findViewById(R.id.navigationView);
          System.out.println("drawerLayout是否为空?"+(drawerLayout==null));
        System.out.println("navigationView是否为空?"+(navigationView==null));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawerLayout.isDrawerOpen(navigationView)){
                    drawerLayout.closeDrawer(navigationView);


                }else{

                    drawerLayout.openDrawer(navigationView);

                }

                System.out.println("drawerLayout是否为空?"+String.valueOf(drawerLayout==null));
                System.out.println("navigationView是否为空?"+(navigationView==null));
            }
        });
    }
    public void getPermission() {
        /*if (ContextCompat.checkSelfPermission((Context) this, "android.permission.READ_PHONE_STATE") != 0 && !ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, "android.permission.READ_PHONE_STATE"))
        {    ActivityCompat.requestPermissions((Activity) this,
                    new String[]{"android.permission.PACKAGE_USAGE_STATS", "android.permission.READ_PHONE_STATE", "android.permission.ACCESS_NETWORK_STATE"},
                    1);
        }
        return true;*/
        /*boolean hasPM = ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED
                &&   ContextCompat.checkSelfPermission(this, "android.permission.PACKAGE_USAGE_STATS") == PackageManager.PERMISSION_GRANTED
                &&   ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_NETWORK_STATE") == PackageManager.PERMISSION_GRANTED
                &&   ContextCompat.checkSelfPermission(this, "android.permission.READ_NETWORK_USAGE_HISTORY") == PackageManager.PERMISSION_GRANTED
                ;
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_PHONE_STATE"}, 1);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.PACKAGE_USAGE_STATS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.PACKAGE_USAGE_STATS"}, 1);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.ACCESS_NETWORK_STATE"}, 1);
        }*/
        /*if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_NETWORK_USAGE_HISTORY) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_NETWORK_USAGE_HISTORY"}, 1);
        }*/
           /* String[] permissions = new String[]{"android.permission.PACKAGE_USAGE_STATS",
                    "android.permission.READ_PHONE_STATE", "android.permission.ACCESS_NETWORK_STATE","android.permission.READ_NETWORK_USAGE_HISTORY"};
            ActivityCompat.requestPermissions((Activity) this, permissions,1);*/
            System.out.println("获取其他权限");

        String PERMISSION_STORAGE_MSG = "请授予权限，否则影响部分使用功能";
        int PERMISSION_STORAGE_CODE = 10001;
        String[] PERMS = {Manifest.permission.READ_PHONE_STATE};
        if (EasyPermissions.hasPermissions(this, PERMS)) {
            // 已经申请过权限，做想做的事
            initial();
        } else {
            // 没有申请过权限，现在去申请
            /**
             *@param host Context对象
             *@param rationale  权限弹窗上的提示语。
             *@param requestCode 请求权限的唯一标识码
             *@param perms 一系列权限
             */
            EasyPermissions.requestPermissions(this, PERMISSION_STORAGE_MSG, PERMISSION_STORAGE_CODE, PERMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将结果转发给EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        new AppSettingsDialog
                .Builder(this)
                .setTitle("提示！")
                .setRationale("请到设置授予权限，否则影响部分功能使用。")
                .build()
                .show();
    }
    @AfterPermissionGranted(10001)
    public void success(){
        Toast.makeText(this, "哈哈哈哈哈哈成功了", Toast.LENGTH_SHORT).show();
        initial();
    }

    //检测用户是否对本app开启了“Apps with usage access”权限
    private boolean hasPermission() {
        AppOpsManager appOpsM = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOpsM.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }
}