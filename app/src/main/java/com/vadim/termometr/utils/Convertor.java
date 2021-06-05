package com.vadim.termometr.utils;

public class Convertor {
    public static float fahrenheit(float temperCelsia){
        float far = (temperCelsia*9/5)+32;
        return far;
    }
}
