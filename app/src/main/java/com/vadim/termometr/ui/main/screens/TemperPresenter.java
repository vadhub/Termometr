package com.vadim.termometr.ui.main.screens;

import android.util.Log;

import com.vadim.termometr.R;
import com.vadim.termometr.utils.temperprocessor.TemperatureFromPath;
import com.vadim.termometr.utils.Convertor;

public class TemperPresenter {

    private TemperatureView view;
    private TemperatureFromPath temperature = new TemperatureFromPath();

    public TemperPresenter(TemperatureView view) {
        this.view= view;
    }

    public void getTemperature() {
        Log.i("testTemer", view.loadPathTemperature()+"");
        if (view.loadPathTemperature().equals("")) {
            checkPath();
        } else {
            float t = Float.parseFloat(temperature.catTest(view.loadPathTemperature()));//Convertor.temperatureHuman());
            view.showTemperatureGPU(t);
        }
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
