package cmspooner.slidehome.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import cmspooner.slidehome.R;


/**
 * Application settings activity
 *
 * @author Jakub Chrzanowski <jakub@chrzanowski.info>
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
