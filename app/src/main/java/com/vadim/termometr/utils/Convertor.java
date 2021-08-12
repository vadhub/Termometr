package com.vadim.termometr.utils;

import com.vadim.termometr.R;

public class Convertor {
    public static float fahrenheit(float temperCelsia){
        float far = (temperCelsia*9/5)+32;
        return far;
    }

    public String temperatureConvertor(float temperature, boolean isCelsius){
        float farTemper;
        String temper = String.format("%.0f", temperature)+"C°";

        if(!isCelsius){
            farTemper = Convertor.fahrenheit(temperature);
            temper =String.format("%.0f", farTemper)+"F°";
        }

        return temper;
    }
}
