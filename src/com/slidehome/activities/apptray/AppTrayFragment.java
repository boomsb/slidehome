package com.slidehome.activities.apptray;

import com.slidehome.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author Bradley Booms <bradley.booms@gmail.com>
 *
 */
public class AppTrayFragment extends Fragment {
	public final static String PAGE = "page";
	public final static String APPTRAY_ITEMS = "apptray_items";
	
	private int pageNumber;
	
	public AppTrayFragment() {}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
		Bundle args = getArguments();
		pageNumber = (int)args.getInt(PAGE);
    	
    	Log.d(AppTrayFragment.class.getName(), "onCreateView called for page " + pageNumber + ".");
    	
    	return inflater.inflate(R.layout.apptray_fragment, container, false);
    }
}
