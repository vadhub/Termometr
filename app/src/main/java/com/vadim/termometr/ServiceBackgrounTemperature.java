package com.vadim.termometr;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceBackgrounTemperature extends Service {
    private float temperature;
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                temperature = getTemperatureCPU();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        outTemper(temperature);
                    }
                });
            }

            private void runOnUiThread(Runnable runnable) {
            }

        }, 0, 3500);
        return START_STICKY;
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

        String t = String.valueOf(temperat);

        Notification builder = new NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(t)
                .setOngoing(true)
                .setAutoCancel(false)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder);


    }
}
