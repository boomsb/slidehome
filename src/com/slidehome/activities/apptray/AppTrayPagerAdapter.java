package com.slidehome.activities.apptray;

import java.util.List;

import com.slidehome.activities.SlideHome;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * 
 * @author Bradley Booms <bradley.booms@gmail.com>
 *
 */
public class AppTrayPagerAdapter extends FragmentStatePagerAdapter {
	private List<Fragment> fragments;

	public AppTrayPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		Log.d(SlideHome.TAG, "getPage " + position + " : " + position % this.fragments.size());
		//return this.fragments.get(position % this.fragments.size());
		return this.fragments.get(position);
	}

	@Override
	public int getCount() {
		//return this.fragments.size()+2;
		return this.fragments.size();
	}

	public int getRealCount() {
		return this.fragments.size();
	}
}
