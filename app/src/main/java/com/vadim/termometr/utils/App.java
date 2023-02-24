package com.vadim.termometr.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.yandex.mobile.ads.common.MobileAds;

public class App extends Application {

    public static final String CHANNEL_ID = "Temperature";
    public static NotificationManager notificationManager;
    private static final String YANDEX_MOBILE_ADS_TAG = "YandexMobileAds";

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, () -> Log.d(YANDEX_MOBILE_ADS_TAG, "SDK initialized"));
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createChannel();
    }

    public void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "channel temperature",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
