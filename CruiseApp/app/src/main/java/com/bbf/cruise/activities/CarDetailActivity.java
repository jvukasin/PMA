package com.bbf.cruise.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bbf.cruise.MainActivity;
import com.bbf.cruise.R;
import com.bbf.cruise.service.ReservationService;
import com.bbf.cruise.tools.NetworkUtil;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import model.Car;

public class CarDetailActivity extends AppCompatActivity {

    private Button rentBtn, reserveBtn, favButton, cancelBtn;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private String plateNo;
    private TextView dis_from_me, from_you, counter;
    private BroadcastReceiver broadcastReceiver;
    private ReservationService reservationService;
    private ImageSlider imageSlider;
    private List<SlideModel> slideModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        setTitle(R.string.carInfo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reserveBtn = (Button) findViewById(R.id.reserveBtn);
        rentBtn = (Button) findViewById(R.id.rentBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        plateNo = getIntent().getStringExtra("plate");
        imageSlider = findViewById(R.id.slider);
        slideModels = new ArrayList<>();
        initTextFields();

        rentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initQRScanner(v);
            }
        });
        reserveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveCar();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelReservation();
            }
        });

        counter = (TextView) findViewById(R.id.counter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        initImages();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Counter");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String timer = intent.getStringExtra("timer");
                counter.setText(timer);
                if(counter.getText().equals("00:00")) {
                    cancelBtn.setVisibility(View.INVISIBLE);
                    reserveBtn.setVisibility(View.VISIBLE);
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
        if(reservationService == null) reservationService = new ReservationService();
    }



    private void initImages() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("cars").child(plateNo).child("images");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    slideModels.add(new SlideModel(snap.getValue(String.class)));
                }
                if(slideModels.isEmpty()) {
                    slideModels.add(new SlideModel(R.drawable.image_not_available));
                }
                imageSlider.setImageList(slideModels, true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void cancelReservation() {
        if(NetworkUtil.isConnected(this)) {
            counter.setVisibility(View.INVISIBLE);
            cancelBtn.setVisibility(View.INVISIBLE);
            reserveBtn.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, reservationService.getClass());
            intent.putExtra("plates", plateNo);
            intent.setAction("REMOVE_FOREGROUND");
            startService(intent);
        }
    }

    private void reserveCar() {
        if(NetworkUtil.isConnected(this)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, PackageManager.PERMISSION_GRANTED);

            dis_from_me.setVisibility(View.INVISIBLE);
            from_you.setVisibility(View.INVISIBLE);
            reserveBtn.setVisibility(View.INVISIBLE);
            counter.setText("30:00");
            counter.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);

            FirebaseDatabase.getInstance().getReference().child("cars").child(plateNo).child("occupied").setValue(true);
            reference = FirebaseDatabase.getInstance().getReference().child("Reservations").child(plateNo);
            reference.child("user").setValue(firebaseUser.getUid());
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MINUTE, 30);
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            reference.child("time").setValue(df.format(now.getTime()));


            Intent i = new Intent(this, reservationService.getClass());
            i.putExtra("plates", plateNo);
            i.setAction("START_SERVICE");
            ContextCompat.startForegroundService(this, i);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initTextFields() {
        TextView carName = (TextView) findViewById(R.id.carName);
        carName.setText(getIntent().getStringExtra("name"));

        TextView carPlate = (TextView) findViewById(R.id.carPlate);
        carPlate.setText(plateNo);

        TextView carRating = (TextView) findViewById(R.id.carRating);
        carRating.setText(String.valueOf(getIntent().getDoubleExtra("rating", 0.0)));

        TextView carFuel = (TextView) findViewById(R.id.carFuel);
        carFuel.setText(getIntent().getStringExtra("fuel_distance") + " km");

        TextView carMileage = (TextView) findViewById(R.id.carDistance);
        carMileage.setText(getIntent().getStringExtra("mileage") + " km");

        TextView carRides = (TextView) findViewById(R.id.carRides);
        carRides.setText(String.valueOf(getIntent().getIntExtra("no_of_rides", 0)));

        TextView carTp_fl = (TextView) findViewById(R.id.carTp_fl);
        carTp_fl.setText(String.valueOf(getIntent().getFloatExtra("tp_fl", 0.0f)));

        TextView carTp_fr = (TextView) findViewById(R.id.carTp_fr);
        carTp_fr.setText(String.valueOf(getIntent().getFloatExtra("tp_fr", 0.0f)));

        TextView carTp_rl = (TextView) findViewById(R.id.carTp_rl);
        carTp_rl.setText(String.valueOf(getIntent().getFloatExtra("tp_rl", 0.0f)));

        TextView carTp_rr = (TextView) findViewById(R.id.carTp_rr);
        carTp_rr.setText(String.valueOf(getIntent().getFloatExtra("tp_rr", 0.0f)));

        dis_from_me = (TextView) findViewById(R.id.dis_from_me);
        from_you = (TextView) findViewById(R.id.from_you_str);

        if(getIntent().getDoubleExtra("distance_from_me",0) == 0) {
            dis_from_me.setVisibility(View.INVISIBLE);
            from_you.setVisibility(View.INVISIBLE);
            reserveBtn.setVisibility(View.INVISIBLE);
        } else {
            dis_from_me.setText(String.valueOf(getIntent().getDoubleExtra("distance_from_me",0)) + " km");
            dis_from_me.setVisibility(View.VISIBLE);
            from_you.setVisibility(View.VISIBLE);
            reserveBtn.setVisibility(View.VISIBLE);
        }

        favButton = (Button) findViewById(R.id.favBtn);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favButton.getTag().equals("add")) {
                    FirebaseDatabase.getInstance().getReference().child("Favorites").child(firebaseUser.getUid()).child(plateNo).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Favorites").child(firebaseUser.getUid()).child(plateNo).removeValue();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(NetworkUtil.isConnected(this)) {
            reference = FirebaseDatabase.getInstance().getReference().child("Favorites").child(firebaseUser.getUid());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(plateNo).exists()) {
                        favButton.setBackgroundResource(R.drawable.ic_favorite_red);
                        favButton.setTag("added");
                    } else {
                        favButton.setBackgroundResource(R.drawable.ic_favorite_gray);
                        favButton.setTag("add");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if (isServiceRunning(reservationService.getClass())) {
                dis_from_me.setVisibility(View.INVISIBLE);
                from_you.setVisibility(View.INVISIBLE);
                reserveBtn.setVisibility(View.INVISIBLE);
                counter.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.VISIBLE);
            } else {
                dis_from_me.setVisibility(View.VISIBLE);
                from_you.setVisibility(View.VISIBLE);
                reserveBtn.setVisibility(View.VISIBLE);
                counter.setVisibility(View.INVISIBLE);
                cancelBtn.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initQRScanner(View v) {
        FirebaseUser usr = FirebaseAuth.getInstance().getCurrentUser();
        final View view = v;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(usr.getUid()).child("wallet");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double wall = dataSnapshot.getValue(Double.class);
                if(wall > 2) {
                    Intent intent = new Intent(CarDetailActivity.this, QRScannerActivity.class);
                    startActivity(intent);
                } else {
                    Snackbar snack = Snackbar.make(view, R.string.insufficientFunds, Snackbar.LENGTH_LONG);
                    snack.setDuration(10000);
                    snack.setAction("ADD FUNDS", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CarDetailActivity.this, WalletActivity.class);
                            startActivity(intent);
                        }
                    });
                    snack.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if(extras != null){
            initTextFields();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}
