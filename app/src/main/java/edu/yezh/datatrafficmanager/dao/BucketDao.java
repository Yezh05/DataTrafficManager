package edu.yezh.datatrafficmanager.dao;

import android.content.Context;
import android.telephony.SubscriptionInfo;

import java.util.List;
import java.util.Map;

import edu.yezh.datatrafficmanager.model.AppsInfo;
import edu.yezh.datatrafficmanager.model.TransInfo;

public interface BucketDao {
    public TransInfo getTrafficData(Context context, String subscriberID, int networkType,long startTime,long endTime);
    public TransInfo getTrafficDataOfToday(Context context, String subscriberID, int networkType);
    public TransInfo getTrafficDataOfThisMonth(Context context, String subscriberID, int networkType);
    public TransInfo getTrafficDataFromStartDayToToday(Context context, String subscriberID, int dataPlanStartDay, int networkType);
    /*public List<SubscriptionInfo> getSubscriptionInfoList(Context context);
    public String getSubscriberId(Context context,int subId);*/
    public List<TransInfo> getTrafficDataOfLastThirtyDays(Context context, String subscriberID, int networkType);
    public Map<String,List<String>> getTrafficDataOfLastTwelveMonths(Context context, String subscriberID, int dataPlanStartDay, int networkType);
    public List<AppsInfo> getAllInstalledAppsTrafficData(Context context, String subscriberID ,  int networkType,long startTime,long endTime );
    //public List<AppsInfo> getAllInstalledAppsTrafficDataOfToday(Context context, String subscriberID , int networkType);
    public TransInfo getAppTrafficData(Context context, String subscriberID, int networkType, long startTime, long endTime, int uid);
    public List<TransInfo> getAppTrafficDataOfPeriod(Context context, String subscriberID, int networkType, Map<String,List<Long>> timeMap, int uid);
}
