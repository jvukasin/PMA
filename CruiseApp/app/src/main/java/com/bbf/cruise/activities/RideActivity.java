package com.bbf.cruise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import com.bbf.cruise.R;

public class RideActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private Button finishBtn;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);
        setTitle(R.string.activeRide);

        finishBtn = findViewById(R.id.finishRideBtn);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishRide();
            }
        });

        chronometer = findViewById(R.id.rideTime);
        chronometer.start();

        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.rate_finish_dialog);
        mDialog.setCancelable(false);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void finishRide() {
        chronometer.stop();
        mDialog.show();
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.exitRideOnBackTitle);
        builder.setMessage(R.string.exitRideOnBackMessage);
        builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO exit and charge

                finish();
            }
        });
        builder.setNegativeButton("Wait", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
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
