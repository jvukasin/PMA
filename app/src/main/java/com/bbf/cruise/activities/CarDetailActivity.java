package com.bbf.cruise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbf.cruise.MainActivity;
import com.bbf.cruise.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.Car;

public class CarDetailActivity extends AppCompatActivity {

    private Button rentBtn;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private Button favButton;
    private String plateNo;
    private Car car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        setTitle(R.string.carInfo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        rentBtn = (Button) findViewById(R.id.rentBtn);
        rentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initQRScanner();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        plateNo = getIntent().getStringExtra("plate");

        initTextFields();
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

        TextView dis_from_me = (TextView) findViewById(R.id.dis_from_me);
        TextView from_you = (TextView) findViewById(R.id.from_you_str);

        if(getIntent().getDoubleExtra("distance_from_me",0) == 0) {
            dis_from_me.setVisibility(View.INVISIBLE);
            from_you.setVisibility(View.INVISIBLE);
        } else {
            dis_from_me.setText(String.valueOf(getIntent().getDoubleExtra("distance_from_me",0)) + " km");
            dis_from_me.setVisibility(View.VISIBLE);
            from_you.setVisibility(View.VISIBLE);
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

        reference = FirebaseDatabase.getInstance().getReference().child("Favorites").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(plateNo).exists()) {
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

    private void initQRScanner() {
        Intent intent = new Intent(CarDetailActivity.this, QRScannerActivity.class);
        startActivity(intent);
    }
}
