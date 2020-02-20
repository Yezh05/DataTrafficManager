package edu.yezh.datatrafficmanager.dao;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
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
    public TransInfo getTrafficData(Context context, String subscriberID, int networkType, long startTime, long endTime) {
        try {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            NetworkStats.Bucket bucket = networkStatsManager.querySummaryForDevice(networkType, subscriberID, startTime, endTime);
            TransInfo useBytes = new TransInfo(bucket.getRxBytes(), bucket.getTxBytes());
            return useBytes;
        } catch (Exception e) {
            Log.e("严重错误", "错误信息:" + e.toString());
            return null;
        }
    }

    @Override
    public TransInfo getTrafficDataOfToday(Context context, String subscriberID, int networkType) {
        TransInfo useBytes = getTrafficData(context, subscriberID, networkType, new DateTools().getTimesTodayMorning(), System.currentTimeMillis());
        return useBytes;
    }

    @Override
    public TransInfo getTrafficDataOfThisMonth(Context context, String subscriberID, int networkType) {
        //List<SubscriptionInfo> subscriptionInfoList = getSubscriptionInfoList(context);
        //List<Long> rxBytesList = new ArrayList<Long>();
        //for (SubscriptionInfo info : subscriptionInfoList) {
        try {
                /*NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
                NetworkStats.Bucket bucketThisMonth = null;
                DateTools dateTools = new DateTools();
                bucketThisMonth = networkStatsManager.querySummaryForDevice(networkType, subscriberID, dateTools.getTimesMonthmorning(), System.currentTimeMillis());
                */
            TransInfo useBytes = getTrafficData(context, subscriberID, networkType, new DateTools().getTimesMonthmorning(), System.currentTimeMillis());
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
    public TransInfo getTrafficDataFromStartDayToToday(Context context, String subscriberID, int dataPlanStartDay, int networkType) {
        //List<SubscriptionInfo> subscriptionInfoList = getSubscriptionInfoList(context);
        //List<Long> rxBytesList = new ArrayList<Long>();
        //for (SubscriptionInfo info : subscriptionInfoList) {
        try {
            /*NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            NetworkStats.Bucket bucketStartDayToToday = null;
            DateTools dateTools = new DateTools();
            bucketStartDayToToday = networkStatsManager.querySummaryForDevice(networkType,subscriberID, dateTools.getTimesStartDayMorning(dataPlanStartDay), System.currentTimeMillis());
            */


            TransInfo useBytesStartDayToToday = getTrafficData(context, subscriberID, networkType, new DateTools().getTimesStartDayMorning(dataPlanStartDay), System.currentTimeMillis());
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
    public List<TransInfo> getTrafficDataOfLastThirtyDays(Context context, String subscriberID, int networkType) {
        List<TransInfo> lastThirtyDaysTrafficData = new ArrayList<>();
        try {
            /*NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            NetworkStats.Bucket bucket;*/
            DateTools dateTools = new DateTools();
            /*List<Long> lastSevenDaysStartTimeInMillis = dateTools.getLastSevenDaysStartTimeInMillis();
            List<Long> lastSevenDaysEndTimeInMillis = dateTools.getLastSevenDaysEndTimeInMillis();*/
            //System.out.println("起始时间长度："+lastSevenDaysStartTimeInMillis.size());
            //System.out.println("结束时间长度："+lastSevenDaysEndTimeInMillis.size());
            Map<String, List<Long>> LastThirtyDaysMap = dateTools.getLastThirtyDaysMap();
            List<Long> lastThirtyDaysStartTimeInMillis = LastThirtyDaysMap.get("StartTimeList");
            List<Long> lastThirtyDaysEndTimeInMillis = LastThirtyDaysMap.get("EndTimeList");
            for (int i = 0; i < lastThirtyDaysEndTimeInMillis.size(); i++) {
                /*bucket = networkStatsManager.querySummaryForDevice(networkType, subscriberID, lastThirtyDaysStartTimeInMillis.get(i), lastThirtyDaysEndTimeInMillis.get(i));
                TransInfo useBytes = new TransInfo(bucket.getRxBytes(), bucket.getTxBytes());*/
                TransInfo useBytes = getTrafficData(context,subscriberID,networkType, lastThirtyDaysStartTimeInMillis.get(i), lastThirtyDaysEndTimeInMillis.get(i));
                //System.out.println("第"+i+"次添加数据："+useBytes);
                lastThirtyDaysTrafficData.add(useBytes);
            }
            //System.out.println("lastSevenDaysTrafficData长度："+lastSevenDaysTrafficData.size());
            /*for (int i=0;i<7;i++)
            {
                System.out.println("一周使用量每天："+lastSevenDaysTrafficData.get(i));
            }*/
            return lastThirtyDaysTrafficData;
        } catch (Exception e) {
            Log.e("严重错误", "错误信息:" + e.toString());
            return lastThirtyDaysTrafficData;
        }

    }

    @Override
    //public Map<String, List<String>> getTrafficDataOfLastTwelveMonths(Context context, String subscriberID, int dataPlanStartDay, int networkType) {
    public List<Object> getTrafficDataOfLastTwelveMonths(Context context, String subscriberID, int dataPlanStartDay, int networkType) {
        //Map<String, List<String>> lastSixMonthsTrafficDataMap = new HashMap<>();
        List<Object> objectsDataList = new ArrayList<>();
        try {
            /*NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            NetworkStats.Bucket bucket;*/
            DateTools dateTools = new DateTools();
            Map<String, List<String>> lastSixMonthsMap = dateTools.getLastTwelveMonthsMap(dataPlanStartDay);
            List<String> lastTwelveMonthsStartTimeInMillisList = lastSixMonthsMap.get("StartTimeList");
            List<String> lastTwelveMonthsEndTimeInMillisList = lastSixMonthsMap.get("EndTimeList");
            List<String> lastTwelveMonthsStartMonthAndEndMonth = lastSixMonthsMap.get("MonthString");

            //List<String> lastTwelveMonthsTrafficDataTotalList = new ArrayList<>();
            List<TransInfo> lastTwelveMonthsTrafficDataTotalList = new ArrayList<>();
            for (int i = 0; i < lastTwelveMonthsStartTimeInMillisList.size(); i++) {
                /*bucket = networkStatsManager.querySummaryForDevice(networkType, subscriberID, Long.valueOf(lastTwelveMonthsStartTimeInMillisList.get(i)), Long.valueOf(lastTwelveMonthsEndTimeInMillisList.get(i)));
                long useBytes = bucket.getRxBytes() + bucket.getTxBytes();*/

                TransInfo useBytes = getTrafficData(context,subscriberID,networkType, Long.valueOf(lastTwelveMonthsStartTimeInMillisList.get(i)), Long.valueOf(lastTwelveMonthsEndTimeInMillisList.get(i)));
                lastTwelveMonthsTrafficDataTotalList.add(useBytes);
                //lastTwelveMonthsTrafficDataTotalList.add(String.valueOf(useBytes.getTotal()));
            }
            /*lastSixMonthsTrafficDataMap.put("LastSixMonthsTrafficDataTotalList", lastTwelveMonthsTrafficDataTotalList);
            lastSixMonthsTrafficDataMap.put("MonthString", lastTwelveMonthsStartMonthAndEndMonth);
            return lastSixMonthsTrafficDataMap;*/

            objectsDataList.add(0,lastTwelveMonthsStartMonthAndEndMonth);
            objectsDataList.add(1,lastTwelveMonthsTrafficDataTotalList);
            return  objectsDataList;
        } catch (Exception e) {
            Log.e("严重错误", "错误信息:" + e.toString());
            //return lastSixMonthsTrafficDataMap;
            return  objectsDataList;
        }


    }

    @Override
    public List<AppsInfo> getAllInstalledAppsTrafficData(Context context, String subscriberID,  int networkType,long startTime,long endTime) {
        /*NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
        NetworkStats networkStats = null;*/
        List<AppsInfo> allInstalledAppsTrafficData = new ArrayList<>();
        DateTools dateTools = new DateTools();
        InstalledAppsInfoTools installedAppsInfoTools = new InstalledAppsInfoTools();
        List<AppsInfo> allInstalledAppsInfo = installedAppsInfoTools.getAllInstalledAppsInfo(context);
        //System.out.println("安装应用信息列表长度:"+allInstalledAppsInfo.size());
        for (int i = 0; i < allInstalledAppsInfo.size(); i++) {
            AppsInfo singleInstalledAppsInfo = allInstalledAppsInfo.get(i);
            //System.out.println(singleInstalledAppsInfo.toString());
            //long startDayMorning = dateTools.getTimesStartDayMorning(dataPlanStartDay);
            TransInfo data = new TransInfo(0, 0);
            try {
                //networkStats = networkStatsManager.queryDetailsForUid(ConnectivityManager.TYPE_WIFI, "", dateTools.getTimesStartDayMorning(dataPlanStartDay), System.currentTimeMillis(), Integer.valueOf(singleInstalledAppsInfo.get("uid")));
                //System.out.println("开始时间:"+m);
                //networkStats = networkStatsManager.queryDetailsForUid(networkType, subscriberID, startDayMorning, System.currentTimeMillis(), Integer.valueOf(singleInstalledAppsInfo.getUid()));
            data = getAppTrafficData(context,subscriberID,networkType,startTime,endTime,Integer.valueOf(singleInstalledAppsInfo.getUid()));
            } catch (Exception e) {
                Log.e("严重错误", "错误信息:" + e.toString());
            }
            /*NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            long rxBytes = bucket.getRxBytes();
            long txBytes = bucket.getTxBytes();

            *//*networkStats.getNextBucket(bucket);
            rxBytes += bucket.getRxBytes();
            txBytes += bucket.getTxBytes();*//*

            while (networkStats.hasNextBucket()) {
                networkStats.getNextBucket(bucket);
                rxBytes += bucket.getRxBytes();
                txBytes += bucket.getTxBytes();
            }*/
            long rxBytes = data.getRx();
            long txBytes = data.getTx();
            if (rxBytes == 0L && txBytes == 0L) {
                continue;
            }

            /*singleInstalledAppsInfo.setRxBytes(rxBytes);
            singleInstalledAppsInfo.setTxBytes(txBytes);*/
            singleInstalledAppsInfo.setTrans(rxBytes,txBytes);
            //System.out.println(singleInstalledAppsTrafficData.toString());
            allInstalledAppsTrafficData.add(singleInstalledAppsInfo);
        }
        //System.out.println(allInstalledAppsTrafficData.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            allInstalledAppsTrafficData.sort(new Comparator<AppsInfo>() {
                @Override
                public int compare(AppsInfo a1, AppsInfo a2) {
                    return a2.getTrans().compareTo(a1.getTrans());
                }
            });
        }
        return allInstalledAppsTrafficData;
    }

    /*@Override
    public List<AppsInfo> getAllInstalledAppsTrafficDataOfToday(Context context, String subscriberID, int networkType) {
        *//*NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
        NetworkStats networkStats = null;*//*
        List<AppsInfo> allInstalledAppsTrafficData = new ArrayList<>();
        DateTools dateTools = new DateTools();
        InstalledAppsInfoTools installedAppsInfoTools = new InstalledAppsInfoTools();
        List<AppsInfo> allInstalledAppsInfo = installedAppsInfoTools.getAllInstalledAppsInfo(context);
        //System.out.println("安装应用信息列表长度:"+allInstalledAppsInfo.size());
        for (int i = 0; i < allInstalledAppsInfo.size(); i++) {
            AppsInfo singleInstalledAppsInfo = allInstalledAppsInfo.get(i);
            //System.out.println(singleInstalledAppsInfo.toString());
            long startDayMorning = dateTools.getTimesTodayMorning();
            TransInfo data = new TransInfo(0, 0);
            try {
                //networkStats = networkStatsManager.queryDetailsForUid(ConnectivityManager.TYPE_WIFI, "", dateTools.getTimesStartDayMorning(dataPlanStartDay), System.currentTimeMillis(), Integer.valueOf(singleInstalledAppsInfo.get("uid")));
                //System.out.println("开始时间:"+m);
                //networkStats = networkStatsManager.queryDetailsForUid);
                data = getAppTrafficData(context, subscriberID, networkType, startDayMorning, System.currentTimeMillis(), Integer.valueOf(singleInstalledAppsInfo.getUid()));
            } catch (Exception e) {
                Log.e("严重错误", "错误信息:" + e.toString());
            }
            *//*NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            long rxBytes = bucket.getRxBytes();
            long txBytes = bucket.getTxBytes();*//*

            *//*networkStats.getNextBucket(bucket);
            rxBytes += bucket.getRxBytes();
            txBytes += bucket.getTxBytes();*//*

            *//*while(networkStats.hasNextBucket()) {
                networkStats.getNextBucket(bucket);
                rxBytes += bucket.getRxBytes();
                txBytes += bucket.getTxBytes();
            }*//*
            long rxBytes = data.getRx();
            long txBytes = data.getTx();
            if (rxBytes == 0L && txBytes == 0L) {
                continue;
            }

            singleInstalledAppsInfo.setRxBytes(rxBytes);
            singleInstalledAppsInfo.setTxBytes(txBytes);
            //System.out.println(singleInstalledAppsTrafficData.toString());
            allInstalledAppsTrafficData.add(singleInstalledAppsInfo);
        }
        //System.out.println(allInstalledAppsTrafficData.toString());
        return allInstalledAppsTrafficData;
    }*/

    @Override
    public TransInfo getAppTrafficData(Context context, String subscriberID, int networkType, long startTime, long endTime, int uid) {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
        NetworkStats networkStats = networkStatsManager.queryDetailsForUid(networkType, subscriberID, startTime, endTime, uid);
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        long rxBytes = bucket.getRxBytes();
        long txBytes = bucket.getTxBytes();

        while (networkStats.hasNextBucket()) {
            //System.out.println("bucket has Next");
            networkStats.getNextBucket(bucket);
            //System.out.println("BUCKET STARTTIME"+bucket.getStartTimeStamp());
            //System.out.println("BUCKET ENDTIME"+bucket.getEndTimeStamp());
            rxBytes += bucket.getRxBytes();
            txBytes += bucket.getTxBytes();
        }
        TransInfo data = new TransInfo(rxBytes, txBytes);

        return data;
    }

    @Override
    public List<TransInfo> getAppTrafficDataOfPeriod(Context context, String subscriberID, int networkType, Map<String, List<Long>> timeMap, int uid) {
        List<Long> StartTimeList = timeMap.get("StartTimeList");
        List<Long> EndTimeList = timeMap.get("EndTimeList");
        List<TransInfo> AppTrafficDataList = new ArrayList<>();

        int size = StartTimeList.size();
        for (int i = 0; i < size; i++) {
            AppTrafficDataList.add(getAppTrafficData(context, subscriberID, networkType, StartTimeList.get(i), EndTimeList.get(i), uid));
        }
        // System.out.println(1+""+getAppTrafficData(context,subscriberID,networkType,1581843600000l,1581854399999l+1l,uid));
        /*System.out.println(2+""+getAppTrafficData(context,subscriberID,networkType,1581771600000L,1581857999999L,uid));
        System.out.println(3+""+getAppTrafficData(context,subscriberID,networkType,1581771600000L,1581854399999L,uid));*/
        //System.out.println(AppTrafficDataList);
        return AppTrafficDataList;
    }
}
