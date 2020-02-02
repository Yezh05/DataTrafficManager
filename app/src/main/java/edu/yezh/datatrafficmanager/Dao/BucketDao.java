package edu.yezh.datatrafficmanager.Dao;

import android.content.Context;

import java.util.List;

public interface BucketDao {
    public List<Long> getTrafficDataOfThisMonth(Context context, String subscriberID);
    public List<Long> getTrafficDataFromStartDay(Context context,String subscriberID,int dataPlanStartDay);
    public void t1(Context context);
}
