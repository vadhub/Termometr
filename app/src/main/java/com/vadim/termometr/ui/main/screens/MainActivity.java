package com.vadim.termometr.ui.main.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;

import com.vadim.termometr.R;
import com.vadim.termometr.ui.main.temperatureview.Thermometer;
import com.vadim.termometr.utils.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    //todo #2 create bound with service

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Switch aSwitchService = (Switch) findViewById(R.id.switchService);
        Thermometer thermometer = findViewById(R.id.thermometer);
        TextView temperature = findViewById(R.id.temperature);

        //todo #3 move in service it ----------->
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        float temp = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
        //<---------------

        temperature.setText(temp+"");
        thermometer.setCurrentTemp(temp, true);

        //switch
        aSwitchService.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

            } else {

                NotificationHelper.notificationClear(MainActivity.this);
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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.celsius_menu:

                break;

            case R.id.fahrenheit_menu:

                break;
        }

        return true;
    }

}