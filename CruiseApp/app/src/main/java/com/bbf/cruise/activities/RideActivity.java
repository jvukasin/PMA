package com.bbf.cruise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bbf.cruise.R;
import com.bbf.cruise.constants.FirebasePaths;
import com.bbf.cruise.fragments.RideMapFragment;
import com.bbf.cruise.tools.FragmentTransition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import model.RideHistory;

public class RideActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private Button finishBtn;
    private Dialog mDialog;
    private int counter;
    private double price;
    private TextView priceTV, distanceTV;
    private FirebaseAuth auth;
    private String plates;
    private DatabaseReference carReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);
        setTitle(R.string.activeRide);

        carReference = FirebaseDatabase.getInstance().getReference("cars");

        auth = FirebaseAuth.getInstance();
        plates = getIntent().getStringExtra("plates");
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
        mDialog.setCancelable(true); // TODO: promeniti na false
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button payBtn = mDialog.findViewById(R.id.payBPBtn);
        Button payWithPointsBtn = mDialog.findViewById(R.id.payBtn);

        final TextView fee = mDialog.findViewById(R.id.fee);
        final TextView feeBP = mDialog.findViewById(R.id.feeBP);
        final RatingBar rate = mDialog.findViewById(R.id.rideRating);
        final TextView bonusPoints = mDialog.findViewById(R.id.earnedPoints);
        final TextView distance = findViewById(R.id.rideDistance);

        final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        final String startDate = formatter.format(new Date());
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String endDate = formatter.format(new Date());
                updateCarRating(rate.getRating());
                Intent intent = new Intent();
                intent.putExtra("flagBonusPoints", false);
                intent.setAction("SAVE_RIDE_HISTORY_ACTION");
                intent.putExtra("rideHistory", new RideHistory(startDate, endDate, Double.valueOf(distance.getText().toString()), Double.valueOf(fee.getText().toString()),
                        Integer.valueOf(bonusPoints.getText().toString()), auth.getCurrentUser().getUid()));
                sendBroadcast(intent);
            }
        });

        payWithPointsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: uraditi placanje sa bonus poenima
                String endDate = formatter.format(new Date());
                updateCarRating(rate.getRating());
                Intent intent = new Intent();
                intent.putExtra("flagBonusPoints", true);
                intent.putExtra("rideHistory", new RideHistory(startDate, endDate, Double.valueOf(distance.getText().toString()), Double.valueOf(feeBP.getText().toString()),
                        Integer.valueOf(bonusPoints.getText().toString()), auth.getCurrentUser().getUid()));
                intent.setAction("SAVE_RIDE_HISTORY_ACTION");
                sendBroadcast(intent);
            }
        });

        RideMapFragment fragment = RideMapFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putDouble("lat", getIntent().getDoubleExtra("lat", 0));
        bundle.putDouble("lng", getIntent().getDoubleExtra("lng", 0));
        fragment.setArguments(bundle);
        FragmentTransition.addRideMap(fragment, this, false);
    }

    private void updateCarRating(final float rating) {
        carReference.child(plates).child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Double rate = dataSnapshot.getValue(Double.class);
               if(rate == 0){
                   carReference.child(plates).child("rating").setValue(rating);
               }else{
                   double newRating = Math.round(((rate + rating) / 2) * 10.0) / 10.0;
                   carReference.child(plates).child("rating").setValue(newRating);
               }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void finishRide() {
        Intent intent = new Intent();
        intent.setAction("RIDE_FINISHED_ACTION");
        sendBroadcast(intent);
        chronometer.stop();
        //TODO proslediti parametre u dijalog
        TextView showPrice = mDialog.findViewById(R.id.fee);
        showPrice.setText(priceTV.getText());

        TextView distance = findViewById(R.id.rideDistance);

        int bonusPoints = calculateBonusPoints(Double.valueOf(distance.getText().toString()));
        TextView bounsPoints = mDialog.findViewById(R.id.earnedPoints);
        bounsPoints.setText(String.valueOf(bonusPoints));

        TextView feeBP = mDialog.findViewById(R.id.feeBP);
        feeBP.setText(String.valueOf(calculateFeeBP(bonusPoints, Double.valueOf(showPrice.getText().toString()))));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FirebasePaths.USERS_PATH);
        databaseReference.child(auth.getCurrentUser().getUid()).child("bonusPoints").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer points = dataSnapshot.getValue(Integer.class);
                TextView bounsPoints = mDialog.findViewById(R.id.earnedPoints);
                TextView totalPoints = mDialog.findViewById(R.id.totalPoints);
                int val = points + Integer.parseInt(bounsPoints.getText().toString());
                totalPoints.setText(String.valueOf(val));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //TODO napraviti da ako ubije aplikaciju ili izadje ili sta god, da mu se skine sa racuna svakako cena
        mDialog.show();
    }

    private int calculateFeeBP(int bonusPoints, Double fee) {
        int bp = (int) ((fee/1000) * bonusPoints);
        return (int) (fee - bp);
    }

    private int calculateBonusPoints(Double distance) {
        return (int) (distance * 35);
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
