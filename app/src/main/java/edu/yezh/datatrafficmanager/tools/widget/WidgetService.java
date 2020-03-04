package edu.yezh.datatrafficmanager.tools.widget;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class WidgetService extends Service implements Runnable {
    boolean threadRunning=false;
    String usageString;String nowSpeedString;

    public WidgetService(String usageString, String nowSpeedString) {
        this.usageString = usageString;
        this.nowSpeedString = nowSpeedString;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        /*while (!Thread.interrupted()){

        }*/

        //DesktopWidget.updateAppWidget(this,usageString,nowSpeedString);
    }
}
