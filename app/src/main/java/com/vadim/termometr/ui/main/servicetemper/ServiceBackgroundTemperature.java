package com.vadim.termometr.ui.main.servicetemper;

import static com.vadim.termometr.utils.NotificationHelper.NOTIFICATION_ID;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.vadim.termometr.ui.main.temperatureview.Thermometer;
import com.vadim.termometr.utils.App;
import com.vadim.termometr.utils.NotificationHelper;

public class ServiceBackgroundTemperature extends Service {

    private NotificationHelper notificationHelper;
    private PeriodicTask periodicTask;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private final IBinder binder = new TemperatureBinder();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public class TemperatureBinder extends Binder {
        public ServiceBackgroundTemperature getService() {
            return ServiceBackgroundTemperature.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = App.notificationManager;
        notificationHelper = new NotificationHelper(this, notificationManager);
        builder = notificationHelper.viewNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setTemperature(TextView temperature, Thermometer thermometer) {
        startForeground(NOTIFICATION_ID, builder.build());

        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        Runnable runnable = () -> {

            float temp = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
            handler.post(() -> {
                temperature.setText(temp+" C°");
            });
            thermometer.setCurrentTemp(temp);
            builder.setCustomContentView(notificationHelper.thermometerView(temp+" C°"));
            notificationManager.notify(NOTIFICATION_ID, builder.build());
            notificationManager.cancel(NOTIFICATION_ID);
        };

        periodicTask = new PeriodicTask(runnable);
        periodicTask.startPeriodic();
    }

    public void cleanedNotification() {
        periodicTask.stopPeriodic();
        if (periodicTask != null) {
            periodicTask = null;
        }
        stopForeground(Service.STOP_FOREGROUND_LEGACY);
        notificationManager.cancel(NOTIFICATION_ID);
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationHelper.notificationClear();
        builder = null;
        notificationHelper = null;
        if (periodicTask != null) {
            periodicTask.stopPeriodic();
            periodicTask = null;
        }

    }


}
