package com.vadim.termometr.screens.main;

import com.vadim.termometr.R;
import com.vadim.termometr.utils.temperprocessor.TemperatureFromPath;
import com.vadim.termometr.utils.Convertor;

public class TemperPresenter {

    private TemperatureView view;
    private TemperatureFromPath temperature = new TemperatureFromPath();

    public TemperPresenter(TemperatureView view) {
        this.view=view;
    }

    public void getTemperature() {
        float t = Convertor.temperatureHuman(temperature.getTemperaturePath());
        if (t==-100) {
            view.showError(R.string.warning);
        }
        view.showTemperatureGPU(t);
    }

    private void chekPaths() {
        if (!temperature.getTemperaturePath().equals("")) {
            view.savePathTemperature(temperature.getTemperaturePath());
        }
    }

}
