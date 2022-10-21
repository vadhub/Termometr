package com.vadim.termometr.utils;

import static com.vadim.termometr.utils.App.CHANNEL_ID;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.vadim.termometr.R;
import com.vadim.termometr.ui.main.screens.MainActivity;

public class NotificationHelper {
    public final Context context;
    public static final int NOTIFICATION_ID = 1223243;
    private final NotificationManager notificationManager;
    public NotificationHelper(Context context, NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
        this.context = context;
    }

    public NotificationCompat.Builder viewNotification() {

        Intent resultIntent = new Intent(context, MainActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setCustomContentView(thermometerView("32"))
                .setOngoing(false)
                .setAutoCancel(false)
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setTicker("32");
    }

    public void notificationClear() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public RemoteViews thermometerView(String temper) {
        RemoteViews thermometerNotify = new RemoteViews(
                context.getPackageName(),
                R.layout.termometer_notif
        );
        thermometerNotify.setTextViewText(
                R.id.textViewTemper,
                temper
        );
        return thermometerNotify;
    }
}
