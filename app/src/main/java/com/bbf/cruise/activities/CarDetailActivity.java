package com.bbf.cruise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbf.cruise.R;

public class CarDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        setTitle(R.string.carInfo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initTextFields();
    }

    private void initTextFields() {
        TextView carName = (TextView) findViewById(R.id.carName);
        carName.setText(getIntent().getStringExtra("name"));

        TextView carPlate = (TextView) findViewById(R.id.carPlate);
        carPlate.setText(getIntent().getStringExtra("plate"));

        TextView carRating = (TextView) findViewById(R.id.carRating);
        carRating.setText(String.valueOf(getIntent().getDoubleExtra("rating", 0.0)));

        TextView carFuel = (TextView) findViewById(R.id.carFuel);
        carFuel.setText(String.valueOf(getIntent().getDoubleExtra("fuel_distance", 0.0)) + " km");

        TextView carDistance = (TextView) findViewById(R.id.carDistance);
        carDistance.setText(String.valueOf(getIntent().getDoubleExtra("distance", 0.0)) + " km");

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
}
