package edu.yezh.datatrafficmanager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import edu.yezh.datatrafficmanager.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean bool = getPermission();
        Log.e("PermissionNetworkStats", String.valueOf(bool));

        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        /*FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*//*

            }
        });*/
    }

    public boolean getPermission() {
        if (ContextCompat.checkSelfPermission((Context) this, "android.permission.READ_PHONE_STATE") != 0 && !ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, "android.permission.READ_PHONE_STATE"))
            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{"android.permission.PACKAGE_USAGE_STATS", "android.permission.READ_PHONE_STATE", "android.permission.ACCESS_NETWORK_STATE"},
                    1);
        return true;
    }
}