package com.vadim.termometr.utils.temperprocessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class TemperatureFromPath {
    private String fileTemper = "";

    public String getFileTemper() {
        return parsingListPaths(getTemperaturePaths());
    }

    public void setFileTemper(String fileTemper) {
        this.fileTemper = fileTemper;
    }

    private String cat(String file) {
        BufferedReader reader = null;
        StringBuilder result = new StringBuilder();
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(file));

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result.toString();
    }

    private String parsingListPaths(Map<String, String> listPaths) {
        String tmp = null;
        for (String key: listPaths.keySet()) {
            tmp = cat(listPaths.get(key));
            if (!tmp.equals("")) {
                return listPaths.get(key);
            }
        }

        return tmp;
    }

    private Map<String, String> getTemperaturePaths() {
        Map<String, String> listPaths = new HashMap<>();

        for (Field field: TemperaturePaths.class.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                try {
                    listPaths.put(field.getName(), String.valueOf(field.get(field.getType())));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return listPaths;
    }
}
