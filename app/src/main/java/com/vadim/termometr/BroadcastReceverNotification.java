package com.vadim.termometr;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class BroadcastReceverNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        float temp = intent.getFloatExtra("temper", 0);
        String srt = String.valueOf(temp);
        notificationTemperature(srt, context);
    }

    private void notificationTemperature(String title, Context context){
        Notification builder = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText("Notification text")
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();



        Toast.makeText(context, "ypoug", Toast.LENGTH_SHORT).show();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder);


    }
}
