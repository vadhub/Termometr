package com.vadim.termometr.servicetemper;

import android.app.NotificationManager;

import android.app.Service;
import android.content.Context;
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
import com.vadim.termometr.temperprocessor.TemperatureProcessor;
import com.vadim.termometr.utils.NotificationHelper;
import com.vadim.termometr.viewable.ViewableResult;


public class ServiceBackgroundTemperature extends Service implements SensorEventListener, ViewableResult {
    protected float temperature;
    protected SensorManager mSensorManager;
    protected Sensor mTempSensor;
    protected Handler handler;
    protected boolean isLife;
    protected boolean isCelsia;
    private TemperatureProcessor temperatureProcessor;
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
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        handler = new Handler();
        temperatureProcessor = new TemperatureProcessor();
        notificationHelper = new NotificationHelper();
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)!=null) {
            mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        } else {
            updateResult();
        }
    }

    @Override
    public void updateResult() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                temperature = temperatureProcessor.getTemperatureCPU();
                startForeground(1, notificationHelper.viewNotification(temperature, isCelsia));
                handler.postDelayed(this, 1000);
                if(!isLife){
                    handler.removeCallbacks(this);
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
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
        startForeground(1, notificationHelper.viewNotification(temperature, isCelsia));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        String resource = getResources().getString(R.string.service_stop);
        Toast.makeText(getApplicationContext(), resource, Toast.LENGTH_SHORT).show();
        isLife =false;
    }
}
