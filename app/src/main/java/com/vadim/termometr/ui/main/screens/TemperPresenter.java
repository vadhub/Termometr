package com.vadim.termometr.ui.main.screens;

import android.os.Handler;
import com.vadim.termometr.R;
import com.vadim.termometr.utils.temperprocessor.TemperatureFromPath;
import com.vadim.termometr.utils.Convertor;

public class TemperPresenter {

    private final TemperatureView view;
    private final TemperatureFromPath temperature = new TemperatureFromPath();
    private final Handler handler = new Handler();
    boolean isRunning = false;
    float t = 0;

    public TemperPresenter(TemperatureView view) {
        this.view= view;
    }

    public void getTemperature() {
        if (view.loadPathTemperature().equals("")) {
            checkPath();
        } else {
            runRunnable();
        }
    }

    private void runRunnable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    isRunning = true;
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
