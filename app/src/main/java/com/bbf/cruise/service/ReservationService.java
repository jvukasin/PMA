package com.bbf.cruise.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.SyncStateContract;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.bbf.cruise.MainActivity;
import com.bbf.cruise.R;
import com.bbf.cruise.activities.CarDetailActivity;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.bbf.cruise.CruiseApplication.CHANNEL_ID;

public class ReservationService extends Service {

    private long timeRemainingMilis = 10000; //30min = 1800000ms
    private long minutes = 00; //29
    private long seconds = 30; //60
    private CountDownTimer countDownTimer;
    private Timer timer;
    private String plates;

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        plates = intent.getStringExtra("plates");
        if(intent.getAction().equals("REMOVE_FOREGROUND")){
            FirebaseDatabase.getInstance().getReference().child("cars").child(plates).child("occupied").setValue(false);
            FirebaseDatabase.getInstance().getReference().child("Reservations").child(plates).removeValue();
            stopForeground(true);
            stopSelf();
        }else{
            startTimer();
        }
        return START_NOT_STICKY;
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
                        FirebaseDatabase.getInstance().getReference().child("cars").child(plates).child("occupied").setValue(false);
                        FirebaseDatabase.getInstance().getReference().child("Reservations").child(plates).removeValue();
                        stopForeground(true);
                        stopSelf();
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
                notificationUpdate(timeLeftFormated);
            }
        }, 1000,1000);
    }

    private void notificationUpdate(String timeLeft){
        Intent notificationIntent = new Intent(this, CarDetailActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        final Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Reservation " + plates)
                .setContentText("Time Remaing : " + timeLeft)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_logo)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .build();
        startForeground(1, notification);
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
