package com.epson.moverio.bt300.sample.samplehwkey;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PreferenceActivityCustom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_custom);
        FragmentManager mFragmentManager = getFragmentManager();
        PreferenceCustomFragment preference = new PreferenceCustomFragment();
        mFragmentManager.beginTransaction().replace(android.R.id.content, preference).commit();
    }
}
