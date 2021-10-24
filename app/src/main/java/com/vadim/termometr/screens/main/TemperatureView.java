package com.vadim.termometr.screens.main;

import com.vadim.termometr.servicetemper.TemperatureViewService;

public interface TemperatureView extends TemperatureViewService {
    void savePathTemperature(String path);
    String loadPathTemperature();
}
