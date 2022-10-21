package com.vadim.termometr.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveData {
    private SharedPreferences sPref;
    private final Context context;
    public SaveData(Context context) {
        this.context=context;
    }

    //save changed farengete or celestial from menu
    public void saveChangedTypeTemperature(boolean isCheckTypeTemperature) {
        sPref = context.getSharedPreferences("save_change_type", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("isCheckTypeTemperature", isCheckTypeTemperature);
        ed.apply();
    }

    public boolean loadChangedTypeTemperature() {
        sPref = context.getSharedPreferences("save_change_type", MODE_PRIVATE);
        return sPref.getBoolean("isCheckTypeTemperature", true);
    }
}
