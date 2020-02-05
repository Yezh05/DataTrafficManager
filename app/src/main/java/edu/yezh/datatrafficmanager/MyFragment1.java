package edu.yezh.datatrafficmanager;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yezh.datatrafficmanager.Dao.BucketDao;
import edu.yezh.datatrafficmanager.Dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.DateTools;
import edu.yezh.datatrafficmanager.tools.chartTools.MyLineValueFormatter;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.TELEPHONY_SERVICE;

public class MyFragment1 extends Fragment {
    public MyFragment1() {
    }
    //public String subscriberID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.t1, container, false);
        //TextView txt_content = (TextView) view.findViewById(R.id.txt_content);
        //txt_content.setText("第一个Fragment");
        Log.e("标签页", "移动网络页面");

        final Context context = this.getContext();
        BucketDao bucketDao = new BucketDaoImpl();

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
        //subscriberID = tm.getSubscriberId();

        final List<SubscriptionInfo> subscriptionInfoList = bucketDao.getSubscriptionInfoList(context);

        /*FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*//*
                openEditViewAlert();
            }
        });*/

        showRealTimeNetSpeed(view);

        final Button button_SIM1 = (Button) view.findViewById(R.id.Button_SIM1);
        final Button button_SIM2 = (Button) view.findViewById(R.id.Button_SIM2);
        button_SIM1.setText("SIM卡1:"+subscriptionInfoList.get(0).getCarrierName());
        button_SIM1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_SIM1.setTextColor(Color.parseColor("#009688"));
                button_SIM2.setTextColor(Color.BLACK);
                setTrafficDataView( view, subscriptionInfoList.get(0).getSubscriptionId());
            }
        });
        if (subscriptionInfoList.size()==2) {
            button_SIM2.setText("SIM卡2:"+subscriptionInfoList.get(1).getCarrierName());
            button_SIM2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    button_SIM2.setTextColor(Color.parseColor("#009688"));
                    button_SIM1.setTextColor(Color.BLACK);
                    setTrafficDataView(view, subscriptionInfoList.get(1).getSubscriptionId());
                }
            });
        }else {
            button_SIM2.setVisibility(View.GONE);
        }
        button_SIM1.performClick();



        return view;
    }
    /*获取并显示流量使用情况*/
    public void setTrafficDataView(View view,int simID){
        try {
            SharedPreferences sp = getActivity().getSharedPreferences("TrafficManager",MODE_PRIVATE);
            final Context context = this.getContext();
            BucketDao bucketDao = new BucketDaoImpl();
            BytesFormatter bytesFormatter = new BytesFormatter();
            DateTools dateTools = new DateTools();
            final String subscriberID = bucketDao.getSubscriberId(context,simID);

            /*TextView TextViewSubscriberID = (TextView)view.findViewById(R.id.TextViewSubscriberID);
            TextViewSubscriberID.setText(subscriberID);*/

            float dataPlan = setTextViewDataPlan(view,subscriberID);
            //bucketDao.t1(context);

            /*NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            NetworkStats.Bucket bucketThisMonth = null;
            NetworkStats.Bucket bucketStartDayToToday = null;
            DateTools dateTools = new DateTools();
            bucketThisMonth = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, subscriberID, dateTools.getTimesMonthmorning(), System.currentTimeMillis());
            //Log.w("Info", "Total: " + (bucket.getRxBytes() + bucket.getTxBytes()));

            long rxBytes = bucketThisMonth.getRxBytes();*/

            long rxBytes =bucketDao.getTrafficDataOfThisMonth(context,subscriberID, ConnectivityManager.TYPE_MOBILE);


            String readableData = bytesFormatter.getPrintSize(rxBytes);
            TextView TextViewData4GThisMonth = (TextView) view.findViewById(R.id.TextViewData4GThisMonth);
            TextViewData4GThisMonth.setText(readableData);

            final int dataPlanStartDay = sp.getInt("dataPlanStartDay_" + subscriberID,1);
            /*bucketStartDayToToday = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, subscriberID, dateTools.getTimesStartDayMorning(dataPlanStartDay), System.currentTimeMillis());
            long rxBytesStartDayToToday = bucketStartDayToToday.getRxBytes();*/
            long rxBytesStartDayToToday = bucketDao.getTrafficDataFromStartDay(context,subscriberID,dataPlanStartDay,ConnectivityManager.TYPE_MOBILE);

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




            List<Long> lastSevenDaysTrafficData = bucketDao.getLastSevenDaysTrafficData(context,subscriberID,ConnectivityManager.TYPE_MOBILE);
            //System.out.println("传出7日流量数据长度："+lastSevenDaysTrafficData.size());
            /*System.out.println("传出7日流量数据0："+lastSevenDaysTrafficData.get(0));
            System.out.println("传出7日流量数据6："+lastSevenDaysTrafficData.get(6));*/
            /*for (int i=0;i<lastSevenDaysTrafficData.size();i++){
                System.out.println("传出7日流量数据"+i+":"+lastSevenDaysTrafficData.get(i));
            }*/

            setChart(view,PercentDataUseStatus,lastSevenDaysTrafficData,dateTools.getLastSevenDays());


            FloatingActionButton fab = view.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                    openEditViewAlert(subscriberID);
                }
            });
            Button buttonShowLastSixMonthTrafficData = view.findViewById(R.id.ButtonShowLastSixMonthTrafficData);
            buttonShowLastSixMonthTrafficData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ShowDataListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("subscriberID",subscriberID);
                    bundle.putInt("dataPlanStartDay",dataPlanStartDay);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            Log.e("出现错误", "错误："+e.toString());
            e.printStackTrace();
        }
    }
    /*流量套餐限额设置框*/
    public void openEditViewAlert(final String subscriberID){
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
                        //Log.w("设置流量套餐信息", "dataPlan_" + subscriberID + " : " + Float.valueOf(inputData).toString());
                        editor.putFloat("dataPlan_" + subscriberID, Float.valueOf(inputData));
                        editor.putInt("dataPlanStartDay_" + subscriberID, Integer.valueOf(inputDataPlanStartDay.getText().toString()));
                        editor.commit();
                        setTextViewDataPlan(getView(),subscriberID);
                    }
                });
                builderDataPlan.show();

            }
        });
        builderDataPlanStartDay.show();
    }
    /*获取并显示流量套餐限额*/
    public Float setTextViewDataPlan(View view,String subscriberID){
        SharedPreferences sp = getActivity().getSharedPreferences("TrafficManager",MODE_PRIVATE);
        Float dataPlan = sp.getFloat("dataPlan_"+subscriberID,-1);
        int dataPlanStartDay = sp.getInt("dataPlanStartDay_" + subscriberID,1);
        Log.w("流量套餐限额","dataPlan_"+subscriberID+" : "+dataPlan.toString());
        Log.w("流量套餐起始日","dataPlanStartDay_"+subscriberID+" : "+dataPlanStartDay);
        TextView TextViewDataPlan = (TextView) view.findViewById(R.id.TextViewDataPlan);
        TextViewDataPlan.setText(dataPlan.toString()+"GB");
        return dataPlan;
    }

    public void setChart(View view,int PercentDataUseStatus,List<Long> lastSevenDaysTrafficData,List<Integer> lastSevenDays){
        BytesFormatter bytesFormatter = new BytesFormatter();
        PieChart pieChart = (PieChart)view.findViewById(R.id.Chart1);
        List yVals = new ArrayList<>();
        yVals.add(new PieEntry(100-PercentDataUseStatus, 100-PercentDataUseStatus+"%未使用"));
        yVals.add(new PieEntry(PercentDataUseStatus, PercentDataUseStatus+"%已使用"));

        List colors = new ArrayList<>();
        colors.add(Color.parseColor("#4A92FC"));
        colors.add(Color.parseColor("#ee6e55"));

        PieDataSet pieDataSet = new PieDataSet(yVals, "已使用流量");
        pieDataSet.setColors(colors);
        //pieDataSet.setValueFormatter(new PercentFormatter());
        pieDataSet.setDrawValues(false);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(Color.parseColor("#FFFFFF"));
        PieData pieData = new PieData(pieDataSet);
        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        pieChart.setData(pieData);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
        pieChart.animateXY(2000,2000);


        LineChart lineChart = (LineChart) view.findViewById(R.id.LineChartLastSevenDaysTrafficData);

        final ArrayList<Entry> values = new ArrayList<Entry>();
        /*values.add(new Entry(5, 50));
        values.add(new Entry(10, 66));
        values.add(new Entry(15, 120));
        values.add(new Entry(20, 30));
        values.add(new Entry(35, 10));
        values.add(new Entry(40, 110));
        values.add(new Entry(45, 30));
        values.add(new Entry(50, 160));
        values.add(new Entry(100, 30));*/



        final String[] Xvalues = new String[lastSevenDays.size()];
        for (int i=0;i<lastSevenDays.size();i++){
            values.add(new Entry(i,lastSevenDaysTrafficData.get(i)));
            Xvalues[i]= lastSevenDays.get(i) + "日" ;
        }


        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float xvalue, AxisBase axis) {
                return Xvalues[(int) xvalue];
            }

        };


        LineDataSet lineDataSet = new LineDataSet(values,"数据xxxx");
        //lineDataSet.setValues(values);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setColor(Color.parseColor("#008080"));
        lineDataSet.setFillColor(Color.parseColor("#20B2AA"));
        lineDataSet.setValueFormatter(new MyLineValueFormatter());
        lineDataSet.setValueTextSize(13);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleColor(Color.parseColor("#2F4F4F"));
        lineDataSet.setDrawCircleHole(false);
        dataSets.add(lineDataSet);
        LineData data1 = new LineData(dataSets);
        lineChart.setData(data1);
        lineChart.getLegend().setEnabled(false);

        lineChart.getXAxis().setGranularity(1);
        lineChart.getXAxis().setValueFormatter(formatter);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);

        lineChart.getAxisRight().setEnabled(false);
        //lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.animateXY(000,1200);

        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
    }
    /*
     * 显示实时下载速度
     * */
    public void showRealTimeNetSpeed(View view){
        final BytesFormatter bytesFormatter = new BytesFormatter();
        final long [] RXOld = new long [2];
        final Handler handler = new Handler();
        final TextView textViewRealTimeTxSpeed = (TextView)view.findViewById(R.id.TextViewRealTimeTxSpeed);
        final TextView textViewRealTimeRxSpeed = (TextView)view.findViewById(R.id.TextViewRealTimeRxSpeed);
        handler.postDelayed(new Runnable() {
            int firstTimeShow =0;
            @Override
            public void run() {
                if (firstTimeShow!=0){
                    long overTxTraffic = TrafficStats.getTotalTxBytes();
                    long overRxTraffic = TrafficStats.getTotalRxBytes();
                    long currentTxDataRate = overTxTraffic - RXOld [0];
                    long currentRxDataRate = overRxTraffic - RXOld [1];
                    //TextView view1 = null;
                    //view1 = (TextView) view.findViewById(R.id.view1);
                    //view1.setText("Current Data Rate per second= " + currentDataRate);
                    // System.out.println("Current Data Rate per second= " + bytesFormatter.getPrintSize(currentDataRate));
                    textViewRealTimeTxSpeed.setText("上传:"+ bytesFormatter.getPrintSize(currentTxDataRate)+"/s");
                    textViewRealTimeRxSpeed.setText("下载:"+ bytesFormatter.getPrintSize(currentRxDataRate)+"/s");
                    RXOld [0] = overTxTraffic;
                    RXOld[1]=overRxTraffic;
                }
                else {
                    long overTxTraffic = TrafficStats.getTotalTxBytes();
                    long overRxTraffic = TrafficStats.getTotalRxBytes();
                    RXOld [0] = overTxTraffic;
                    RXOld[1]=overRxTraffic;
                    firstTimeShow = 1;
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000 );
    }
}
