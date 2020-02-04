package edu.yezh.datatrafficmanager.Dao;

import android.Manifest;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yezh.datatrafficmanager.tools.DateTools;

import static android.content.Context.NETWORK_STATS_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

public class BucketDaoImpl implements BucketDao {


    @Override
    public long getTrafficDataOfThisMonth(Context context,String subscriberID) {
        //List<SubscriptionInfo> subscriptionInfoList = getSubscriptionInfoList(context);
        //List<Long> rxBytesList = new ArrayList<Long>();
        //for (SubscriptionInfo info : subscriptionInfoList) {
            try {
                NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
                NetworkStats.Bucket bucketThisMonth = null;
                DateTools dateTools = new DateTools();
                bucketThisMonth = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, subscriberID, dateTools.getTimesMonthmorning(), System.currentTimeMillis());
                long useBytes = bucketThisMonth.getRxBytes()+bucketThisMonth.getTxBytes();
                /*rxBytesList.add(rxBytes);*/
                return useBytes;
            } catch (Exception e) {
                Log.e("严重错误", "错误信息:" + e.toString());
                return 0;
            }
       // }
        //return  rxBytesList;
    }

    @Override
    public long getTrafficDataFromStartDay(Context context,String subscriberID , int dataPlanStartDay) {
        //List<SubscriptionInfo> subscriptionInfoList = getSubscriptionInfoList(context);
        //List<Long> rxBytesList = new ArrayList<Long>();
        //for (SubscriptionInfo info : subscriptionInfoList) {
        try {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            NetworkStats.Bucket bucketStartDayToToday = null;
            DateTools dateTools = new DateTools();
            bucketStartDayToToday = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,subscriberID, dateTools.getTimesStartDayMorning(dataPlanStartDay), System.currentTimeMillis());
            long useBytesStartDayToToday = bucketStartDayToToday.getRxBytes()+bucketStartDayToToday.getTxBytes();
            //rxBytesList.add(rxBytesStartDayToToday);
            //Log.e("当前卡流量",String.valueOf(rxBytesStartDayToToday));
            return useBytesStartDayToToday;
        } catch (Exception e) {
            Log.e("严重错误", "错误信息:" + e.toString());
            return 0;
        }
        //}
        //return rxBytesList;
    }
    @Override
    public List<Long> getLastSevenDaysTrafficData(Context context, String subscriberID) {
        List<Long> lastSevenDaysTrafficData = new ArrayList<>();
        try {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            NetworkStats.Bucket bucket = null;
            DateTools dateTools = new DateTools();
            List<Long> lastSevenDaysStartTimeInMillis = dateTools.getLastSevenDaysStartTimeInMillis();
            List<Long> lastSevenDaysEndTimeInMillis = dateTools.getLastSevenDaysEndTimeInMillis();
            //System.out.println("起始时间长度："+lastSevenDaysStartTimeInMillis.size());
            //System.out.println("结束时间长度："+lastSevenDaysEndTimeInMillis.size());

            for (int i=6;i>=0;i--){
                bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,subscriberID, lastSevenDaysStartTimeInMillis.get(i), lastSevenDaysEndTimeInMillis.get(i));
                long useBytes = bucket.getRxBytes()+bucket.getTxBytes();

                //System.out.println("第"+i+"次添加数据："+useBytes);
                lastSevenDaysTrafficData.add(useBytes);
            }
            //System.out.println("lastSevenDaysTrafficData长度："+lastSevenDaysTrafficData.size());
            /*for (int i=0;i<7;i++)
            {
                System.out.println("一周使用量每天："+lastSevenDaysTrafficData.get(i));
            }*/
            return lastSevenDaysTrafficData;
        }catch (Exception e){
            Log.e("严重错误", "错误信息:" + e.toString());
            return lastSevenDaysTrafficData;
        }

    }

    @Override
    public Map<String,List<String>> getLastSixMonthsTrafficData(Context context, String subscriberID, int dataPlanStartDay) {
        Map<String,List<String>> lastSixMonthsTrafficDataMap=new HashMap<>();
        List<String> lastSixMonthsTrafficDataList = new ArrayList<>();
        try {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            NetworkStats.Bucket bucket = null;
            DateTools dateTools = new DateTools();
            Map<String,List<String>> lastSixMonthsMap = dateTools.getLastSixMonthsMap(dataPlanStartDay);
            List<String> lastSixMonthsStartTimeInMillisList = lastSixMonthsMap.get("StartTimeList");
            List<String> lastSixMonthsEndTimeInMillisList = lastSixMonthsMap.get("EndTimeList");
            List<String> lastSixMonthsStartMonthAndEndMonth = lastSixMonthsMap.get("MonthString");

            for (int i=0;i<6;i++){
                bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,subscriberID, Long.valueOf(lastSixMonthsStartTimeInMillisList.get(i)),Long.valueOf(lastSixMonthsEndTimeInMillisList.get(i)));
                long useBytes = bucket.getRxBytes()+bucket.getTxBytes();
                lastSixMonthsTrafficDataList.add(String.valueOf(useBytes));
            }
            lastSixMonthsTrafficDataMap.put("LastSixMonthsTrafficDataList",lastSixMonthsTrafficDataList);
            lastSixMonthsTrafficDataMap.put("MonthString",lastSixMonthsStartMonthAndEndMonth);
            return lastSixMonthsTrafficDataMap;
        }catch (Exception e){
            Log.e("严重错误", "错误信息:" + e.toString());
            return lastSixMonthsTrafficDataMap;
        }


    }



    /*@Override
    public void t1(Context context) {
        SubscriptionManager sub = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        *//*List<SubscriptionInfo> info = sub.getActiveSubscriptionInfoList();
        String icc1=""; String icc2 = "";*//*



            List<SubscriptionInfo> list = SubscriptionManager.from( context).getActiveSubscriptionInfoList();
            *//*for (SubscriptionInfo info : list) {
                Log.d("Q_M", "ICCID-->" + info.getIccId());
                Log.d("Q_M", "subId-->" + info.getSubscriptionId());
                Log.d("Q_M", "DisplayName-->" + info.getDisplayName());
                Log.d("Q_M", "CarrierName-->" + info.getCarrierName());
                Log.d("Q_M", "---------------------------------");
            }*//*


        *//*int count = sub.getActiveSubscriptionInfoCount();
        if (count > 0) {
            if (count > 1) {
                 icc1 = info.get(0).getIccId();
                 icc2 = info.get(1).getIccId();
                Log.e("PhoneUtil", icc1 + "," + icc2);
            } else {
                for (SubscriptionInfo list : info) {
                    icc1 = list.getIccId();
                    Log.e("PhoneUtil",  icc1);
                }
            }
        } else {
            Log.e("PhoneUtil", "无SIM卡");
        }*//*
        List<SubscriptionInfo> subscriptionInfoList = getSubscriptionInfoList(context);

        for (SubscriptionInfo info : subscriptionInfoList) {

            try {
                NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
                NetworkStats.Bucket bucketThisMonth = null;
                DateTools dateTools = new DateTools();
                bucketThisMonth = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, String.valueOf(getSubscriberId( context, info.getSubscriptionId())), dateTools.getTimesMonthmorning(), System.currentTimeMillis());
                long rxBytes = bucketThisMonth.getRxBytes();
                Log.e("PhoneUtil数据", "卡:"+info.getSubscriptionId()+","+info.getCarrierName()+"下载数据量:" +String.valueOf(rxBytes));
            } catch (Exception e) {
                Log.e("严重错误", "错误信息:" + e.toString());

            }

        }


    }*/


    /*
    * 获取Sim信息列表
    */
    @Override
    public List<SubscriptionInfo> getSubscriptionInfoList(Context context){
        SubscriptionManager sub = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return null;
        }
        List<SubscriptionInfo> list = SubscriptionManager.from( context).getActiveSubscriptionInfoList();
        /*for (SubscriptionInfo info : list) {
                Log.d("Q_M", "ICCID-->" + info.getIccId());
                Log.d("Q_M", "subId-->" + info.getSubscriptionId());
                Log.d("Q_M", "DisplayName-->" + info.getDisplayName());
                Log.d("Q_M", "CarrierName-->" + info.getCarrierName());
                Log.d("Q_M", "---------------------------------");
            }*/
        Log.d("Sim卡信息","Sim卡数量:"+list.size());
        return list;
    }

    /*
    * 通过SimId获取IMSI
    */
    @Override
    public String getSubscriberId(Context context,int subId) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);// 取得相关系统服务
        Class<?> telephonyManagerClass = null;
        String imsi = null;
        try {
            telephonyManagerClass = Class.forName("android.telephony.TelephonyManager");

            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
                Method method = telephonyManagerClass.getMethod("getSubscriberId", int.class);
                imsi = (String) method.invoke(telephonyManager, subId);
            } else if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.LOLLIPOP) {
                Method method = telephonyManagerClass.getMethod("getSubscriberId", long.class);
                imsi = (String) method.invoke(telephonyManager, (long) subId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("Q_M", "IMSI--" + imsi);
        return imsi;
    }



}
