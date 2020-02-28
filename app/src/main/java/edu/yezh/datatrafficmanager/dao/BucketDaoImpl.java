package edu.yezh.datatrafficmanager.dao;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
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
        try {
            TransInfo useBytes = getTrafficData(context, subscriberID, networkType, new DateTools().getTimesMonthMorning(), System.currentTimeMillis());

            return useBytes;
        } catch (Exception e) {
            Log.e("严重错误", "错误信息:" + e.toString());
            return null;
        }
    }

    @Override
    public TransInfo getTrafficDataFromStartDayToToday(Context context, String subscriberID, int dataPlanStartDay, int networkType) {
        try {
            TransInfo useBytesStartDayToToday = getTrafficData(context, subscriberID, networkType, new DateTools().getTimesStartDayMorning(dataPlanStartDay), System.currentTimeMillis());

            return useBytesStartDayToToday;
        } catch (Exception e) {
            Log.e("严重错误", "错误信息:" + e.toString());
            return null;
        }
    }

    @Override
    public List<TransInfo> getTrafficDataOfLastThirtyDays(Context context, String subscriberID, int networkType) {
        List<TransInfo> lastThirtyDaysTrafficData = new ArrayList<>();
        try {
            DateTools dateTools = new DateTools();
            Map<String, List<Long>> LastThirtyDaysMap = dateTools.getLastThirtyDaysMap();
            List<Long> lastThirtyDaysStartTimeInMillis = LastThirtyDaysMap.get("StartTimeList");
            List<Long> lastThirtyDaysEndTimeInMillis = LastThirtyDaysMap.get("EndTimeList");
            for (int i = 0; i < lastThirtyDaysEndTimeInMillis.size(); i++) {
                TransInfo useBytes = getTrafficData(context,subscriberID,networkType, lastThirtyDaysStartTimeInMillis.get(i), lastThirtyDaysEndTimeInMillis.get(i));
                //System.out.println("第"+i+"次添加数据："+useBytes);
                lastThirtyDaysTrafficData.add(useBytes);
            }
            return lastThirtyDaysTrafficData;
        } catch (Exception e) {
            Log.e("严重错误", "错误信息:" + e.toString());
            return lastThirtyDaysTrafficData;
        }

    }

    @Override
    public List<Object> getTrafficDataOfLastTwelveMonths(Context context, String subscriberID, int dataPlanStartDay, int networkType) {
        List<Object> objectsDataList = new ArrayList<>();
        try {
            DateTools dateTools = new DateTools();
            Map<String, List<String>> lastSixMonthsMap = dateTools.getLastTwelveMonthsMap(dataPlanStartDay);
            List<String> lastTwelveMonthsStartTimeInMillisList = lastSixMonthsMap.get("StartTimeList");
            List<String> lastTwelveMonthsEndTimeInMillisList = lastSixMonthsMap.get("EndTimeList");
            List<String> lastTwelveMonthsStartMonthAndEndMonth = lastSixMonthsMap.get("MonthString");
            List<TransInfo> lastTwelveMonthsTrafficDataTotalList = new ArrayList<>();
            for (int i = 0; i < lastTwelveMonthsStartTimeInMillisList.size(); i++) {
                TransInfo useBytes = getTrafficData(context,subscriberID,networkType, Long.valueOf(lastTwelveMonthsStartTimeInMillisList.get(i)), Long.valueOf(lastTwelveMonthsEndTimeInMillisList.get(i)));
                lastTwelveMonthsTrafficDataTotalList.add(useBytes);
            }
            objectsDataList.add(0,lastTwelveMonthsStartMonthAndEndMonth);
            objectsDataList.add(1,lastTwelveMonthsTrafficDataTotalList);
            objectsDataList.add(2,lastTwelveMonthsStartTimeInMillisList);
            objectsDataList.add(3,lastTwelveMonthsEndTimeInMillisList);
            return  objectsDataList;
        } catch (Exception e) {
            Log.e("严重错误", "错误信息:" + e.toString());
            //return lastSixMonthsTrafficDataMap;
            return  objectsDataList;
        }


    }

    @Override
    public List<AppsInfo> getAllInstalledAppsTrafficData(Context context, String subscriberID,  int networkType,long startTime,long endTime) {
        List<AppsInfo> allInstalledAppsTrafficData = new ArrayList<>();
        InstalledAppsInfoTools installedAppsInfoTools = new InstalledAppsInfoTools();
        List<AppsInfo> allInstalledAppsInfo = installedAppsInfoTools.getAllInstalledAppsInfo(context);
        System.out.println("安装应用信息列表长度:"+allInstalledAppsInfo.size());
        for (int i = 0; i < allInstalledAppsInfo.size(); i++) {
            AppsInfo singleInstalledAppsInfo = allInstalledAppsInfo.get(i);
            TransInfo data = new TransInfo(0, 0);
            try {
                data = getAppTrafficData(context,subscriberID,networkType,startTime,endTime,Integer.valueOf(singleInstalledAppsInfo.getUid()));
            } catch (Exception e) {
                Log.e("严重错误", "错误信息:" + e.toString());
            }
            long rxBytes = data.getRx();
            long txBytes = data.getTx();
            if (rxBytes == 0L && txBytes == 0L) {
                continue;
            }
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
        return AppTrafficDataList;
    }
}
