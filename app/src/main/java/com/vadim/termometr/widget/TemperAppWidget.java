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
import android.widget.TextView;

import com.vadim.termometr.MainActivity;
import com.vadim.termometr.R;


public class TemperAppWidget extends AppWidgetProvider implements SensorEventListener {

    private SensorManager mySensorManager;
    private Sensor mySensorTemper;
    private Handler handler;

    private float temper;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        mySensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        handler = new Handler();

        if(mySensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)!=null){
            mySensorTemper = mySensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }else{

        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.temper_app_widget);
        views.setOnClickPendingIntent(R.id.layout, pendingIntent);
        views.setTextViewText(R.id.appwidget_text, "f");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        temper = sensorEvent.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}