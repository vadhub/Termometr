package com.vadim.termometr;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class BroadcastReceverNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    private void notificationTemperature(String title, Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText("Notification text");

        Notification notification = builder.build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
