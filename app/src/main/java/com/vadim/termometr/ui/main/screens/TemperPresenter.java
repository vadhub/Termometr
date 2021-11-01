package com.vadim.termometr.ui.main.screens;

import android.os.Handler;
import android.util.Log;

import com.vadim.termometr.R;
import com.vadim.termometr.utils.temperprocessor.TemperatureFromPath;
import com.vadim.termometr.utils.Convertor;

import java.util.Random;

public class TemperPresenter {

    private TemperatureView view;
    private TemperatureFromPath temperature = new TemperatureFromPath();
    private final Handler handler = new Handler();
    private Runnable runnable;
    boolean isRunning = false;

    public TemperPresenter(TemperatureView view) {
        this.view= view;
    }

    public void getTemperature() {
        Log.i("testTemer", view.loadPathTemperature()+"");
        if (view.loadPathTemperature().equals("")) {
            checkPath();
        } else {
            float t = Convertor.temperatureHuman(temperature.cat(view.loadPathTemperature()));//Convertor.temperatureHuman());
            runRunnable(t);
        }
    }

    public void stopRunnable() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void runRunnable(float t) {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    isRunning=true;
                    System.out.println(Math.random()*10);
                    view.showTemperatureGPU(t);
                    handler.postDelayed(this, 5000);
                } catch (Exception e) {
                    e.printStackTrace();
                    isRunning = false;
                }
            }
        };
        if (!isRunning) {
            runnable.run();
        }

    }
    private void checkPath() {
        if (!temperature.getTemperaturePath().equals("")) {
            view.savePathTemperature(temperature.getTemperaturePath());
        } else {
            view.showError(R.string.warning);
        }
    }

}
