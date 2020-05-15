package edu.yezh.datatrafficmanager.tools;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import androidx.core.app.NotificationCompat;

import edu.yezh.datatrafficmanager.ui.MainActivity;
import edu.yezh.datatrafficmanager.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationTools {
    public static void setNotification(final Context context, final String title, final String message, final boolean isOngoing,int id) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String sid = "my_channel_01";
                String name="流量使用通知";
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                Notification notification = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel mChannel = new NotificationChannel(sid, name, NotificationManager.IMPORTANCE_LOW);
                    //Toast.makeText(context, mChannel.toString(), Toast.LENGTH_SHORT).show();
                    //System.out.println(mChannel.toString());
                    notificationManager.createNotificationChannel(mChannel);
                    Intent intent = new Intent(context, MainActivity.class);
                    PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0)  ;
                    notification = new Notification.Builder(context)
                            .setChannelId(sid)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setContentIntent(pendingIntent)
                            .setOngoing(isOngoing)
                            .setSmallIcon(R.mipmap.ic_launcher).build();
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0)  ;
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setOngoing(isOngoing);
                    //.setChannel(id);//无效
                    notification = notificationBuilder.build();
                }
                notificationManager.notify(id, notification);
            }
        },1000L);
    }
}
