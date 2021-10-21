package com.vadim.termometr.servicetemper;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.vadim.termometr.R;
import com.vadim.termometr.utils.temperprocessor.TemperatureFromPath;
import com.vadim.termometr.utils.Convertor;
import com.vadim.termometr.utils.NotificationHelper;

public class ServiceBackgroundTemperature extends Service implements SensorEventListener {
    protected float temperature;
    protected SensorManager mSensorManager;
    protected Sensor mTempSensor;
    protected Handler handler;
    protected boolean isLife = true;
    protected boolean isCelsia;
    private TemperatureFromPath temperatureFromPath = new TemperatureFromPath();
    private NotificationHelper notificationHelper;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isLife = true;
        isCelsia = intent.getExtras().getBoolean("typeTemperature");

        if (mTempSensor == null) {
            updateResult();
            System.out.println(isLife+"________________________________________________");
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mTempSensor = null;//mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (mTempSensor != null) {
            mSensorManager.registerListener(this, mTempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        handler = new Handler();
        notificationHelper = new NotificationHelper();
    }

    public void updateResult() {
        Runnable r = () -> {
            temperature = Convertor.temperatureHuman(temperatureFromPath.getTemperature());
            startForeground(
                    NotificationHelper.NOTIFICATION_ID,
                    notificationHelper.viewNotification(
                            temperature,
                            isCelsia,
                            ServiceBackgroundTemperature.this
                    )
            );
        };
        handler.postDelayed(r, 1000);

        System.out.println(isLife);
        if (!isLife) {
            handler.removeCallbacks(r);
            NotificationHelper.notificationClear(ServiceBackgroundTemperature.this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        temperature = event.values[0];
        startForeground(
                NotificationHelper.NOTIFICATION_ID,
                notificationHelper.viewNotification(
                        temperature,
                        isCelsia,
                        ServiceBackgroundTemperature.this
                )
        );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(
                getApplicationContext(),
                getResources().getString(R.string.service_stop),
                Toast.LENGTH_SHORT
        ).show();
        isLife = false;
    }
}
