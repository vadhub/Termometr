package com.vadim.termometr.screens.main;

public interface TemperatureView {
    void showTemperatureGPU(float t);
    void showError(int str);
    void savePathTemperature(String path);
}
