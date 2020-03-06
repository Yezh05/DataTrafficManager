package edu.yezh.datatrafficmanager.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.EventStats;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

import static android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND;
import static android.content.Context.ACTIVITY_SERVICE;

public  class NowProcess {
    public static String getForegroundApp(Context context) {
        UsageStatsManager usageStatsManager =
                (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long ts = System.currentTimeMillis();
        List<UsageStats> queryUsageStats =
                usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
        UsageEvents usageEvents = usageStatsManager.queryEvents(ts - 3000, ts);
        if (usageEvents == null) {
            return null;
        }
        UsageEvents.Event event = new UsageEvents.Event();
        UsageEvents.Event lastEvent = null;
        while (usageEvents.getNextEvent(event)) {
            // if from notification bar, class name will be null
            if (event.getPackageName() == null || event.getClassName() == null) {
                continue;
            }

            if (lastEvent == null || lastEvent.getTimeStamp() < event.getTimeStamp()) {
                lastEvent = event;
            }
        }

        if (lastEvent == null) {
            return null;
        }
        return lastEvent.getPackageName();
    }

    public static String hdddd(Context context) {
        String packageName = "";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            final long end = System.currentTimeMillis();
            final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            if (null == usageStatsManager) {
                return packageName;
            }
            final UsageEvents events = usageStatsManager.queryEvents((end - 60 * 1000), end);
            if (null == events) {
                return packageName;
            }
            UsageEvents.Event usageEvent = new UsageEvents.Event();
            UsageEvents.Event lastMoveToFGEvent = null;
            while (events.hasNextEvent()) {
                events.getNextEvent(usageEvent);
                if (usageEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    lastMoveToFGEvent = usageEvent;
                }
            }
            if (lastMoveToFGEvent != null) {
                packageName = lastMoveToFGEvent.getPackageName();
            }
        //}
        //System.out.println(packageName);
        return packageName;
    }

}
