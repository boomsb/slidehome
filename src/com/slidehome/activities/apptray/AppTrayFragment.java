package com.slidehome.activities.apptray;

import java.util.ArrayList;

import com.slidehome.R;
import com.slidehome.activities.ApplicationInfoArrayAdapter;
import com.slidehome.activities.ApplicationInfo;
import com.slidehome.activities.SlideHome;
import com.slidehome.providers.AppTrayItem;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * 
 * @author Bradley Booms <bradley.booms@gmail.com>
 *
 */
public class AppTrayFragment extends Fragment implements OnAppInfoSelectedListener {
	public final static String PAGE = "page";
	public final static String APPTRAY_ITEMS = "apptray_items";
	
	private int pageNumber;
	private ArrayList<AppTrayItem> items;
	private ApplicationInfoArrayAdapter mAdapter;
	private GridView mGrid;
	
	public AppTrayFragment() {}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View view = inflater.inflate(R.layout.apptray_fragment, container, false);
    	
		Bundle args = getArguments();
		pageNumber = args.getInt(PAGE);
		items = args.getParcelableArrayList(APPTRAY_ITEMS);
    	
    	Log.d(SlideHome.TAG, "onCreateView called for page " + pageNumber + ".");
    	Log.d(SlideHome.TAG, "with " + items.size() + " items.");
    	
    	ApplicationInfo[] appInfo = new ApplicationInfo[8];

		PackageManager manager = getActivity().getPackageManager();
    	for( AppTrayItem item : items){
    		if (item.getPosition() < 8){
	    		ResolveInfo info = manager.resolveActivity(
	    				new Intent().setComponent(
	    						new ComponentName(item.getPackageName(), item.getClassName())
						), PackageManager.MATCH_DEFAULT_ONLY);
	    		if (info != null){
	    			appInfo[item.getPosition()] = new ApplicationInfo(info, manager);
	    		}
    		}
    	}
    	
    	// fill blanks with a special ApplicationInfo..
    	Drawable icon = container.getContext().getResources().getDrawable(R.drawable.super_mono_sticker_plus);
    	for (int i = 0; i < 8; i++) {
    		if (appInfo[i] == null) {
    			appInfo[i] = new ApplicationInfo(icon);
    		}
    	}
    	
    	mAdapter = new ApplicationInfoArrayAdapter(getActivity(), appInfo);
    	
    	mGrid = (GridView) view.findViewById(R.id.apptray_page_grid);
    	if (mGrid != null){
    		Log.d(SlideHome.TAG, "Set apptray grid adapter.");
    		mGrid.setAdapter(mAdapter);
    		
    		mGrid.setOnItemClickListener(new AppTrayLauncher());
    	}
    	
    	return view;
    }
    
    public static interface OnEmptyItemClickListener {
    	void onEmptyItemClick(AppTrayFragment listener);
    }

	@Override
	public void onItemSelected(ApplicationInfo item) {
		// TODO Auto-generated method stub
		
	}
}
