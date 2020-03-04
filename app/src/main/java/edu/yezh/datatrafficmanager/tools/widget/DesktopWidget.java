package edu.yezh.datatrafficmanager.tools.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import java.util.LinkedList;
import java.util.Queue;

import edu.yezh.datatrafficmanager.R;

public class DesktopWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int i=0;i<appWidgetIds.length;i++){
            widgetIds.add(appWidgetIds[i]);
        }
        //context.startService()
        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int i=0;i<appWidgetIds.length;i++){
            if (widgetIds.contains(appWidgetIds[i])){
                widgetIds.remove(appWidgetIds[i]);
            }
        }
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {

        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {

        super.onDisabled(context);
    }

    private static Queue<Integer> widgetIds = new LinkedList<Integer>();
    public static void updateAppWidget(Context context,String usageString,String usageInfoString){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.TextViewWidgetRow1,usageString);
        views.setTextViewText(R.id.TextViewWidgetRow2,usageInfoString);
        final int WIDGET_SIZE=widgetIds.size();
        for (int i=0;i<WIDGET_SIZE;i++){
            int appWidgetId = widgetIds.poll();
            appWidgetManager.updateAppWidget(appWidgetId,views);
            widgetIds.add(appWidgetId);
        }
    }
}
