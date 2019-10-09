package com.badboy.gpstracker;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

public class MyApp extends Application {
    public static final String MAP_SCANNING_SERVICE = "com.badboy.gpstracker.TRACKING_SERVICE";

    public static NotificationChannel getMapScanningChannel() {
        return mapScanningChannel;
    }

    private static NotificationChannel mapScanningChannel=null;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mapScanningChannel = new NotificationChannel(MAP_SCANNING_SERVICE,
                    "Map Scanning Service", NotificationManager.IMPORTANCE_DEFAULT);

        // Sets whether notifications posted to this channel should display notification lights
        mapScanningChannel.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        mapScanningChannel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        mapScanningChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        mapScanningChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        }
    }
}
