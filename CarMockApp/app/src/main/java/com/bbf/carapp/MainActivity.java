package com.bbf.carapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bbf.carapp.activities.CarDetailActivity;
import com.bbf.carapp.adapters.CarListAdapter;
import com.bbf.carapp.model.Car;
import com.bbf.carapp.model.CarItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<CarItem> favoriteCars = new ArrayList<>();
    private DatabaseReference reference;
    private CarListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.mainTitle);
        listView = (ListView) findViewById(R.id.listview);
        reference = FirebaseDatabase.getInstance().getReference("cars");

        adapter = new CarListAdapter(this, favoriteCars);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CarItem item = favoriteCars.get(position);

                Intent intent = new Intent(MainActivity.this, CarDetailActivity.class);
                intent.putExtra("name", item.getCarName());
                intent.putExtra("mileage", String.format("%.1f", item.getMileage()));
                intent.putExtra("fuel_distance", Double.toString(item.getFuel_distance()));
                intent.putExtra("plate", item.getReg_number());
                intent.putExtra("no_of_rides", item.getNo_of_rides());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteCars.clear();
                for(DataSnapshot carSnapshot: dataSnapshot.getChildren()){
                    favoriteCars.add(new CarItem(carSnapshot.getValue(Car.class)));
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
