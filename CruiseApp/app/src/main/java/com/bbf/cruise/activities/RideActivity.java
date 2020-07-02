package com.bbf.cruise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bbf.cruise.MainActivity;
import com.bbf.cruise.R;
import com.bbf.cruise.constants.FirebasePaths;
import com.bbf.cruise.dialogs.LoadingDialog;
import com.bbf.cruise.fragments.RideMapFragment;
import com.bbf.cruise.service.RentService;
import com.bbf.cruise.tools.FragmentTransition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.RideHistory;

public class RideActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private Button finishBtn;
    private Dialog mDialog;
    private int counter;
    private double price;
    private TextView priceTV;
    private FirebaseAuth auth;
    private String plates;
    private DatabaseReference carReference;
    private boolean paid;
    private DatabaseReference userReference;
    private FirebaseUser firebaseUser;
    private BroadcastReceiver broadcastReceiver;

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
        paid = false;
        finishBtn = findViewById(R.id.finishRideBtn);
        priceTV = findViewById(R.id.ridePrice);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishRide();
            }
        });

        chronometer = findViewById(R.id.rideTime);
//        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//            @Override
//            public void onChronometerTick(Chronometer chronometer) {
//                if(counter == 60) {
//                    counter = 0;
//                    price += 0.4;
//                    double rounded = Math.round(price * 10.0) / 10.0;
//                    priceTV.setText(String.valueOf(rounded));
//                }
//                counter++;
//            }
//        });
        chronometer.start();
        Intent intentService = new Intent(this, RentService.class);
        startService(intentService);

        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.rate_finish_dialog);
        mDialog.setCancelable(false);
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
                intent.putExtra("rideHistory", new RideHistory(startDate, endDate, Double.parseDouble(distance.getText().toString()), Double.parseDouble(fee.getText().toString()),
                        Integer.parseInt(bonusPoints.getText().toString()), auth.getCurrentUser().getUid()));
                sendBroadcast(intent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callLoadingDialogAndFinish();
                    }
                }, 500);

            }
        });

        payWithPointsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String endDate = formatter.format(new Date());
                updateCarRating(rate.getRating());
                Intent intent = new Intent();
                intent.putExtra("flagBonusPoints", true);
                intent.putExtra("rideHistory", new RideHistory(startDate, endDate, Double.parseDouble(distance.getText().toString()), Double.parseDouble(feeBP.getText().toString()),
                        Integer.parseInt(bonusPoints.getText().toString()), auth.getCurrentUser().getUid()));
                intent.setAction("SAVE_RIDE_HISTORY_ACTION");
                sendBroadcast(intent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callLoadingDialogAndFinish();
                    }
                }, 500);
            }
        });

        RideMapFragment fragment = RideMapFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("plates", plates);
        bundle.putDouble("lat", getIntent().getDoubleExtra("lat", 0));
        bundle.putDouble("lng", getIntent().getDoubleExtra("lng", 0));
        fragment.setArguments(bundle);
        FragmentTransition.addRideMap(fragment, this, false);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Price");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Double currPrice = intent.getDoubleExtra("price", 0.0);
                price = currPrice;
                priceTV.setText(String.valueOf(currPrice));
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void callLoadingDialogAndFinish() {
        paid = true;
        mDialog.dismiss();
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("reservation", "none");
        editor.apply();
        final LoadingDialog loadingDialog = new LoadingDialog(RideActivity.this);
        loadingDialog.startLoadingDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismissDialog();
                Intent intent = new Intent(RideActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    private void updateCarRating(final float rating) {
        if(rating != 0.0) {
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
    }

    private void finishRide() {
        Intent intent = new Intent();
        intent.setAction("RIDE_FINISHED_ACTION");
        sendBroadcast(intent);
        chronometer.stop();

        TextView showPrice = mDialog.findViewById(R.id.fee);
        showPrice.setText(priceTV.getText());

        TextView distance = findViewById(R.id.rideDistance);

        int bonusPoints = calculateBonusPoints(Double.valueOf(distance.getText().toString()));
        TextView bounsPoints = mDialog.findViewById(R.id.earnedPoints);
        bounsPoints.setText(String.valueOf(bonusPoints));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FirebasePaths.USERS_PATH);
        databaseReference.child(auth.getCurrentUser().getUid()).child("bonusPoints").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer points = dataSnapshot.getValue(Integer.class);
                TextView bounsPoints = mDialog.findViewById(R.id.earnedPoints);
                TextView totalPoints = mDialog.findViewById(R.id.totalPoints);
                int val = points + Integer.parseInt(bounsPoints.getText().toString());
                totalPoints.setText(String.valueOf(val));
                TextView feeBP = mDialog.findViewById(R.id.feeBP);

                double rounded = Math.round(calculateFeeBP(val, Double.valueOf(priceTV.getText().toString())) * 10.0) / 10.0;
                feeBP.setText(Double.toString(rounded));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Intent intentService = new Intent(this, RentService.class);
        stopService(intentService);
        mDialog.show();
    }

    private double calculateFeeBP(int bonusPoints, Double fee) {
        double bp = Math.round(((fee/1000) * bonusPoints) * 10.0) / 10.0;
        return (fee - bp);
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
        if(!paid) {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (RentService.class.getName().equals(service.service.getClassName())) {
                    Intent intentService = new Intent(this, RentService.class);
                    stopService(intentService);
                }
            }
            userReference = FirebaseDatabase.getInstance().getReference("Users");
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userReference.child(firebaseUser.getUid()).child("wallet").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Double value = dataSnapshot.getValue(Double.class);
                    userReference.child(firebaseUser.getUid()).child("wallet").setValue(value - price);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            FirebaseDatabase.getInstance().getReference().child("cars").child(plates).child("occupied").setValue(false);
        }
        unregisterReceiver(broadcastReceiver);
    }
}
