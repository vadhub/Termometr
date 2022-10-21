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

        @SuppressLint("SetTextI18n") Runnable runnable = () -> {
            try {
                float temp = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
                handler.post(() -> {
                    temperature.setText(temp+"");
                });
                thermometer.setCurrentTemp(temp, true);
                builder.setCustomContentView(notificationHelper.thermometerView(temp, true));
                App.notificationManager.notify(App.NOTIFICATION_ID, builder.build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        PeriodicTask periodicTask = new PeriodicTask(runnable);
        periodicTask.startPeriodic();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(
                getApplicationContext(),
                getResources().getString(R.string.service_stop),
                Toast.LENGTH_SHORT
        ).show();
        notificationHelper.notificationClear();
        builder = null;
        notificationHelper = null;
    }


}
