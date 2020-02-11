package edu.yezh.datatrafficmanager.dao;

import android.content.Context;
import android.telephony.SubscriptionInfo;

import java.util.List;
import java.util.Map;

import edu.yezh.datatrafficmanager.model.AppsInfo;

public interface BucketDao {
    public long getTrafficDataOfThisMonth(Context context, String subscriberID,int networkType);
    public long getTrafficDataFromStartDay(Context context,String subscriberID,int dataPlanStartDay,int networkType);
    /*public List<SubscriptionInfo> getSubscriptionInfoList(Context context);
    public String getSubscriberId(Context context,int subId);*/
    public List<Long> getLastSevenDaysTrafficData(Context context,String subscriberID,int networkType);
    public Map<String,List<String>> getLastSixMonthsTrafficData(Context context, String subscriberID, int dataPlanStartDay,int networkType);
    public List<AppsInfo> getInstalledAppsTrafficData(Context context, String subscriberID , int dataPlanStartDay, int networkType);
    public List<AppsInfo> getInstalledAppsTodayTrafficData(Context context,String subscriberID ,int networkType);

}
