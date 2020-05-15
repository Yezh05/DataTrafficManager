package edu.yezh.datatrafficmanager.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.ui.adapter.RecyclerViewWifiInfoAdapter;

import java.util.*;

public class ShowWifiInfoActivity extends AppCompatActivity {
    Context context;
    int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_wifi_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        context=getApplicationContext();

        registerPermission();

    }
    void handleWifiInfo(){
        List<ScanResult> wifiList = getWifiList();

        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String nowSSID = wifiInfo.getSSID();
        if (nowSSID.length() > 2 && nowSSID.charAt(0) == '"' && nowSSID.charAt(nowSSID.length() - 1) == '"') {
            nowSSID = nowSSID.substring(1, nowSSID.length() - 1);
        }
        int nowSSIDFer = wifiInfo.getFrequency();
        int nowSSIDLevel = WifiManager.calculateSignalLevel( wifiInfo.getRssi(),5);
        //System.out.println("当前wifi-SSID"+ nowSSID);
        ImageView ImageViewWifiIcon = findViewById(R.id.ImageViewWifiIcon);
        TextView TextViewNowSSID = findViewById(R.id.TextViewNowSSID),TextViewNowSignal= findViewById(R.id.TextViewNowSignal),TextViewNowLinkSpeed= findViewById(R.id.TextViewNowLinkSpeed),TextViewNowMAC= findViewById(R.id.TextViewNowMAC);
        TextViewNowSSID.setText(nowSSID);TextViewNowLinkSpeed.setText(wifiInfo.getLinkSpeed()+"Mbps");
        TextViewNowMAC.setText(wifiInfo.getBSSID());
        switch (nowSSIDLevel){
            case 0:
            case 1: TextViewNowSignal.setText("信号弱"); TextViewNowSignal.setTextColor(Color.parseColor("#FFA500")); ImageViewWifiIcon.setImageResource(R.drawable.ic_wifi_bad); break;
            case 2: TextViewNowSignal.setText("信号中"); TextViewNowSignal.setTextColor(Color.parseColor("#F4A460")); ImageViewWifiIcon.setImageResource(R.drawable.ic_wifi_normal); break;
            case 3:
            case 4: TextViewNowSignal.setText("信号强"); TextViewNowSignal.setTextColor(Color.parseColor("#3CB371")); ImageViewWifiIcon.setImageResource(R.drawable.ic_wifi_full); break;
        }
        TextView TextViewWifiChannelIsJam =findViewById(R.id.TextViewWifiChannelIsJam);
        int jamFlag=0;
        for (int i=0;i<wifiList.size();i++){
            if (nowSSIDFer == wifiList.get(i).frequency && (!nowSSID.equals(wifiList.get(i).SSID))){
                jamFlag++;
            }
        }
        switch (jamFlag){
            case 0:
            case 1:
            case 2: TextViewWifiChannelIsJam.setText("信道畅通"); TextViewWifiChannelIsJam.setTextColor(Color.parseColor("#3CB371")); break;
            case 3:
            case 4: TextViewWifiChannelIsJam.setText("信道拥挤"); TextViewWifiChannelIsJam.setTextColor(Color.parseColor("#FFD700")); break;
            default: TextViewWifiChannelIsJam.setText("信道堵塞"); TextViewWifiChannelIsJam.setTextColor(Color.parseColor("#FF0000")); break;
        }


        RecyclerViewWifiInfoAdapter recyclerViewWifiInfoAdapter = new RecyclerViewWifiInfoAdapter(context,wifiList,CHANNEL_FREQUENCY_MAP,nowSSID);
        RecyclerView RecyclerViewWifiInfo = findViewById(R.id.RecyclerViewWifiInfo);
        RecyclerViewWifiInfo.setAdapter(recyclerViewWifiInfoAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewWifiInfo.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        RecyclerViewWifiInfo.setLayoutManager(layoutManager);
        //System.out.println("wifi信息"+ wifiList);
    }

    public List<ScanResult> getWifiList() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        List<ScanResult> scanWifiList = wifiManager.getScanResults();
        List<ScanResult> wifiList1 = new ArrayList<>();
        if (scanWifiList != null && scanWifiList.size() > 0) {
            HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
            for (int i = 0; i < scanWifiList.size(); i++) {
                ScanResult scanResult = scanWifiList.get(i);
                if (!scanResult.SSID.isEmpty()) {
                    String key = scanResult.SSID + " " + scanResult.capabilities;
                    if (!signalStrength.containsKey(key)) {
                        signalStrength.put(key, i);
                        wifiList1.add(scanResult);
                    }
                }
            }
        }
        //List<ScanResult> wifiList = new ArrayList<>();
        Collections.sort(wifiList1, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult o1, ScanResult o2) {
                int i = o2.level - o1.level;
                return i;
            }
        });
        return wifiList1;
    }

    private void registerPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);

        } else {
            handleWifiInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            handleWifiInfo();
        }
    }

    HashMap<Integer,Integer> CHANNEL_FREQUENCY_MAP = new HashMap<Integer,Integer>(){
        {
            put(2412,1);put(2417,2);put(2422,3);put(2427,4);
            put(2432,5);put(2437,6);put(2442,7);put(2447,8);
            put(2452,9);put(2457,10);put(2462,11);put(2467,12);put(2472,13);put(2484,14);
            put(5745,149);put(5765,153);put(5785,157);put(5805,161);put(5825,165);

        }
    };

}
