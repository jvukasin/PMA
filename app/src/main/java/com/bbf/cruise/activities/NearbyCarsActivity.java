package com.bbf.cruise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
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
        list.add(new CarItem("BMW", "320d", R.drawable.sedan_512, "NS 643SK", 1.3, 380));
        list.add(new CarItem("Renault", "Clio", R.drawable.sedan_512, "NS 274DJ", 2.2, 322));
        list.add(new CarItem("Opel", "Astra", R.drawable.sedan_512, "NS 486BR", 2.5, 263));
    }
}
