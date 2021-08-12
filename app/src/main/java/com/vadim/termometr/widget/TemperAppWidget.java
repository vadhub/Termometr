package com.vadim.termometr.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.widget.RemoteViews;

import com.vadim.termometr.screens.main.MainActivity;
import com.vadim.termometr.R;
import com.vadim.termometr.utils.Convertor;
import com.vadim.termometr.utils.TemperatureProcessor;
import com.vadim.termometr.viewable.ViewableResult;


public class TemperAppWidget extends AppWidgetProvider implements SensorEventListener, ViewableResult {

    private SensorManager mySensorManager;
    private Sensor mySensorTemper;
    private Handler handler;
    private float temper;
    private boolean isLife;
    private TemperatureProcessor temperatureProcessor;
    private RemoteViews views;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        isLife = true;
        mySensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        handler = new Handler();
        temperatureProcessor = new TemperatureProcessor();

        if(mySensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)!=null){
            mySensorTemper = mySensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }else{
            updateResult();
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        views = new RemoteViews(context.getPackageName(), R.layout.temper_app_widget);
        views.setOnClickPendingIntent(R.id.layout, pendingIntent);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        temper = sensorEvent.values[0];
        outTemper(temper, true);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void outTemper(float temperat, boolean typeTemper) {
        views.setTextViewText(R.id.appwidget_text,getTemperatureChanged(temper, true));
    }

    @Override
    public void createChannel() {

    }

    @Override
    public String getTemperatureChanged(float temperature, boolean isCelsia) {
        return String.format("%.0f", temperature) + "C°"+" "+String.format("%.0f", Convertor.fahrenheit(temperature)) + "F°";
    }

    @Override
    public void updateResult() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                temper = temperatureProcessor.getTemperatureCPU();

                outTemper(temper, true);

                handler.postDelayed(this, 1000);
                if(!isLife){
                    handler.removeCallbacks(this);
                }
            }
        });
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        isLife = false;
    }
}