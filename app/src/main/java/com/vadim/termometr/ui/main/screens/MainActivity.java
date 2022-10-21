package com.vadim.termometr.ui.main.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;

import com.vadim.termometr.R;
import com.vadim.termometr.ui.main.servicetemper.ServiceBackgroundTemperature;
import com.vadim.termometr.ui.main.temperatureview.Thermometer;

public class MainActivity extends AppCompatActivity {

    private ServiceBackgroundTemperature serviceBackgroundTemperature;
    private boolean mShouldUnbind;
    private Thermometer thermometer;
    private TextView temperature;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServiceBackgroundTemperature.TemperatureBinder binder = (ServiceBackgroundTemperature.TemperatureBinder) service;
            serviceBackgroundTemperature = binder.getService();
            serviceBackgroundTemperature.setTemperature(temperature, thermometer);
            mShouldUnbind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBackgroundTemperature = null;
            mShouldUnbind = false;
        }
    };

    void doBindService() {
        Intent intent = new Intent(this, ServiceBackgroundTemperature.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Switch aSwitchService = (Switch) findViewById(R.id.switchService);
        aSwitchService.setChecked(true);
        thermometer = findViewById(R.id.thermometer);
        temperature = findViewById(R.id.temperature);
        doBindService();

        //switch
        aSwitchService.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                doBindService();
            } else {
                doUnbindService();
            }
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
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.celsius_menu:
                serviceBackgroundTemperature.setTypeTemperature(true);
                break;

            case R.id.fahrenheit_menu:
                serviceBackgroundTemperature.setTypeTemperature(false);
                break;
        }

        return true;
    }

    void doUnbindService() {
        if (mShouldUnbind) {
            unbindService(connection);
            mShouldUnbind = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}