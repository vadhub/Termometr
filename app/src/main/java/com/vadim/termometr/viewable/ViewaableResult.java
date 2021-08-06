package com.vadim.termometr.viewable;

public interface ViewaableResult {
    void outTemper(float temperat, boolean typeTemper);
    void createChannel();
    String getTemperatureChanged(float temperature, boolean isCelsia);
}
