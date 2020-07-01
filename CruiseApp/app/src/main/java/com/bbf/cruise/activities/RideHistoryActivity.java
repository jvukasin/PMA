package com.bbf.cruise.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bbf.cruise.R;
import com.bbf.cruise.adapters.RideHistoryAdapter;
import com.bbf.cruise.tools.NetworkUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import model.RideHistory;
import model.RideHistoryItem;

public class RideHistoryActivity extends AppCompatActivity {

    private ListView rideHistoryList;
    private TextView loading;
    private ArrayList<RideHistoryItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);
        setTitle("Ride history");

        rideHistoryList = findViewById(R.id.rideHistoryItems);
        loading = findViewById(R.id.ride_history_loading);

        loading.setVisibility(View.VISIBLE);
        rideHistoryList.setVisibility(View.GONE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RideHistory").child(user.getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    list.add(new RideHistoryItem(snapshot.getValue(RideHistory.class)));
                }
                loading.setVisibility(View.GONE);
                rideHistoryList.setVisibility(View.VISIBLE);
                RideHistoryAdapter rideHistoryAdapter = new RideHistoryAdapter(RideHistoryActivity.this, list);
                rideHistoryList.setAdapter(rideHistoryAdapter);
                rideHistoryList.setDivider(null);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NetworkUtil.isConnected(RideHistoryActivity.this);
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
