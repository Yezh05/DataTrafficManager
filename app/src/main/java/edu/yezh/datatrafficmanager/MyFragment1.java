package edu.yezh.datatrafficmanager;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.DateTools;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NETWORK_STATS_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

public class MyFragment1 extends Fragment {
    public MyFragment1() {
    }
    public String subscriberID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.t1, container, false);
        //TextView txt_content = (TextView) view.findViewById(R.id.txt_content);
        //txt_content.setText("第一个Fragment");
        Log.e("HEHE", "1日狗");


        Context context = this.getContext();

        /*NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);*/
        /*NetworkStats.Bucket bucket = null;*/
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{"android.permission.READ_PHONE_STATE"}, 1);
        }
        subscriberID = tm.getSubscriberId();
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                openEditViewAlert();
            }
        });

        float dataPlan = setTextViewDataPlan(view);
        setTrafficDataView( view, subscriberID,dataPlan);


        return view;
    }
    /*获取并显示流量使用情况*/
    public void setTrafficDataView(View view,String subscriberID,float dataPlan){
        try {

            TextView TextViewSubscriberID = (TextView)view.findViewById(R.id.TextViewSubscriberID);
            TextViewSubscriberID.setText(subscriberID);

            SharedPreferences sp = getActivity().getSharedPreferences("TrafficManager",MODE_PRIVATE);
            Context context = this.getContext();
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            NetworkStats.Bucket bucketThisMonth = null;
            NetworkStats.Bucket bucketStartDayToToday = null;
            DateTools dateTools = new DateTools();
            bucketThisMonth = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, subscriberID, dateTools.getTimesMonthmorning(), System.currentTimeMillis());
            //Log.w("Info", "Total: " + (bucket.getRxBytes() + bucket.getTxBytes()));

            long rxBytes = bucketThisMonth.getRxBytes();
            BytesFormatter bytesFormatter = new BytesFormatter();
            String readableData = bytesFormatter.getPrintSize(rxBytes);
            TextView TextViewData4GThisMonth = (TextView) view.findViewById(R.id.TextViewData4GThisMonth);
            TextViewData4GThisMonth.setText(readableData);

            int dataPlanStartDay = sp.getInt("dataPlanStartDay_" + subscriberID,1);
            bucketStartDayToToday = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, subscriberID, dateTools.getTimesStartDayMorning(dataPlanStartDay), System.currentTimeMillis());
            long rxBytesStartDayToToday = bucketStartDayToToday.getRxBytes();
            String readableDataStartDayToToday = bytesFormatter.getPrintSize(rxBytesStartDayToToday);
            TextView TextViewData4GStartDayToToday = (TextView)view.findViewById(R.id.TextViewData4GStartDayToToday);
            TextViewData4GStartDayToToday.setText(readableDataStartDayToToday);

            float DataUseStatus =  (rxBytesStartDayToToday/( dataPlan * 1024 * 1024 * 1024  ))*100;
            int PercentDataUseStatus= Math.round(DataUseStatus);
            String TextDataUseStatus = "";
            if (PercentDataUseStatus<0) {TextDataUseStatus = "请设置流量限额";}
            if (PercentDataUseStatus>=0&&PercentDataUseStatus<30) {TextDataUseStatus = "流量充足，放心使用";}
                else if (PercentDataUseStatus>=30&&PercentDataUseStatus<50) {TextDataUseStatus = "流量较多，正常使用";}
                else if (PercentDataUseStatus>=50&&PercentDataUseStatus<80) {TextDataUseStatus = "流量过半，注意使用";}
                else if (PercentDataUseStatus>=80&&PercentDataUseStatus<90) {TextDataUseStatus = "流量较少，谨慎使用";}
                else if (PercentDataUseStatus>=90&&PercentDataUseStatus<100) {TextDataUseStatus = "流量告急，谨慎使用";}
                else if (PercentDataUseStatus>=100) {TextDataUseStatus = "流量使用量已超过套餐限额，敬请留意";}
            TextView TextViewDataUseStatus = (TextView)view.findViewById(R.id.TextViewDataUseStatus);
            TextViewDataUseStatus.setText( String.valueOf(PercentDataUseStatus)+"%\n"+ TextDataUseStatus );

        } catch (RemoteException e) {
            Log.e("bucket", "错误：GetTotalError");
            e.printStackTrace();
        }
    }
    /*流量套餐限额设置框*/
    public void openEditViewAlert(){
        final Context context = this.getContext();

        final EditText inputDataPlanStartDay = new EditText(context);
        inputDataPlanStartDay.setHint("请输入套餐起始日");
        AlertDialog.Builder builderDataPlanStartDay = new AlertDialog.Builder(context);
        builderDataPlanStartDay.setTitle("设置套餐起始日").setIcon(R.mipmap.edit).setView(inputDataPlanStartDay).setNegativeButton("取消", null);
        builderDataPlanStartDay.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                final EditText inputDataPlan = new EditText(context);
                inputDataPlan.setHint("请输入套餐流量额度(GB)");
                AlertDialog.Builder builderDataPlan = new AlertDialog.Builder(context);
                builderDataPlan.setTitle("设置套餐流量额度").setIcon(R.mipmap.edit).setView(inputDataPlan).setNegativeButton("取消", null);
                builderDataPlan.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String inputData = inputDataPlan.getText().toString();
                        Toast.makeText(getActivity(), inputData, Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = context.getSharedPreferences("TrafficManager", Context.MODE_PRIVATE).edit();
                        Log.w("设置流量套餐信息", "dataPlan_" + subscriberID + " : " + Float.valueOf(inputData).toString());
                        editor.putFloat("dataPlan_" + subscriberID, Float.valueOf(inputData));
                        editor.putInt("dataPlanStartDay_" + subscriberID, Integer.valueOf(inputDataPlanStartDay.getText().toString()));
                        editor.commit();
                        setTextViewDataPlan(getView());
                    }
                });
                builderDataPlan.show();

            }
        });
        builderDataPlanStartDay.show();
    }
    /*获取并显示流量套餐限额*/
    public Float setTextViewDataPlan(View view){
        SharedPreferences sp = getActivity().getSharedPreferences("TrafficManager",MODE_PRIVATE);
        Float dataPlan = sp.getFloat("dataPlan_"+subscriberID,-1);
        int dataPlanStartDay = sp.getInt("dataPlanStartDay_" + subscriberID,1);
        Log.w("流量套餐限额","dataPlan_"+subscriberID+" : "+dataPlan.toString());
        Log.w("流量套餐起始日","dataPlanStartDay_"+subscriberID+" : "+dataPlanStartDay);
        TextView TextViewDataPlan = (TextView) view.findViewById(R.id.TextViewDataPlan);
        TextViewDataPlan.setText(dataPlan.toString()+"GB");
        return dataPlan;
    }
}
