package com.vadim.termometr.screens.main;

import com.vadim.termometr.R;
import com.vadim.termometr.utils.temperprocessor.TemperatureProcessor;

public class TemperPresentor {

    private TemperatureView view;
    private TemperatureProcessor temperatureProcessor = new TemperatureProcessor();

    public TemperPresentor(TemperatureView view) {
        this.view=view;
    }

    public void setTemperature(){
        float t = temperatureProcessor.getTemperatureCPU();
        if(t==-100){
            view.showError(R.string.warning);
        }
        view.getTemperatureGPU(t);

    }


}
