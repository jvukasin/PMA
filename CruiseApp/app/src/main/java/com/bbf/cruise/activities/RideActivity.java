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
import android.widget.TextView;

import com.bbf.cruise.R;

public class RideActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private Button finishBtn;
    private Dialog mDialog;
    private int counter;
    private double price;
    private TextView priceTV, distanceTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);
        setTitle(R.string.activeRide);

        price = 2.0;
        counter = 0;
        finishBtn = findViewById(R.id.finishRideBtn);
        priceTV = findViewById(R.id.ridePrice);
        distanceTV = findViewById(R.id.rideDistance);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishRide();
            }
        });

        chronometer = findViewById(R.id.rideTime);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                System.out.println(counter);
                if(counter == 60) {
                    counter = 0;
                    System.out.println("usao");
                    price += 0.4;
                    double rounded = Math.round(price * 10) / 10.0;
                    priceTV.setText(String.valueOf(rounded));
                }
                counter++;
            }
        });
        chronometer.start();

        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.rate_finish_dialog);
        mDialog.setCancelable(false);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void finishRide() {
        chronometer.stop();
        //TODO proslediti parametre u dijalog
        TextView showPrice = mDialog.findViewById(R.id.fee);
        showPrice.setText(priceTV.getText());
        //TODO napraviti da ako ubije aplikaciju ili izadje ili sta god, da mu se skine sa racuna svakako cena
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
