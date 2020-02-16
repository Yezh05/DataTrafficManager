package edu.yezh.datatrafficmanager.dao;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yezh.datatrafficmanager.model.AppsInfo;
import edu.yezh.datatrafficmanager.model.TransInfo;
import edu.yezh.datatrafficmanager.tools.DateTools;
import edu.yezh.datatrafficmanager.tools.InstalledAppsInfoTools;

import static android.content.Context.NETWORK_STATS_SERVICE;

public class BucketDaoImpl implements BucketDao {


    @Override
    public TransInfo getTrafficDataOfThisMonth(Context context,String subscriberID,int networkType) {
        //List<SubscriptionInfo> subscriptionInfoList = getSubscriptionInfoList(context);
        //List<Long> rxBytesList = new ArrayList<Long>();
        //for (SubscriptionInfo info : subscriptionInfoList) {
            try {
                NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
                NetworkStats.Bucket bucketThisMonth = null;
                DateTools dateTools = new DateTools();
                bucketThisMonth = networkStatsManager.querySummaryForDevice(networkType, subscriberID, dateTools.getTimesMonthmorning(), System.currentTimeMillis());
                TransInfo useBytes = new TransInfo( bucketThisMonth.getRxBytes(),bucketThisMonth.getTxBytes());
                /*rxBytesList.add(rxBytes);*/
                return useBytes;
            } catch (Exception e) {
                Log.e("严重错误", "错误信息:" + e.toString());
                return null;
            }
       // }
        //return  rxBytesList;
    }

    @Override
    public TransInfo getTrafficDataFromStartDay(Context context,String subscriberID , int dataPlanStartDay,int networkType) {
        //List<SubscriptionInfo> subscriptionInfoList = getSubscriptionInfoList(context);
        //List<Long> rxBytesList = new ArrayList<Long>();
        //for (SubscriptionInfo info : subscriptionInfoList) {
        try {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            NetworkStats.Bucket bucketStartDayToToday = null;
            DateTools dateTools = new DateTools();
            bucketStartDayToToday = networkStatsManager.querySummaryForDevice(networkType,subscriberID, dateTools.getTimesStartDayMorning(dataPlanStartDay), System.currentTimeMillis());
            TransInfo useBytesStartDayToToday = new TransInfo( bucketStartDayToToday.getRxBytes(),bucketStartDayToToday.getTxBytes());
            //rxBytesList.add(rxBytesStartDayToToday);
            //Log.e("当前卡流量",String.valueOf(rxBytesStartDayToToday));
            return useBytesStartDayToToday;
        } catch (Exception e) {
            Log.e("严重错误", "错误信息:" + e.toString());
            return null;
        }
        //}
        //return rxBytesList;
    }
    @Override
    public List<TransInfo> getLastSevenDaysTrafficData(Context context, String subscriberID,int networkType) {
        List<TransInfo> lastSevenDaysTrafficData = new ArrayList<>();
        try {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            NetworkStats.Bucket bucket;
            DateTools dateTools = new DateTools();
            List<Long> lastSevenDaysStartTimeInMillis = dateTools.getLastSevenDaysStartTimeInMillis();
            List<Long> lastSevenDaysEndTimeInMillis = dateTools.getLastSevenDaysEndTimeInMillis();
            //System.out.println("起始时间长度："+lastSevenDaysStartTimeInMillis.size());
            //System.out.println("结束时间长度："+lastSevenDaysEndTimeInMillis.size());

            for (int i=6;i>=0;i--){
                bucket = networkStatsManager.querySummaryForDevice(networkType,subscriberID, lastSevenDaysStartTimeInMillis.get(i), lastSevenDaysEndTimeInMillis.get(i));
                TransInfo useBytes = new TransInfo( bucket.getRxBytes(),bucket.getTxBytes());

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
    public Map<String,List<String>> getLastSixMonthsTrafficData(Context context, String subscriberID, int dataPlanStartDay,int networkType) {
        Map<String,List<String>> lastSixMonthsTrafficDataMap=new HashMap<>();
        try {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            NetworkStats.Bucket bucket;
            DateTools dateTools = new DateTools();
            Map<String,List<String>> lastSixMonthsMap = dateTools.getLastSixMonthsMap(dataPlanStartDay);
            List<String> lastSixMonthsStartTimeInMillisList = lastSixMonthsMap.get("StartTimeList");
            List<String> lastSixMonthsEndTimeInMillisList = lastSixMonthsMap.get("EndTimeList");
            List<String> lastSixMonthsStartMonthAndEndMonth = lastSixMonthsMap.get("MonthString");

            List<String> lastSixMonthsTrafficDataList = new ArrayList<>();
            for (int i = 0; i<6; i++){
                bucket = networkStatsManager.querySummaryForDevice(networkType,subscriberID, Long.valueOf(lastSixMonthsStartTimeInMillisList.get(i)),Long.valueOf(lastSixMonthsEndTimeInMillisList.get(i)));
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

    @Override
    public List<AppsInfo> getInstalledAppsTrafficData(Context context, String subscriberID ,int dataPlanStartDay, int networkType) {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
        NetworkStats networkStats = null;
        List<AppsInfo> allInstalledAppsTrafficData = new ArrayList<>();
        DateTools dateTools = new DateTools();
        InstalledAppsInfoTools installedAppsInfoTools = new InstalledAppsInfoTools();
        List<AppsInfo> allInstalledAppsInfo = installedAppsInfoTools.getAllInstalledAppsInfo(context);
        //System.out.println("安装应用信息列表长度:"+allInstalledAppsInfo.size());
        for (int i=0;i<allInstalledAppsInfo.size();i++) {
            AppsInfo singleInstalledAppsInfo = allInstalledAppsInfo.get(i);
            //System.out.println(singleInstalledAppsInfo.toString());
            long startDayMorning = dateTools.getTimesStartDayMorning(dataPlanStartDay);
            try {
                //networkStats = networkStatsManager.queryDetailsForUid(ConnectivityManager.TYPE_WIFI, "", dateTools.getTimesStartDayMorning(dataPlanStartDay), System.currentTimeMillis(), Integer.valueOf(singleInstalledAppsInfo.get("uid")));
                //System.out.println("开始时间:"+m);
                networkStats = networkStatsManager.queryDetailsForUid(networkType, subscriberID,startDayMorning, System.currentTimeMillis(), Integer.valueOf(singleInstalledAppsInfo.getUid()));
            } catch (Exception e) {
                Log.e("严重错误", "错误信息:" + e.toString());
            }
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            long rxBytes = bucket.getRxBytes();
            long txBytes = bucket.getTxBytes();

            /*networkStats.getNextBucket(bucket);
            rxBytes += bucket.getRxBytes();
            txBytes += bucket.getTxBytes();*/

            while(networkStats.hasNextBucket()) {
                networkStats.getNextBucket(bucket);
                rxBytes += bucket.getRxBytes();
                txBytes += bucket.getTxBytes();
            }

            if (rxBytes == 0L&&txBytes==0L){
                continue;
            }

            singleInstalledAppsInfo.setRxBytes(rxBytes);
            singleInstalledAppsInfo.setTxBytes(txBytes);
            //System.out.println(singleInstalledAppsTrafficData.toString());
            allInstalledAppsTrafficData.add(singleInstalledAppsInfo);
        }
        //System.out.println(allInstalledAppsTrafficData.toString());
        return allInstalledAppsTrafficData;
    }

    @Override
    public List<AppsInfo> getInstalledAppsTodayTrafficData(Context context, String subscriberID , int networkType) {
        /*NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
        NetworkStats networkStats = null;*/
        List<AppsInfo> allInstalledAppsTrafficData = new ArrayList<>();
        DateTools dateTools = new DateTools();
        InstalledAppsInfoTools installedAppsInfoTools = new InstalledAppsInfoTools();
        List<AppsInfo> allInstalledAppsInfo = installedAppsInfoTools.getAllInstalledAppsInfo(context);
        //System.out.println("安装应用信息列表长度:"+allInstalledAppsInfo.size());
        for (int i=0;i<allInstalledAppsInfo.size();i++) {
            AppsInfo singleInstalledAppsInfo = allInstalledAppsInfo.get(i);
            //System.out.println(singleInstalledAppsInfo.toString());
            long startDayMorning = dateTools.getTimesTodayMorning();
            TransInfo data = new TransInfo(0,0);
            try {
                //networkStats = networkStatsManager.queryDetailsForUid(ConnectivityManager.TYPE_WIFI, "", dateTools.getTimesStartDayMorning(dataPlanStartDay), System.currentTimeMillis(), Integer.valueOf(singleInstalledAppsInfo.get("uid")));
                //System.out.println("开始时间:"+m);
                //networkStats = networkStatsManager.queryDetailsForUid);
                data = getTrafficDataOfApp(context, subscriberID,networkType,startDayMorning, System.currentTimeMillis(), Integer.valueOf(singleInstalledAppsInfo.getUid()));
            } catch (Exception e) {
                Log.e("严重错误", "错误信息:" + e.toString());
            }
            /*NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            long rxBytes = bucket.getRxBytes();
            long txBytes = bucket.getTxBytes();*/

            /*networkStats.getNextBucket(bucket);
            rxBytes += bucket.getRxBytes();
            txBytes += bucket.getTxBytes();*/

            /*while(networkStats.hasNextBucket()) {
                networkStats.getNextBucket(bucket);
                rxBytes += bucket.getRxBytes();
                txBytes += bucket.getTxBytes();
            }*/
            long rxBytes = data.getRx();
            long txBytes =data.getTx();
            if (rxBytes == 0L&&txBytes==0L){
                continue;
            }

            singleInstalledAppsInfo.setRxBytes(rxBytes);
            singleInstalledAppsInfo.setTxBytes(txBytes);
            //System.out.println(singleInstalledAppsTrafficData.toString());
            allInstalledAppsTrafficData.add(singleInstalledAppsInfo);
        }
        //System.out.println(allInstalledAppsTrafficData.toString());
        return allInstalledAppsTrafficData;
    }

    @Override
    public TransInfo getTrafficDataOfApp(Context context, String subscriberID, int networkType, long startTime, long endTime, int uid) {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
        NetworkStats networkStats = networkStatsManager.queryDetailsForUid(networkType, subscriberID,startTime, endTime, uid);
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        long rxBytes = bucket.getRxBytes();
        long txBytes = bucket.getTxBytes();

        while(networkStats.hasNextBucket()) {
            //System.out.println("bucket has Next");
            networkStats.getNextBucket(bucket);
            //System.out.println("BUCKET STARTTIME"+bucket.getStartTimeStamp());
            //System.out.println("BUCKET ENDTIME"+bucket.getEndTimeStamp());
            rxBytes += bucket.getRxBytes();
            txBytes += bucket.getTxBytes();
        }
        TransInfo data = new TransInfo(rxBytes,txBytes);

        return data;
    }

    @Override
    public List<TransInfo> getAppTrafficDataForPeriod(Context context, String subscriberID, int networkType, Map<String, List<Long>> timeMap, int uid) {
        List<Long> StartTimeList = timeMap.get("StartTimeList");
        List<Long> EndTimeList = timeMap.get("EndTimeList");
        List<TransInfo> AppTrafficDataList = new ArrayList<>();

        int size = StartTimeList.size();
        for (int i=0;i<size;i++){
            AppTrafficDataList.add(getTrafficDataOfApp(context,subscriberID,networkType,StartTimeList.get(i),EndTimeList.get(i),uid));
        }
       // System.out.println(1+""+getTrafficDataOfApp(context,subscriberID,networkType,1581843600000l,1581854399999l+1l,uid));
        /*System.out.println(2+""+getTrafficDataOfApp(context,subscriberID,networkType,1581771600000L,1581857999999L,uid));
        System.out.println(3+""+getTrafficDataOfApp(context,subscriberID,networkType,1581771600000L,1581854399999L,uid));*/
        //System.out.println(AppTrafficDataList);
        return AppTrafficDataList;
    }
}
