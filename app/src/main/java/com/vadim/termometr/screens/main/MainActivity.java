package com.vadim.termometr.screens.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.vadim.termometr.R;
import com.vadim.termometr.servicetemper.ServiceBackgroundTemperature;
import com.vadim.termometr.temperatureview.Termometr;
import com.vadim.termometr.utils.Convertor;
import com.vadim.termometr.utils.NotificationHelper;
import com.vadim.termometr.utils.SaveData;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class MainActivity extends AppCompatActivity implements SensorEventListener, TemperatureView {

    private Termometr thermometer;
    private AdView mAdView;
    private Switch aSwitchService;
    private TemperPresentor presenter;
    private SensorManager mSensorManager;
    private Sensor mTempSensor;
    private Intent service;
    private SaveData saveData;
    private static final int READ_REQUEST = 1123445;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            presenter.getTemperature();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new TemperPresentor(this);
        saveData = new SaveData(this);
        aSwitchService = (Switch) findViewById(R.id.switchService);
        thermometer = (Termometr) findViewById(R.id.thermometer);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        service = new Intent(this, ServiceBackgroundTemperature.class);
        service.putExtra("typeTemperature", saveData.loadChangedTypeTemperature());

        //Check sensor is null if null to commandline temperature
        if (mTempSensor==null) {
            requestPermissions();
            Log.i("presenter", "ok");
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
        aSwitchService.setChecked(saveData.loadState());
        if (saveData.loadState()) {
            startService(service);
            System.out.println("savedata true");
        } else {
            stopService(service);
            System.out.println("savedata false");
        }

        //switch
        aSwitchService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(service);
                } else {
                    stopService(service);
                    NotificationHelper.notificationClear(MainActivity.this);
                }
                saveData.saveState(isChecked);
            }
        });
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQUEST);
        } else {
            presenter.getTemperature();
        }
    }

    //menu option on action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.change_display_temper, menu);
        return true;
    }

    //switch between farengete and celsia
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.celsius_menu:
                saveData.saveChangedTypeTemperature(true);
                break;

            case R.id.fahrenheit_menu:
                saveData.saveChangedTypeTemperature(false);
                break;
        }
        service.putExtra("typeTemperature",saveData.loadChangedTypeTemperature());
        if (saveData.loadState()) {
            restartService();
        }
        return true;
    }

    //Process sensor
    protected void onResume() {
        super.onResume();
        if (mTempSensor!=null) {
            mSensorManager.registerListener(this, mTempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        thermometer.setCurrentTemp(event.values[0], saveData.loadChangedTypeTemperature());
        Log.i("temper", event.values[0]+"");
        getSupportActionBar().setTitle(Convertor.temperatureConvertor(event.values[0], saveData.loadChangedTypeTemperature()));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //restart notification service
    private void restartService() {
        stopService(service);
        NotificationHelper.notificationClear(this);
        startService(service);
    }

    @Override
    public void getTemperatureGPU(float t) {
        thermometer.setCurrentTemp(t, saveData.loadChangedTypeTemperature());
        Log.i("cpu", t+"");
        getSupportActionBar().setTitle(Convertor.temperatureConvertor(t, saveData.loadChangedTypeTemperature()));
    }

    @Override
    public void showError(int str) {
        Toast.makeText(this, ""+getResources().getString(str), Toast.LENGTH_SHORT).show();
    }
}