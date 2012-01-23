package com.slidehome.activities.apptray;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.slidehome.activities.ApplicationInfo;
import com.slidehome.activities.ApplicationLauncher;
import com.slidehome.activities.SlideHome;

/**
 * 
 * @author Bradley Booms <bradley.booms@gmail.com>
 *
 */
public class AppTrayLauncher extends ApplicationLauncher {
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Log.d(SlideHome.TAG, "onItemClick Called");

		ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);
		if (app.intent == null){
			
		} else {
	        startActivity(parent, app);
		}
	}
}
