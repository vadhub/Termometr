package com.vadim.termometr.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TemperatureProcessor {

    public float getTemperatureCPU(){
        Process process;

        try {
            process = Runtime.getRuntime().exec("cat sys/devices/virtual/thermal/thermal_zone0/temp");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if(line!=null) {
                float temp = Float.parseFloat(line);
                return temp / 1000.0f;
            }else{
                return 30.0f;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    public String getTemperatureChanged(float temperature, boolean isCelsia){
        String temper = String.format("%.0f", temperature) + "C°";

        if(!isCelsia){
            float fareng = Convertor.fahrenheit(temperature);
            temper = String.format("%.0f", fareng) + "F°";
        }
        return temper;
    }
}
