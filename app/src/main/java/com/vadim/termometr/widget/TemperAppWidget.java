package com.vadim.termometr.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.RemoteViews;

import com.vadim.termometr.MainActivity;
import com.vadim.termometr.R;
import com.vadim.termometr.ServiceBackgrounTemperature;

public class TemperAppWidget extends AppWidgetProvider {

    PendingIntent service;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Intent intentService = new Intent(context, ServiceWidget.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(service == null){
            service = PendingIntent.getService(context, 0, intentService, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 30000, service);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.temper_app_widget);
        views.setOnClickPendingIntent(R.id.layout, pendingIntent);
    }

}