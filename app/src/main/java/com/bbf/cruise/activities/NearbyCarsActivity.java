package com.bbf.cruise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bbf.cruise.R;
import com.bbf.cruise.adapters.NearbyCarsAdapter;

import java.util.ArrayList;

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
        prepareList(cars);
        NearbyCarsAdapter adapter = new NearbyCarsAdapter(this, cars);
        listV.setAdapter(adapter);

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CarItem item = cars.get(position);

                Intent intent = new Intent(NearbyCarsActivity.this, CarDetailActivity.class);
                intent.putExtra("name", item.getCarName());
                intent.putExtra("avatar", item.getAvatar());
                intent.putExtra("distance", item.getDistance());
                intent.putExtra("fuel_distance", item.getFuel_distance());
                intent.putExtra("plate", item.getReg_number());
                intent.putExtra("no_of_rides", item.getNo_of_rides());
                intent.putExtra("rating", item.getRating());
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

    private void prepareList(ArrayList<CarItem> list) {
        //TODO izvuci iz baze i sta sve treba
        list.add(new CarItem("BMW", "320d", R.drawable.sedan_512, "NS 643SK", 1.3, 380, 2, 5.0));
        list.add(new CarItem("Renault", "Clio", R.drawable.sedan_512, "NS 274DJ", 2.2, 322, 4, 4.5));
        list.add(new CarItem("Opel", "Astra", R.drawable.sedan_512, "NS 486BR", 2.5, 263, 3, 4.3));
    }

}
