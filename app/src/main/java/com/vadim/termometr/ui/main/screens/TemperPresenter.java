package com.vadim.termometr.ui.main.screens;

import android.os.Handler;
import android.util.Log;

import com.vadim.termometr.R;
import com.vadim.termometr.utils.temperprocessor.TemperatureFromPath;
import com.vadim.termometr.utils.Convertor;

public class TemperPresenter {

    private TemperatureView view;
    private TemperatureFromPath temperature = new TemperatureFromPath();
    private Handler handler = new Handler();

    public TemperPresenter(TemperatureView view) {
        this.view= view;
    }

    public void getTemperature() {
        Log.i("testTemer", view.loadPathTemperature()+"");
        if (view.loadPathTemperature().equals("")) {
            checkPath();
        } else {
            float t = Float.parseFloat(temperature.catTest(view.loadPathTemperature()));//Convertor.temperatureHuman());
            runRunnable(t);
        }
    }

    public void stopRunnable(Runnable runnable) {
        handler.removeCallbacks(runnable);
    }

    private void runRunnable(float t) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                view.showTemperatureGPU(t);
                handler.postDelayed(this, 5000);
            }
        };
        runnable.run();
    }

    private void checkPath() {
        if (!temperature.getTemperaturePath().equals("")) {
            //view.savePathTemperature(temperature.getTemperaturePath());
            view.savePathTemperature("10");
        } else {
            view.showError(R.string.warning);
        }
    }

}
