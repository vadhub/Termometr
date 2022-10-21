package com.vadim.termometr.ui.main.servicetemper;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.vadim.termometr.R;
import com.vadim.termometr.ui.main.temperatureview.Thermometer;
import com.vadim.termometr.utils.NotificationHelper;

public class ServiceBackgroundTemperature extends Service {

    //todo #4 save and read state from prefer

    protected boolean isLife = true;
    protected boolean isCelsia;
    private NotificationHelper notificationHelper;
    private final IBinder binder = new TemperatureBinder();

    public class TemperatureBinder extends Binder {
        public ServiceBackgroundTemperature getService() {
            return ServiceBackgroundTemperature.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isLife = true;
        return super.onStartCommand(intent, flags, startId);
    }

    public void setTemperature(TextView temperature, Thermometer thermometer) {
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        Runnable runnable = () -> {
            float temp = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
            temperature.setText(temp+"");
            thermometer.setCurrentTemp(temp, true);
        };

        PeriodicTask periodicTask = new PeriodicTask(runnable);
        periodicTask.startPeriodic();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper = new NotificationHelper();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
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
