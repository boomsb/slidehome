package com.slidehome.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import com.slidehome.R;


/**
 * Application settings activity
 *
 * @author Jakub Chrzanowski <jakub@chrzanowski.info>
 */
public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = SettingsActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Loading preferences from external xml file");

        addPreferencesFromResource(R.xml.preferences);
    }
}
