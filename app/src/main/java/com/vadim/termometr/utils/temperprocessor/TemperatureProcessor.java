package com.vadim.termometr.utils.temperprocessor;

public class TemperatureProcessor {

    public float getTemperatureCPU() {
        TemperatureFromPath temperatureFromPath = TemperatureFromPath.getInstance();
        String line = temperatureFromPath.getFileTemper();
        try {
            if (line!=null) {
                float temp = Float.parseFloat(line);
                return temp / 1000.0f;
            } else {
                return -100;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

}
