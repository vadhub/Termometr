package com.vadim.termometr;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.vadim.termometr.utils.Convertor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ServiceBackgrounTemperature extends Service implements SensorEventListener {
    private float temperature;
    private SensorManager mSensorManager;
    private Sensor mTempSensor;
    private Handler handler;
    private boolean isLife;

    public static final String CHANNEL_ID = "service";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isLife = true;
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        handler = new Handler();

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)!=null){
            mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }else{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    temperature = getTemperatureCPU();
                    outTemper(temperature);

                    handler.postDelayed(this, 1000);
                    if(!isLife){
                        handler.removeCallbacks(this);
                        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(1);
                    }
                }
            });
        }
        createChannel();
    }

    private float getTemperatureCPU(){
        Process process;

        try {
            process = Runtime.getRuntime().exec("cat sys/devices/virtual/thermal/thermal_zone0/temp");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if(line!=null) {
                float temp = Float.parseFloat(line);
                return temp / 1000.0f;
            }else{
                return 30.0f;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    public void outTemper(float temperat){

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getTemperatureChanged(temperat, loadChangedTypeTemperature()))
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setTicker(getTemperatureChanged(temperat, loadChangedTypeTemperature())).build();

        System.out.println(loadChangedTypeTemperature());
        Toast.makeText(this, ""+loadChangedTypeTemperature(), Toast.LENGTH_SHORT).show();

        startForeground(1, builder);
    }

    private void createChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private boolean loadChangedTypeTemperature(){
        SharedPreferences sPref = getSharedPreferences("temperature_", MODE_PRIVATE);
        return sPref.getBoolean("isCheckTypeTemperature", true);
    }

    private String getTemperatureChanged(float temperature, boolean isCelsia){
        String temper = String.format("%.0f", temperature) + "C°";

        if(!isCelsia){
            float fareng = Convertor.fahrenheit(temperature);
            temper = String.format("%.0f", fareng) + "F°";
        }
        return temper;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
            temperature = event.values[0];
            outTemper(temperature);
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
