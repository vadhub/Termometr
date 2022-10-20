package com.vadim.termometr.utils;

import android.annotation.SuppressLint;

public class Convertor {
    public static float fahrenheit(float temperCelsia) {
        return (temperCelsia*9/5)+32;
    }

    @SuppressLint("DefaultLocale")
    public static String temperatureConvertor(float temperature, boolean isCelsius) {
        float farTemper;
        @SuppressLint("DefaultLocale") String temper = String.format("%.0f", temperature)+"C°";

        if (!isCelsius) {
            farTemper = Convertor.fahrenheit(temperature);
            temper =String.format("%.0f", farTemper)+"F°";
        }

        return temper;
    }

    public static float temperatureHuman(String line) {
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
