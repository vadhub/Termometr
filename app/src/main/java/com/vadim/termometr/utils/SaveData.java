package com.vadim.termometr.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveData {
    private SharedPreferences sPref;
    private Context context;
    public SaveData(Context context) {
        this.context=context;
    }

    //save check state
    public void saveState(boolean isCheck) {
        sPref = context.getSharedPreferences("save_state", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("isChecked", isCheck);
        ed.apply();
    }

    public boolean loadState() {
        sPref = context.getSharedPreferences("save_state", MODE_PRIVATE);
        return sPref.getBoolean("isChecked", true);
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

    public void savePathTemperature(String path) {
        sPref = context.getSharedPreferences("temper", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("path_temper", path);
        ed.apply();
    }

    public boolean loadPathTemperature() {
        sPref = context.getSharedPreferences("save_change_type", MODE_PRIVATE);
        return sPref.getBoolean("path_temper", true);
    }

}
