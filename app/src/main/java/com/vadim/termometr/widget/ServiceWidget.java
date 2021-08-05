package com.vadim.termometr.widget;

import android.content.Intent;

import com.vadim.termometr.ServiceBackgrounTemperature;

public class ServiceWidget extends ServiceBackgrounTemperature {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isLife=true;
        return START_NOT_STICKY;
    }



    @Override
    public void outTemper(float temperat, boolean typeTemper) {
        super.outTemper(temperat, typeTemper);
    }
}
