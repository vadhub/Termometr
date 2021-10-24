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
import com.vadim.termometr.screens.main.TemperPresenter;
import com.vadim.termometr.screens.main.TemperatureView;
import com.vadim.termometr.utils.temperprocessor.TemperatureFromPath;
import com.vadim.termometr.utils.Convertor;
import com.vadim.termometr.utils.NotificationHelper;

public class ServiceBackgroundTemperature extends Service implements SensorEventListener, TemperatureView {

    protected SensorManager mSensorManager;
    protected Sensor mTempSensor;
    protected Handler handler;
    protected boolean isLife = true;
    protected boolean isCelsia;
    private TemperPresenter presenter;
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
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mTempSensor = null;//mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        handler = new Handler();

        notificationHelper = new NotificationHelper();
        if (mTempSensor != null) {
            mSensorManager.registerListener(this, mTempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (mTempSensor == null) {
            presenter = new TemperPresenter(this);
            presenter.getTemperature();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        startForeground(
                NotificationHelper.NOTIFICATION_ID,
                notificationHelper.viewNotification(
                        event.values[0],
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

    @Override
    public void showTemperatureGPU(float t) {
        Runnable r = () -> {
            startForeground(
                    NotificationHelper.NOTIFICATION_ID,
                    notificationHelper.viewNotification(
                            t,
                            isCelsia,
                            ServiceBackgroundTemperature.this
                    )
            );
        };
        handler.postDelayed(r, 1000);
        if (!isLife) {
            handler.removeCallbacks(r);
            NotificationHelper.notificationClear(ServiceBackgroundTemperature.this);
        }
    }

    @Override
    public void showError(int str) {
        Toast.makeText(this, ""+getResources().getString(str), Toast.LENGTH_SHORT).show();
        isLife = false;
    }

    @Override
    public void savePathTemperature(String path) {

    }
}
