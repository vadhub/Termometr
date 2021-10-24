package com.vadim.termometr.ui.main.screens;

public interface TemperatureView {
    void showTemperatureGPU(float t);
    void showError(int resource);
    void savePathTemperature(String path);
    String loadPathTemperature();
}
