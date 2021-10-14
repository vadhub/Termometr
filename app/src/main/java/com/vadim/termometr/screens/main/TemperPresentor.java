package com.vadim.termometr.screens.main;

import com.vadim.termometr.R;
import com.vadim.termometr.temperprocessor.TemperatureFromPath;
import com.vadim.termometr.utils.Convertor;

public class TemperPresentor {

    private TemperatureView view;
    private TemperatureFromPath temperature = new TemperatureFromPath();

    public TemperPresentor(TemperatureView view) {
        this.view=view;
    }

    public void getTemperature() {
        float t = Convertor.temperatureHuman(temperature.getTemperature());
        if (t==-100) {
            view.showError(R.string.warning);
        }
        view.getTemperatureGPU(t);
    }

}
