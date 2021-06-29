package com.vadim.termometr.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.widget.RemoteViews;

import com.vadim.termometr.R;
import com.vadim.termometr.ServiceBackgrounTemperature;

public class ServiceWidget extends ServiceBackgrounTemperature {

    private RemoteViews view;

    private void updateWidget(float steps){
        view.setTextViewText(R.id.appwidget_text, String.valueOf(steps));
        ComponentName widget = new ComponentName(this, TemperAppWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(widget, view);
    }

    @Override
    public void outTemper(float temperat, boolean typeTemper) {
        super.outTemper(temperat, typeTemper);
        updateWidget(temperat);
    }
}
