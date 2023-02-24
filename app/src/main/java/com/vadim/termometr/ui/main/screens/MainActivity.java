package com.vadim.termometr.ui.main.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import com.vadim.termometr.R;
import com.vadim.termometr.ui.main.servicetemper.ServiceBackgroundTemperature;
import com.vadim.termometr.ui.main.temperatureview.Thermometer;
import com.yandex.mobile.ads.banner.AdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;

public class MainActivity extends AppCompatActivity {

    private ServiceBackgroundTemperature serviceBackgroundTemperature;
    private boolean mShoulBind;
    private Thermometer thermometer;
    private TextView temperature;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch aSwitchService;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServiceBackgroundTemperature.TemperatureBinder binder = (ServiceBackgroundTemperature.TemperatureBinder) service;
            serviceBackgroundTemperature = binder.getService();
            serviceBackgroundTemperature.setTemperature(temperature, thermometer);
            mShoulBind = true;
            aSwitchService.setChecked(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBackgroundTemperature = null;
            mShoulBind = false;
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

        BannerAdView mBanner = (BannerAdView) findViewById(R.id.adView);
        //R-M-DEMO-320x50
        mBanner.setAdUnitId("R-M-1993169-1");
        mBanner.setAdSize(AdSize.stickySize(AdSize.FULL_SCREEN.getWidth()));
        AdRequest adRequest = new AdRequest.Builder().build();
        mBanner.loadAd(adRequest);

        aSwitchService = (Switch) findViewById(R.id.switchService);
        aSwitchService.setChecked(mShoulBind);
        thermometer = findViewById(R.id.thermometer);
        temperature = findViewById(R.id.temperature);
        doBindService();

        //switch
        aSwitchService.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (mShoulBind) serviceBackgroundTemperature.cleanedNotification();
                serviceBackgroundTemperature.setTemperature(temperature, thermometer);
            } else {
                serviceBackgroundTemperature.cleanedNotification();
            }
        });
    }

    void doUnbindService() {
        if (mShoulBind) {
            unbindService(connection);
            mShoulBind = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}