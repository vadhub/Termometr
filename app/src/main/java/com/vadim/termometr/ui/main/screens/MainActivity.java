package com.vadim.termometr.ui.main.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;

import com.vadim.termometr.R;
import com.vadim.termometr.ui.main.servicetemper.ServiceBackgroundTemperature;
import com.vadim.termometr.ui.main.temperatureview.Thermometer;
import com.vadim.termometr.utils.Convertor;
import com.vadim.termometr.utils.NotificationHelper;
import com.vadim.termometr.utils.SaveData;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Thermometer thermometer;
    private Intent service;
    private SaveData saveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveData = new SaveData(this);
        Switch aSwitchService = (Switch) findViewById(R.id.switchService);
        thermometer = (Thermometer) findViewById(R.id.thermometer);
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        float temp = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
        System.out.println(temp + "--------------");

        service = new Intent(MainActivity.this, ServiceBackgroundTemperature.class);
        service.putExtra("typeTemperature", saveData.loadChangedTypeTemperature());
        service.putExtra("temperPath", saveData.loadPath());

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

}