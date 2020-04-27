package com.bbf.cruise.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.bbf.cruise.R;
import com.bbf.cruise.adapters.RideHistoryAdapter;

public class RideHistory extends AppCompatActivity {

    private ListView rideHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);
        setTitle("Ride history");

        rideHistoryList = findViewById(R.id.rideHistoryItems);
        RideHistoryAdapter rideHistoryAdapter = new RideHistoryAdapter(this);
        rideHistoryList.setAdapter(rideHistoryAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
