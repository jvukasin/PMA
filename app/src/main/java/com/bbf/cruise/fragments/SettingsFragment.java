package com.bbf.cruise.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;

import com.bbf.cruise.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preference pref;
    private String summaryStr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO izvuci iz baze podatke o korisniku
        Preference firstName = findPreference("firstName");
        firstName.setSummary("Petar");
        Preference lastname = findPreference("lastName");
        lastname.setSummary("Peric");
        Preference email = findPreference("email");
        email.setSummary("pera@gmail.com");
        Preference phone = findPreference("phone");
        phone.setSummary("+3816312345");
        Preference distance = findPreference("prefDistance");
        distance.setSummary("Km");

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        pref = findPreference(key);

        summaryStr = (String) pref.getSummary();

        String prefixStr = sharedPreferences.getString(key, "");

        pref.setSummary(prefixStr);
        //TODO save to DB, ovde ili u onPause?

    }
}
