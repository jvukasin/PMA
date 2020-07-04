package com.bbf.carapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bbf.carapp.R;
import com.bbf.carapp.fragments.RideMapFragment;
import com.bbf.carapp.utils.FragmentTransition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RideActivity extends AppCompatActivity {

    private DatabaseReference carReference;
    private DatabaseReference rentReference;
    private String plates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        plates = getIntent().getStringExtra("plates");
        carReference = FirebaseDatabase.getInstance().getReference("cars");

        RideMapFragment fragment = RideMapFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("plates", plates);
        bundle.putDouble("lat", getIntent().getDoubleExtra("lat", 0));
        bundle.putDouble("lng", getIntent().getDoubleExtra("lng", 0));
        fragment.setArguments(bundle);
        FragmentTransition.addRideMap(fragment, this, false);
    }
}
