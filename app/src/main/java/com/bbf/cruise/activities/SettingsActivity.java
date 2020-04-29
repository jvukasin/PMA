package com.bbf.cruise.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.os.Bundle;
import android.view.MenuItem;

import com.bbf.cruise.R;
import com.bbf.cruise.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(findViewById(R.id.content_preferences) != null) {
            if(savedInstanceState != null) {
                return;
            }

            getFragmentManager().beginTransaction().add(R.id.content_preferences, new SettingsFragment()).commit();
        }

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
