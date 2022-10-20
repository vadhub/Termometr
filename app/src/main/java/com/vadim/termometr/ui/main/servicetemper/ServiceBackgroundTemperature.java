package com.vadim.termometr.ui.main.servicetemper;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.vadim.termometr.R;
import com.vadim.termometr.utils.NotificationHelper;

public class ServiceBackgroundTemperature extends Service implements SensorEventListener {

    protected SensorManager mSensorManager;
    protected Sensor mTempSensor;
    protected boolean isLife = true;
    protected boolean isCelsia;
    private NotificationHelper notificationHelper;
    private String path = "";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isLife = true;
        isCelsia = intent.getExtras().getBoolean("typeTemperature");
        path = intent.getExtras().getString("temperPath");
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        notificationHelper = new NotificationHelper();
        if (mTempSensor != null) {
            mSensorManager.registerListener(
                    this,
                    mTempSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        startForegroundNotification(event.values[0]);
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
        if (mTempSensor != null) {
            mSensorManager.unregisterListener(this);
        }
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
