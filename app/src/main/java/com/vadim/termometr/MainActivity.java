package com.vadim.termometr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.PendingIntent.getBroadcast;

public class MainActivity extends AppCompatActivity {

    private Termometr thermometer;
    private float temperature;
    private Timer timer;

    private TextView temper;
    private AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId("ca-app-pub-1307594940838625~7527188963");

        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);
        temper = (TextView) findViewById(R.id.temterature);
        thermometer = (Termometr) findViewById(R.id.thermometer);


        simulateAmbientTemperature();
    }

    public void onClickList(View view){

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,BroadcastReceverNotification.class);
        final PendingIntent[] pendingIntent = new PendingIntent[1];
        intent.putExtra("temper", 30);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        pendingIntent[0] = getBroadcast(getApplicationContext(), 12, intent,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, 1000, pendingIntent[0]);
        Toast.makeText(this, "TRTRT", Toast.LENGTH_SHORT).show();

    }


    private void simulateAmbientTemperature() {
        timer = new Timer();


        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                temperature = getTemperatureCPU();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        thermometer.setCurrentTemp(temperature);
                        getSupportActionBar().setTitle(getString(R.string.app_name) + " : " + temperature);
                    }
                });
            }
        }, 0, 3500);
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



}