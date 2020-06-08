package com.bbf.cruise;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class CruiseApplication extends Application {

    private static Context context;
    public static final String CHANNEL_ID = "RES_SERVICE_CH";

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

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Reservation service channel", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Notifications for reservation service.");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }

}
