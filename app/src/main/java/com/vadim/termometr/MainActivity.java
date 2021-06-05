package com.vadim.termometr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.os.Handler;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Termometr thermometer;
    private float temperature;

    private SensorManager mSensorManager;
    private  Sensor mTempSensor;
    private float temper_aut;
    private AdView mAdView;
    private Switch aSwitchService;
    private SharedPreferences sPref;
    private Handler handler;
    private Intent service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aSwitchService = (Switch) findViewById(R.id.switchService);
        thermometer = (Termometr) findViewById(R.id.thermometer);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        handler = new Handler();

        service = new Intent(this, ServiceBackgrounTemperature.class);
        //Check sensor or commandline
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)!=null){
            mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }else{
            simulateAmbientTemperature();
        }

        //AdMob
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //check on off Service
        aSwitchService.setChecked(load());
        if(load()){
            startService(service);
        }else{
            stopService(service);
        }

        //switch
        aSwitchService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startService(service);
                }else{
                    stopService(service);
                    notificationClear(1);
                }
                save(isChecked);

            }
        });
    }

    //Start Handler on measure C
    private void simulateAmbientTemperature() {
        final String[] str = new String[1];

        handler.post(new Runnable() {
            @Override
            public void run() {
                temperature = getTemperatureCPU();
                str[0] = String.format("%.0f", temperature);
                thermometer.setCurrentTemp(temperature);
                getSupportActionBar().setTitle(getString(R.string.app_name) + " : " + str[0]+"C°");
                handler.postDelayed(this, 1000);
            }
        });

    }

    //Process sensor
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mTempSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        temper_aut = event.values[0];

        thermometer.setCurrentTemp(temper_aut);

        getSupportActionBar().setTitle(getString(R.string.app_name) + " : " + temper_aut+"C°");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


//save check
    private void save(boolean isCheck){
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("isChecked", isCheck);
        ed.commit();
    }

    private boolean load(){
        sPref = getPreferences(MODE_PRIVATE);
        return sPref.getBoolean("isChecked", true);
    }

    //Temperature
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
                Toast.makeText(this, R.string.warning, Toast.LENGTH_SHORT).show();
                return 30.0f;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    private void notificationClear(int NOTIFICATION_ID){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

}