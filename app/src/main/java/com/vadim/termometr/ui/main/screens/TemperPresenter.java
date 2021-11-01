package com.vadim.termometr.ui.main.screens;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.vadim.termometr.R;
import com.vadim.termometr.utils.temperprocessor.TemperatureFromPath;
import com.vadim.termometr.utils.Convertor;

import java.util.Random;

public class TemperPresenter {

    private final TemperatureView view;
    private final TemperatureFromPath temperature = new TemperatureFromPath();
    private final Handler handler = new Handler();
    private Runnable runnable;
    boolean isRunning = false;
    float t = 0;

    public TemperPresenter(TemperatureView view) {
        this.view= view;
    }

    public void getTemperature() {
        Log.i("testTemer", view.loadPathTemperature()+"");
        if (view.loadPathTemperature().equals("")) {
            checkPath();
        } else {
            runRunnable();
        }
    }

    public void stopRunnable() {
        if (runnable != null) {
            isRunning = false;
            handler.removeCallbacks(runnable);
        }
    }

    private void runRunnable() {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    isRunning=true;
                    t = Convertor.temperatureHuman(temperature.cat(view.loadPathTemperature()));
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
            runRunnable();
        } else {
            view.showError(R.string.warning);
        }
    }

}
