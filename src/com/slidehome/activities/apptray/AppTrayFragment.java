package com.slidehome.activities.apptray;

import com.slidehome.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
	
	public AppTrayFragment() {
		Bundle args = getArguments();
		this.pageNumber = (int)args.getInt(PAGE);
	}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.apptray_fragment, container, false);
    }
}
