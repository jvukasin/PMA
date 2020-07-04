package com.bbf.cruise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bbf.cruise.R;
import com.bbf.cruise.adapters.NearbyCarsAdapter;
import com.bbf.cruise.fragments.MapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import model.Car;
import model.CarItem;

public class NearbyCarsActivity extends AppCompatActivity {

    private ListView listV;
    private ArrayList<CarItem> cars = new ArrayList<>();
    private DatabaseReference databaseReference;
    private double lat, lng;
    private NearbyCarsAdapter adapter;
    private DatabaseReference referenceReservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_cars);
        setTitle(R.string.nearByCars);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference("cars");
        referenceReservations = FirebaseDatabase.getInstance().getReference("Reservations");

        listV = (ListView) findViewById(R.id.nearbycars_listview);

        lat = getIntent().getDoubleExtra("myLat", 0);
        lng = getIntent().getDoubleExtra("myLng", 0);

        adapter = new NearbyCarsAdapter(this, cars);
        listV.setAdapter(adapter);

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CarItem item = cars.get(position);

                Intent intent = new Intent(NearbyCarsActivity.this, CarDetailActivity.class);
                intent.putExtra("name", item.getCarName());
                intent.putExtra("avatar", item.getAvatar());
                intent.putExtra("mileage", String.format("%.1f", item.getMileage()));
                intent.putExtra("distance_from_me", item.getDistanceFromMe());
                intent.putExtra("fuel_distance", Double.toString(item.getFuel_distance()));
                intent.putExtra("plate", item.getReg_number());
                intent.putExtra("no_of_rides", item.getNo_of_rides());
                intent.putExtra("rating", item.getRating());
                intent.putExtra("tp_fl", item.getTp_fl());
                intent.putExtra("tp_fr", item.getTp_fr());
                intent.putExtra("tp_rl", item.getTp_rl());
                intent.putExtra("tp_rr", item.getTp_rr());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cars.clear();
                ArrayList<Car> tempCars = new ArrayList<>();
                for(DataSnapshot carSnapshot: dataSnapshot.getChildren()){
                    tempCars.add(carSnapshot.getValue(Car.class));
                }
                prepareList(tempCars, lat, lng);
                adapter.notifyDataSetChanged();
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

    private void prepareList(ArrayList<Car> list, double lat, double lng) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        int radius = sharedPreferences.getInt("radius", 30);
        for(Car car: list){
            if(car.isOccupied() && MapFragment.isReservedByMe(referenceReservations,car.getReg_number(), firebaseUser)){
                double distance = Math.round(MapFragment.calculateDistance(lat, lng, car.getLocation().getLatitude(), car.getLocation().getLongitude()) * 10.0) / 10.0;
                if(distance <= radius){
                    CarItem carItem = new CarItem(car);
                    carItem.setDistanceFromMe(distance);
                    cars.add(carItem);
                }
            }else if(car.isOccupied() && !MapFragment.isReservedByMe(referenceReservations, car.getReg_number(), firebaseUser)){
                continue;
            }else{
                double distance = Math.round(MapFragment.calculateDistance(lat, lng, car.getLocation().getLatitude(), car.getLocation().getLongitude()) * 10.0) / 10.0;
                if(distance <= radius){
                    CarItem carItem = new CarItem(car);
                    carItem.setDistanceFromMe(distance);
                    cars.add(carItem);
                }
            }
        }
    }


}
