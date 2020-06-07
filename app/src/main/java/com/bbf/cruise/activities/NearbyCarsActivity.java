package com.bbf.cruise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bbf.cruise.R;
import com.bbf.cruise.adapters.NearbyCarsAdapter;
import com.bbf.cruise.fragments.MapFragment;

import java.util.ArrayList;

import model.Car;
import model.CarItem;

public class NearbyCarsActivity extends AppCompatActivity {

    private ListView listV;
    private ArrayList<CarItem> cars = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_cars);
        setTitle(R.string.nearByCars);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listV = (ListView) findViewById(R.id.nearbycars_listview);

        ArrayList<Car> carArrayList = (ArrayList<Car>) getIntent().getSerializableExtra("cars");
        double lat = getIntent().getDoubleExtra("myLat", 0);
        double lng = getIntent().getDoubleExtra("myLng", 0);
        prepareList(carArrayList, lat, lng);

        NearbyCarsAdapter adapter = new NearbyCarsAdapter(this, cars);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareList(ArrayList<Car> list, double lat, double lng) {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        int radius = sharedPreferences.getInt("radius", 30);
        for(Car car: list){
            double distance = Math.round(MapFragment.calculateDistance(lat, lng, car.getLocation().getLatitude(), car.getLocation().getLongitude()) * 100.0) / 100.0;
            if(distance <= 30){
                CarItem carItem = new CarItem(car);
                carItem.setDistanceFromMe(distance);
                cars.add(carItem);
            }
        }
    }

}
