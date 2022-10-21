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
import androidx.core.app.NotificationCompat;

import com.vadim.termometr.R;
import com.vadim.termometr.ui.main.temperatureview.Thermometer;
import com.vadim.termometr.utils.NotificationHelper;

public class ServiceBackgroundTemperature extends Service {

    private NotificationHelper notificationHelper;
    private NotificationCompat.Builder builder;
    private final IBinder binder = new TemperatureBinder();

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
        startForeground(NotificationHelper.NOTIFICATION_ID, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setTemperature(TextView temperature, Thermometer thermometer) {
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        Runnable runnable = () -> {
            float temp = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
            temperature.setText(temp+"");
            thermometer.setCurrentTemp(temp, true);
            builder.setCustomContentView(notificationHelper.thermometerView(temp, true));
            notificationHelper.getNotificationManager().notify(NotificationHelper.NOTIFICATION_ID, builder.build());
        };

        PeriodicTask periodicTask = new PeriodicTask(runnable);
        periodicTask.startPeriodic();
        System.out.println("ppppppppppppppppppp");
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
