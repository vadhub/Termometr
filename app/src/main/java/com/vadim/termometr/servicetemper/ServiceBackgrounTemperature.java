package com.vadim.termometr.servicetemper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.vadim.termometr.MainActivity;
import com.vadim.termometr.R;
import com.vadim.termometr.utils.Convertor;
import com.vadim.termometr.utils.TemperatureProcessor;
import com.vadim.termometr.viewable.ViewableResult;


public class ServiceBackgrounTemperature extends Service implements SensorEventListener, ViewableResult {
    protected float temperature;
    protected SensorManager mSensorManager;
    protected Sensor mTempSensor;
    protected Handler handler;
    protected boolean isLife;
    protected boolean isCelsia;
    private TemperatureProcessor temperatureProcessor;

    public static final String CHANNEL_ID = "service";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isLife = true;
        isCelsia = intent.getExtras().getBoolean("typeTemperature");
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        handler = new Handler();
        temperatureProcessor = new TemperatureProcessor();

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)!=null){
            mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }else{
            updateResult();
        }
        createChannel();
    }

    @Override
    public void updateResult(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                temperature = temperatureProcessor.getTemperatureCPU();
                outTemper(temperature, isCelsia);
                handler.postDelayed(this, 1000);
                if(!isLife){
                    handler.removeCallbacks(this);
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                }
            }
        });
    }

    @Override
    public void outTemper(float temperat, boolean typeTemper){

        RemoteViews termometerNotif = new RemoteViews(getPackageName(), R.layout.termometer_notif);
        termometerNotif.setTextViewText(R.id.textViewTemper, getTemperatureChanged(temperat, typeTemper));

       //.setContentTitle(getTemperatureChanged(temperat, typeTemper));

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setCustomContentView(termometerNotif)
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setTicker(getTemperatureChanged(temperat, typeTemper)).build();

        startForeground(1, builder);
    }

    @Override
    public void createChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public String getTemperatureChanged(float temperature, boolean isCelsia) {
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
            outTemper(temperature, isCelsia);
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
