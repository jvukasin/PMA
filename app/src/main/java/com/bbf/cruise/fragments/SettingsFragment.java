package com.bbf.cruise.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;

import com.bbf.cruise.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preference pref;
    private String summaryStr;
    private SharedPreferences sharedPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        sharedPref = this.getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);

        putUserValues();
    }

    private void putUserValues() {
        Preference firstName = findPreference("firstName");
        firstName.setSummary(sharedPref.getString("firstName", ""));

        Preference lastName = findPreference("lastName");
        lastName.setSummary(sharedPref.getString("lastName", ""));

        Preference email = findPreference("email");
        email.setSummary(sharedPref.getString("email", ""));
        email.setEnabled(false);

        Preference phone = findPreference("phone");
        phone.setSummary(sharedPref.getString("phone", ""));

        Preference distance = findPreference("distanceMode");
        distance.setSummary(sharedPref.getString("distanceMode", "km"));
    }

    @Override
    public void onResume() {
        super.onResume();

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
        summaryStr = (String) pref.getSummary(); //stari

        String prefixStr = sharedPreferences.getString(key, "");
        pref.setSummary(prefixStr); //novi

        SharedPreferences.Editor editor = sharedPref.edit();
        if(key.equals("firstName")) {
            editor.putString("firstName", prefixStr);
        } else if (key.equals("lastName")) {
            editor.putString("lastName", prefixStr);
        }  else if (key.equals("phone")) {
            editor.putString("phone", prefixStr);
        }  else if (key.equals("distanceMode")) {
            editor.putString("distanceMode", prefixStr);
        }
        editor.commit();

        //TODO sacuvati i u bazi promene

    }
}
