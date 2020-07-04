package com.bbf.cruise.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;
import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bbf.cruise.fragments.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import model.LocationObject;

public class RentService extends Service {

    private Timer timer;
    private int count;
    private double price;
    private static double START_PRICE = 2;
    private ValueEventListener locLis;
    DatabaseReference rentReference;
    private ArrayList<LatLng> route = new ArrayList<>();
    private String plates;
    private double sum = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        timer = new Timer();
        plates = intent.getStringExtra("plates");
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

        rentReference = FirebaseDatabase.getInstance().getReference("Rent").child(plates).child("location");
        locLis = rentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LocationObject location = dataSnapshot.getValue(LocationObject.class);
                route.add(new LatLng(location.getLatitude(), location.getLongitude()));
                Intent intentLocal = new Intent();
                intentLocal.setAction("addCarMarker");
                intentLocal.putExtra("lat", location.getLatitude());
                intentLocal.putExtra("lon", location.getLongitude());
                sendBroadcast(intentLocal);
                updateDistance();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return START_STICKY;
    }

    private void updateDistance(){
        if(route.size() >= 2){
            LatLng pos_prev = route.get(route.size()-2);
            LatLng pos_curr = route.get(route.size()-1);
            double distance = MapFragment.calculateDistance(pos_prev.latitude, pos_prev.longitude, pos_curr.latitude, pos_curr.longitude);
            sum += distance;

            Intent il = new Intent();
            il.setAction("updateDistance");
            il.putExtra("sum", sum);
            sendBroadcast(il);
        }
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        Intent i = new Intent();
        i.setAction("RIDE_FINISHED_ACTION");
        i.putParcelableArrayListExtra("route", route);
        sendBroadcast(i);
        rentReference.removeEventListener(locLis);
        super.onDestroy();
    }
}
