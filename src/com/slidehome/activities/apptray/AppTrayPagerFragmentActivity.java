package com.slidehome.activities.apptray;

import java.util.List;
import java.util.Vector;

import com.slidehome.R;
import com.slidehome.providers.AppTrayItem.AppTrayItems;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

/**
 * 
 * @author Bradley Booms <bradley.booms@gmail.com>
 *
 */
public class AppTrayPagerFragmentActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
	private static final String TAG = AppTrayPagerFragmentActivity.class.getName();
	
	private ViewPager mViewPager;
	private AppTrayPagerAdapter mPagerAdapter;
	private int appTrayPageCount;

	@Override
	protected void onCreate(Bundle saveInstanceState){
		Log.d(TAG, "onCreate called.");
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		appTrayPageCount = prefs.getInt("appTrayPageCount", 3);
		
		Log.d(TAG, "Creating " + appTrayPageCount + " pages.");
		
		ContentResolver cr = getContentResolver();
		List<Fragment> fragments = new Vector<Fragment>();
		for (int i = 1; i <= appTrayPageCount; i++) {
			cr.query(
					AppTrayItems.CONTENT_URI, 
					AppTrayItems.PROJECTION, 
					AppTrayItems.PAGE + " = ?", 
					new String[] {"" + i}, 
					AppTrayItems.POSITION + " ASC");
			Bundle args = new Bundle();
			args.putInt(AppTrayFragment.PAGE, i);
			fragments.add(Fragment.instantiate(this, AppTrayFragment.class.getName(), args));
		}
		
		mViewPager = (ViewPager)super.findViewById(R.id.app_tray);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
