package com.bbf.cruise.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Chronometer;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class RentService extends Service {

    private Timer timer;
    private int count;
    private double price;
    private static double START_PRICE = 2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        timer = new Timer();
        count = 0;
        price = START_PRICE;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                count++;
                if(count%60 == 0) {
                    price +=0.4;
                    DecimalFormat df = new DecimalFormat("#.#");
                    double rounded = Double.valueOf(df.format(price));
                    Intent intentLocal = new Intent();
                    intentLocal.setAction("Price");
                    intentLocal.putExtra("price", rounded);
                    sendBroadcast(intentLocal);
                }
            }
        }, 0,1000);


        return START_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {
        timer.cancel();
        return super.stopService(name);
    }
}
