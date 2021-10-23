package com.vadim.termometr.utils;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.vadim.termometr.R;
import com.vadim.termometr.screens.main.MainActivity;

public class NotificationHelper extends Application {

    public static final String CHANNEL_ID = "service";
    public static final int NOTIFICATION_ID = 2;

    @Override
    public void onCreate() {
        super.onCreate();
        createChannel();
    }

    public static void notificationClear(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public Notification viewNotification(float temper, boolean typeTemper, Context context) {
        RemoteViews termometerNotif = new RemoteViews(
                context.getPackageName(),
                R.layout.termometer_notif
        );
        termometerNotif.setTextViewText(
                R.id.textViewTemper,
                Convertor.temperatureConvertor(temper, typeTemper)
        );

        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        Notification builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setCustomContentView(termometerNotif)
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setTicker(Convertor.temperatureConvertor(temper, typeTemper)).build();

        return builder;
    }
}
