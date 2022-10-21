package com.vadim.termometr.ui.main.servicetemper;

import static com.vadim.termometr.utils.NotificationHelper.NOTIFICATION_ID;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.vadim.termometr.ui.main.temperatureview.Thermometer;
import com.vadim.termometr.utils.App;
import com.vadim.termometr.utils.NotificationHelper;

public class ServiceBackgroundTemperature extends Service {

    private NotificationHelper notificationHelper;
    private NotificationCompat.Builder builder;
    private final IBinder binder = new TemperatureBinder();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public class TemperatureBinder extends Binder {
        public ServiceBackgroundTemperature getService() {
            return ServiceBackgroundTemperature.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper = new NotificationHelper(this);
        builder = notificationHelper.viewNotification();
        showNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setTemperature(TextView temperature, Thermometer thermometer) {
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        @SuppressLint("SetTextI18n") Runnable runnable = () -> {

            float temp = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
            handler.post(() -> {
                temperature.setText(temp+" C°");
            });
            thermometer.setCurrentTemp(temp);
            builder.setCustomContentView(notificationHelper.thermometerView(temp+" C°"));
            App.notificationManager.notify(NOTIFICATION_ID, builder.build());
        };

        PeriodicTask periodicTask = new PeriodicTask(runnable);
        periodicTask.startPeriodic();
    }

    public void cleanedNotification() {
        notificationHelper.notificationClear();
    }

    public void showNotification() {
        startForeground(NOTIFICATION_ID, builder.build());
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
    }


}
