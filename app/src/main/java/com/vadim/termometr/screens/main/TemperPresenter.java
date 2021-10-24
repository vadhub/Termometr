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
        if (view.loadPathTemperature().equals("")) {
            checkPath();
        } else {
            float t = Convertor.temperatureHuman(temperature.cat(view.loadPathTemperature()));
            if (t==-100) {
                view.showError(R.string.warning);
            }
            view.showTemperatureGPU(t);
        }
    }

    private void checkPath() {
        if (!temperature.getTemperaturePath().equals("")) {
            view.savePathTemperature(temperature.getTemperaturePath());
        }
    }

}