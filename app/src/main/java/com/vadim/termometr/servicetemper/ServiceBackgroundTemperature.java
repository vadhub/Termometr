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
import com.vadim.termometr.temperprocessor.TemperatureFromPath;
import com.vadim.termometr.utils.Convertor;
import com.vadim.termometr.utils.NotificationHelper;

public class ServiceBackgroundTemperature extends Service implements SensorEventListener {
    protected float temperature;
    protected SensorManager mSensorManager;
    protected Sensor mTempSensor;
    protected Handler handler;
    protected boolean isLife;
    protected boolean isCelsia;
    private TemperatureFromPath temperatureFromPath = new TemperatureFromPath();
    private NotificationHelper notificationHelper;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isLife = true;
        isCelsia = intent.getExtras().getBoolean("typeTemperature");
        Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        handler = new Handler();

        notificationHelper = new NotificationHelper();
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)!=null) {
            mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        } else {
            updateResult();
        }
    }

    public void updateResult() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                temperature = Convertor.temperatureHuman(temperatureFromPath.getTemperature());
                startForeground(
                        NotificationHelper.NOTIFICATION_ID,
                        notificationHelper.viewNotification(
                                temperature,
                                isCelsia,
                                ServiceBackgroundTemperature.this
                        )
                );
                handler.postDelayed(this, 2000);
                if (!isLife) {
                    handler.removeCallbacks(this);
                    NotificationHelper.notificationClear(ServiceBackgroundTemperature.this);
                }
            }
        });
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
                        this
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
        isLife =false;
    }
}
