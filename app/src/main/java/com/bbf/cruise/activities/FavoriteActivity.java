package com.bbf.cruise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bbf.cruise.R;
import com.bbf.cruise.adapters.FavoriteCarsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import model.Car;
import model.CarItem;

public class FavoriteActivity extends AppCompatActivity {

    private ListView listView;
    private TextView emptyListView;
    private ArrayList<CarItem> favoriteCars = new ArrayList<>();
    private ArrayList<String> plates = new ArrayList<>();
    private DatabaseReference referencePlates;
    private DatabaseReference referenceCars;
    private Activity thisActivity;
    private FavoriteCarsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        setTitle(R.string.favorites);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        thisActivity = this;
        listView = (ListView) findViewById(R.id.favorite_list);
        emptyListView = (TextView) findViewById(R.id.favorite_list_empty);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        referencePlates = FirebaseDatabase.getInstance().getReference("Favorites").child(firebaseUser.getUid());
        referenceCars = FirebaseDatabase.getInstance().getReference("cars");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CarItem item = favoriteCars.get(position);

                Intent intent = new Intent(FavoriteActivity.this, CarDetailActivity.class);
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
    public void onResume() {
        super.onResume();

        referencePlates.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteCars.clear();
                for(DataSnapshot carSnapshot: dataSnapshot.getChildren()) {
                    String plts = carSnapshot.getKey();
                    referenceCars.child(plts).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            CarItem ci = new CarItem(dataSnapshot.getValue(Car.class));
                            favoriteCars.add(ci);
                            adapter.notifyDataSetChanged();
                            if(favoriteCars.isEmpty()) {
                                emptyListView.setVisibility(View.VISIBLE);
                            } else {
                                emptyListView.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                adapter = new FavoriteCarsAdapter(thisActivity, favoriteCars);
                listView.setAdapter(adapter);
                if(plates.isEmpty()) {
                    emptyListView.setVisibility(View.VISIBLE);
                } else {
                    emptyListView.setVisibility(View.INVISIBLE);
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

}
