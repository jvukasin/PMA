package com.bbf.cruise;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class CruiseApplication extends Application {

    private static Context context;
    public static final String CHANNEL_ID_LOW = "RES_SERVICE_CH_LOW";
    public static final String CHANNEL_ID_HIGH = "RES_SERVICE_CH_HIGH";

    public void onCreate() {
        super.onCreate();
        CruiseApplication.context = getApplicationContext();
        createNotificationChannels();
    }

    public static Context getAppContext() {
        return CruiseApplication.context;
    }

    private void createNotificationChannels() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channelLow = new NotificationChannel(CHANNEL_ID_LOW, "Reservation service channel", NotificationManager.IMPORTANCE_LOW);
            channelLow.setDescription("Notifications for reservation service.");

            NotificationChannel channelHigh = new NotificationChannel(CHANNEL_ID_HIGH, "Reservation service channel", NotificationManager.IMPORTANCE_HIGH);
            channelHigh.setDescription("Notifications for reservation service.");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channelLow);
            manager.createNotificationChannel(channelHigh);
        }

    }

}
