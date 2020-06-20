package com.bbf.cruise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import com.bbf.cruise.R;

public class RideActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private Button finishBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);
        setTitle(R.string.activeRide);

        finishBtn = findViewById(R.id.finishRideBtn);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.stop();
            }
        });


        chronometer = findViewById(R.id.rideTime);
        chronometer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(chronometer.isActivated()) {
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
        }
    }
}
