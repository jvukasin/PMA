package com.bbf.cruise.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.bbf.cruise.R;

import java.util.ArrayList;

import model.CarItem;

public class FavoriteActivity extends AppCompatActivity {

    private ListView lista;
    private ArrayList<CarItem> cars = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        setTitle(R.string.favorites);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lista = (ListView) findViewById(R.id.favorite_list);

        //TODO: implement
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
