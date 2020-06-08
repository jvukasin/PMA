package com.bbf.cruise.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ReservationService extends Service {

    private long timeRemainingMilis = 10000; //30min = 1800000ms
    private long minutes = 00; //29
    private long seconds = 30; //60
    private CountDownTimer countDownTimer;
    private Timer timer;

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    public void startTimer() {
        timer = new Timer();
        minutes = 00; //29
        seconds = 30;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                seconds--;
                if(seconds < 0) {
                    if(minutes == 0) {
                        seconds = 0;
                        timer.cancel();
                    } else {
                        seconds = 59;
                        minutes--;
                    }
                }
                Intent intentLocal = new Intent();
                intentLocal.setAction("Counter");
                String timeLeftFormated = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                intentLocal.putExtra("timer", timeLeftFormated);
                sendBroadcast(intentLocal);
            }
        }, 1000,1000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
    }
}
