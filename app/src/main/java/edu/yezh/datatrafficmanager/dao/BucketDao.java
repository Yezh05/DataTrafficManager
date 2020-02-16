package edu.yezh.datatrafficmanager.dao;

import android.content.Context;
import android.telephony.SubscriptionInfo;

import java.util.List;
import java.util.Map;

import edu.yezh.datatrafficmanager.model.AppsInfo;
import edu.yezh.datatrafficmanager.model.TransInfo;

public interface BucketDao {
    public TransInfo getTrafficDataOfThisMonth(Context context, String subscriberID, int networkType);
    public TransInfo getTrafficDataFromStartDay(Context context,String subscriberID,int dataPlanStartDay,int networkType);
    /*public List<SubscriptionInfo> getSubscriptionInfoList(Context context);
    public String getSubscriberId(Context context,int subId);*/
    public List<TransInfo> getLastSevenDaysTrafficData(Context context,String subscriberID,int networkType);
    public Map<String,List<String>> getLastSixMonthsTrafficData(Context context, String subscriberID, int dataPlanStartDay,int networkType);
    public List<AppsInfo> getInstalledAppsTrafficData(Context context, String subscriberID , int dataPlanStartDay, int networkType);
    public List<AppsInfo> getInstalledAppsTodayTrafficData(Context context,String subscriberID ,int networkType);
    public TransInfo getTrafficDataOfApp(Context context, String subscriberID, int networkType,long startTime,long endTime,int uid);
    public List<TransInfo> getAppTrafficDataForPeriod(Context context, String subscriberID, int networkType,Map<String,List<Long>> timeMap,int uid);
}
