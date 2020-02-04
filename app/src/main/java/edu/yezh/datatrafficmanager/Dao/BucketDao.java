package edu.yezh.datatrafficmanager.Dao;

import android.content.Context;
import android.telephony.SubscriptionInfo;

import java.util.List;
import java.util.Map;

public interface BucketDao {
    public long getTrafficDataOfThisMonth(Context context, String subscriberID);
    public long getTrafficDataFromStartDay(Context context,String subscriberID,int dataPlanStartDay);
    public List<SubscriptionInfo> getSubscriptionInfoList(Context context);
    public String getSubscriberId(Context context,int subId);
    public List<Long> getLastSevenDaysTrafficData(Context context,String subscriberID);
    public Map<String,List<String>> getLastSixMonthsTrafficData(Context context, String subscriberID, int dataPlanStartDay);
    //public void t1(Context context);
}
