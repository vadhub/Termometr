package com.vadim.termometr.ui.main.servicetemper;

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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.vadim.termometr.R;
import com.vadim.termometr.ui.main.temperatureview.Thermometer;
import com.vadim.termometr.utils.App;
import com.vadim.termometr.utils.Convertor;
import com.vadim.termometr.utils.NotificationHelper;
import com.vadim.termometr.utils.SaveData;

public class ServiceBackgroundTemperature extends Service {

    private NotificationHelper notificationHelper;
    private NotificationCompat.Builder builder;
    private final IBinder binder = new TemperatureBinder();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private SaveData saveData;

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
        saveData = new SaveData(this);
        notificationHelper = new NotificationHelper(this);
        builder = notificationHelper.viewNotification();
        startForeground(App.NOTIFICATION_ID, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setTemperature(TextView temperature, Thermometer thermometer) {
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        boolean isCelsius = saveData.loadChangedTypeTemperature();
        @SuppressLint("SetTextI18n") Runnable runnable = () -> {
            float temp = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
            String temperString = Convertor.temperatureConvertor(temp, isCelsius);
            handler.post(() -> {
                temperature.setText(temperString);
            });
            thermometer.setCurrentTemp(temp, isCelsius);
            builder.setCustomContentView(notificationHelper.thermometerView(temperString));
            App.notificationManager.notify(App.NOTIFICATION_ID, builder.build());
        };

        PeriodicTask periodicTask = new PeriodicTask(runnable);
        periodicTask.startPeriodic();
    }

    public void setTypeTemperature(boolean isCelsius) {
        saveData.saveChangedTypeTemperature(isCelsius);
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
