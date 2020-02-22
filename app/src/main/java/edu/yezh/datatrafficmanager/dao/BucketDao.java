package edu.yezh.datatrafficmanager.dao;

import android.content.Context;

import java.util.List;
import java.util.Map;

import edu.yezh.datatrafficmanager.model.AppsInfo;
import edu.yezh.datatrafficmanager.model.TransInfo;

public interface BucketDao {
    public TransInfo getTrafficData(Context context, String subscriberID, int networkType,long startTime,long endTime);
    public TransInfo getTrafficDataOfToday(Context context, String subscriberID, int networkType);
    public TransInfo getTrafficDataOfThisMonth(Context context, String subscriberID, int networkType);
    public TransInfo getTrafficDataFromStartDayToToday(Context context, String subscriberID, int dataPlanStartDay, int networkType);
    public List<TransInfo> getTrafficDataOfLastThirtyDays(Context context, String subscriberID, int networkType);
    public List<Object> getTrafficDataOfLastTwelveMonths(Context context, String subscriberID, int dataPlanStartDay, int networkType);
    public List<AppsInfo> getAllInstalledAppsTrafficData(Context context, String subscriberID ,  int networkType,long startTime,long endTime );
    public TransInfo getAppTrafficData(Context context, String subscriberID, int networkType, long startTime, long endTime, int uid);
    public List<TransInfo> getAppTrafficDataOfPeriod(Context context, String subscriberID, int networkType, Map<String,List<Long>> timeMap, int uid);
}
