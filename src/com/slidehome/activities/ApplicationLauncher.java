package com.slidehome.activities;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

/**
 * 
 * @author Chris Spooner <cmspooner@gmail.com>
 *
 */
public class ApplicationLauncher implements AdapterView.OnItemClickListener {
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Log.d(SlideHome.TAG, "onItemClick Called");

        startActivity(parent, (ApplicationInfo) parent.getItemAtPosition(position));
	}
	
	public void startActivity(AdapterView<?> parent, ApplicationInfo app) {
		parent.getContext().startActivity(app.intent);
	}
}
