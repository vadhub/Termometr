package com.vadim.termometr.ui.main.servicetemper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.vadim.termometr.R;
import com.vadim.termometr.utils.NotificationHelper;

public class ServiceBackgroundTemperature extends Service {

    //todo #1 create binder
    //todo #4 save and read state from prefer

    protected boolean isLife = true;
    protected boolean isCelsia;
    private NotificationHelper notificationHelper;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isLife = true;
        isCelsia = intent.getExtras().getBoolean("typeTemperature");
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper = new NotificationHelper();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(
                getApplicationContext(),
                getResources().getString(R.string.service_stop),
                Toast.LENGTH_SHORT
        ).show();

        NotificationHelper.notificationClear(ServiceBackgroundTemperature.this);
        isLife = false;
    }

    private void startForegroundNotification(float t) {
        startForeground(
                NotificationHelper.NOTIFICATION_ID,
                notificationHelper.viewNotification(
                        t,
                        isCelsia,
                        ServiceBackgroundTemperature.this
                )
        );
    }

}
