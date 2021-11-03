package com.vadim.termometr.ui.main.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.widget.Switch;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.vadim.termometr.R;
import com.vadim.termometr.ui.main.servicetemper.ServiceBackgroundTemperature;
import com.vadim.termometr.ui.main.temperatureview.Termometr;
import com.vadim.termometr.utils.Convertor;
import com.vadim.termometr.utils.NotificationHelper;
import com.vadim.termometr.utils.SaveData;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class MainActivity extends AppCompatActivity implements SensorEventListener, TemperatureView {

    private Termometr thermometer;
    private AdView mAdView;
    private Switch aSwitchService;
    private TemperPresenter presenter;
    private SensorManager mSensorManager;
    private Sensor mTempSensor;
    private Intent service;
    private SaveData saveData;
    private static final int READ_REQUEST = 11235;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter = new TemperPresenter(this);

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveData = new SaveData(this);
        aSwitchService = (Switch) findViewById(R.id.switchService);
        thermometer = (Termometr) findViewById(R.id.thermometer);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        service = new Intent(MainActivity.this, ServiceBackgroundTemperature.class);
        service.putExtra("typeTemperature", saveData.loadChangedTypeTemperature());
        service.putExtra("temperPath", saveData.loadPath());

        //Check sensor is null if null to commandline temperature
        if (mTempSensor == null) {
            checkPermissions();
        }

        presenter.getTemperature();
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
        } else {
            stopService(service);
        }

        //switch
        aSwitchService.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startService(service);
            } else {
                stopService(service);
                NotificationHelper.notificationClear(MainActivity.this);
            }
            saveData.saveState(isChecked);
        });
    }

    private void checkPermissions() {
        if (
                ContextCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_REQUEST
            );
        } else {
            presenter = new TemperPresenter(this);
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
        service.putExtra("typeTemperature", saveData.loadChangedTypeTemperature());

        if (saveData.loadState()) {
            restartService();
        }
        return true;
    }

    //Process sensor
    protected void onResume() {
        super.onResume();
        if (mTempSensor != null) {
            mSensorManager.registerListener(
                    this,
                    mTempSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        boolean type = saveData.loadChangedTypeTemperature();
        thermometer.setCurrentTemp(event.values[0], type);
        getSupportActionBar().setTitle(
                        Convertor.temperatureConvertor(
                                        event.values[0],
                                        type
                                )
                );
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
    public void showTemperatureGPU(float temperature) {
        boolean type = saveData.loadChangedTypeTemperature();
        thermometer.setCurrentTemp(temperature, type);
        getSupportActionBar().setTitle(
                        Convertor.temperatureConvertor(
                                        temperature,
                                        type
                        )
                );
    }

    @Override
    public void showError(int str) {
        Toast.makeText(this, ""+getResources().getString(str), Toast.LENGTH_SHORT).show();
        presenter = null;
    }

    @Override
    public void savePathTemperature(String path) {
        saveData.savePath(path);
        Log.i("save", path);
    }

    @Override
    public String loadPathTemperature() {
        Log.i("load", saveData.loadPath());
        return saveData.loadPath();
    }
}