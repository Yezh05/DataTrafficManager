package edu.yezh.datatrafficmanager.tools;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import edu.yezh.datatrafficmanager.MainActivity;
import edu.yezh.datatrafficmanager.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationTools {
    public static void setNotification(Context context, String title, String message) {

        String id = "my_channel_01";
        String name="流量使用通知";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            //Toast.makeText(context, mChannel.toString(), Toast.LENGTH_SHORT).show();
            //System.out.println(mChannel.toString());
            notificationManager.createNotificationChannel(mChannel);
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0)  ;
            notification = new Notification.Builder(context)
                    .setChannelId(id)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher).build();
        } else {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0)  ;
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true);
            //.setChannel(id);//无效
            notification = notificationBuilder.build();
        }
        notificationManager.notify(111123, notification);
    }
}
