package edu.yezh.datatrafficmanager.tools.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;

public class ScreenBroadcastReceiver extends BroadcastReceiver {
    long startTrafficData=0,endTrafficData=0;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            System.out.println("锁屏");
            startTrafficData = TrafficStats.getMobileRxBytes()+TrafficStats.getMobileTxBytes();
        } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
            System.out.println("解锁");
        }else if(action.equals(Intent.ACTION_USER_PRESENT)){
            endTrafficData = TrafficStats.getMobileRxBytes()+TrafficStats.getMobileTxBytes();
            System.out.println("锁屏期间消耗流量"+(endTrafficData-startTrafficData));
            System.out.println("开屏");
            handle.handle(endTrafficData-startTrafficData);
        }
    }
    private ScreenBroadcastReceiver.Handle handle;
    public interface Handle{
        public void handle(long s);
    };
    public void setHandle(ScreenBroadcastReceiver.Handle handle) {
        this.handle = handle;
    }
}
