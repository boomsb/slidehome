package com.slidehome.activities.apptray;

import com.slidehome.activities.SlideHome;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

public class AppTrayPager extends ViewPager {
	
	AppTrayPagerAdapter mPagerAdapter;

	public AppTrayPager(Context context) {
		super(context);
		initialize();
	}

	public AppTrayPager(Context context, AttributeSet attributes) {
		super(context, attributes);
		initialize();
	}
	
	protected void initialize() {
		
		setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				int count = mPagerAdapter.getRealCount();
				Log.d(SlideHome.TAG, "onPageSelected for page " + arg0 + " of " + count);

//				if (arg0 == 0){
//					setCurrentItem(count, false);
//				}
//				if (arg0 == count + 1){
//					setCurrentItem(1, false);
//				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override
	public void setAdapter(PagerAdapter adapter){
		super.setAdapter(adapter);
		
		if (adapter instanceof AppTrayPagerAdapter){
			mPagerAdapter = (AppTrayPagerAdapter) adapter;
		}
	}
}
