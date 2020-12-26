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
        Toast.makeText(context, (int) temp, Toast.LENGTH_SHORT).show();

    }

}
