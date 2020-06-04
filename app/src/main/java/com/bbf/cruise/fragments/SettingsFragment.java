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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preference pref;
    private String summaryStr;
    private SharedPreferences sharedPref;
    private FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        sharedPref = this.getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        auth = FirebaseAuth.getInstance();

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

        Preference slide = findPreference("slider");
        int val = sharedPref.getInt("radius", 30);
        String sVal = Integer.toString(val) + " km";
        slide.setSummary(sVal);

        //Ako zelimo km u mi
//        Preference distance = findPreference("distanceMode");
//        distance.setSummary(sharedPref.getString("distanceMode", "km"));
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

        SharedPreferences.Editor editor = sharedPref.edit();
        if(key.equals("slider")) {
            pref = findPreference(key);
            int num = sharedPreferences.getInt(key, 30);
            pref.setSummary(String.format("%d km", num));
            editor.putInt("radius", num);
        } else  {
            pref = findPreference(key);
            summaryStr = (String) pref.getSummary(); //stari

            String prefixStr = sharedPreferences.getString(key, "");
            pref.setSummary(prefixStr); //novi

            //TODO DA LI U ASYNCTASK?
            String firebaseUserUID = auth.getCurrentUser().getUid();
            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            DatabaseReference reference = rootNode.getReference("Users");

            if(key.equals("firstName")) {
                editor.putString("firstName", prefixStr);
                reference.child(firebaseUserUID).child("firstName").setValue(prefixStr);
            } else if (key.equals("lastName")) {
                editor.putString("lastName", prefixStr);
                reference.child(firebaseUserUID).child("lastName").setValue(prefixStr);
            }  else if (key.equals("phone")) {
                editor.putString("phone", prefixStr);
                reference.child(firebaseUserUID).child("phoneNumber").setValue(prefixStr);
            }
//        Ako zelimo da menja iz km u mi
//        else if (key.equals("distanceMode")) {
//            editor.putString("distanceMode", prefixStr);
//        }
        }
        editor.apply();
    }
}
