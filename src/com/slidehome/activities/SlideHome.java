package com.slidehome.activities;

import android.app.SearchManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;
import com.slidehome.R;
import com.slidehome.activities.apptray.AppTrayFragment;
import com.slidehome.activities.apptray.AppTrayPagerAdapter;
import com.slidehome.providers.AppTrayItem;
import com.slidehome.providers.AppTrayItem.AppTrayItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * SlideHome main activity
 *
 * @author Chris Spooner <cmspooner@gmail.com>
 * @author Jakub Chrzanowski <jakub@chrzanowski.info>
 * @author Bradley Booms <bradley.booms@gmail.com>
 */
// This is a test
public class SlideHome extends FragmentActivity {

    public static final String TAG = SlideHome.class.getCanonicalName();

	private static ApplicationList mApplications;
	private GridView mGrid;
	private final BroadcastReceiver mApplicationsReceiver = new ApplicationsIntentReceiver();
	
	private ViewPager mPager;
	private AppTrayPagerAdapter mPagerAdapter;
	private ApplicationInfo mSelectedApp;
	
	// private Bundle mSavedInstanceState;

	// Test Tools
	// private long sTime;
	// private long mTime;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		System.out.println("-->SlideHome.java: onCreate Called");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.home);
		
		initializePreferences();
		
		registerIntentReceivers();

		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

		loadApplications(true);

		// for (ApplicationInfo each: mApplications){
		//     Log.d(TAG, each.title);
		// }

		bindApplications();
		
		// ArrayList<ResolveInfo> apps = mApplications.getAppsList();
		// savedInstanceState.putParcelableArrayList("apps",(ArrayList<ResolveInfo>)
		// apps);
	}


	private void initializePreferences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("showStatusBar", true)) {
			Log.d(TAG, "Clear Fullscreen Flag");
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			Log.d(TAG, "Set Fullscreen Flag");
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		mPager = (ViewPager) findViewById(R.id.app_tray);
		int visibility;
		if (prefs.getBoolean("enableAppTray", true)) {
			visibility = View.VISIBLE;
			initializePager(Integer.decode(prefs.getString("appTrayPageCount", "3")));
		} else {
			visibility = View.GONE;
		}
		Log.d(TAG, "Set AppTray visibility to: " + visibility);
		mPager.setVisibility(visibility);
	}


	private void initializePager(int appTrayPageCount) {
	
		Log.d(TAG, "initializePager creating " + appTrayPageCount + " pages.");
		
		ContentResolver cr = getContentResolver();
		List<Fragment> fragments = new Vector<Fragment>();
		for (int i = 1; i <= appTrayPageCount; i++) {
			ArrayList<AppTrayItem> items = new ArrayList<AppTrayItem>();
			Cursor itemCursor = cr.query(
					AppTrayItems.CONTENT_URI, 
					AppTrayItems.PROJECTION, 
					AppTrayItems.PAGE + " = ?", 
					new String[] {"" + i}, 
					AppTrayItems.POSITION + " ASC");
			if (itemCursor.moveToFirst()){
				do {
					items.add(new AppTrayItem(itemCursor));
				} while (itemCursor.moveToNext());
			}
			Bundle args = new Bundle();
			args.putInt(AppTrayFragment.PAGE, i);
			args.putParcelableArrayList(AppTrayFragment.APPTRAY_ITEMS, items);
			fragments.add(Fragment.instantiate(this, AppTrayFragment.class.getName(), args));
		}
		
		mPagerAdapter = new AppTrayPagerAdapter(super.getSupportFragmentManager(), fragments);
		
		if (mPager == null){
			mPager = (ViewPager)super.findViewById(R.id.app_tray);
		}
		mPager.setAdapter(mPagerAdapter);
		
		initializeGrid();
		mGrid.setOnItemLongClickListener(new ApplicationSelector());
	}


	@Override
	protected void onResume() {
		super.onResume();
        Log.d(TAG, "onResume Called");
        
		initializePreferences();
	}
	
	@Override
	public boolean onSearchRequested() {
        Log.d(TAG, "onSearchRequested Called");
		
        startActivity(new Intent(SearchManager.INTENT_ACTION_GLOBAL_SEARCH));
        return super.onSearchRequested();
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);

        Log.d(TAG, "restoreState Called");

		if (state == null) {
            Log.d(TAG, "no Bundle");
			return;
		}

        Log.d(TAG, "Bundle!");

		if (mApplications == null) {
            Log.d(TAG, "no mApplications");

			if (state.containsKey("apps")) {
                Log.d(TAG, "apps was saved!");

				ArrayList<ResolveInfo> apps = state.getParcelableArrayList("apps");
				mApplications = new ApplicationList(apps);
			} else {
				Log.d(TAG, "no 'apps' key");
				
				mApplications = new ApplicationList();
			}
		}

		if (state.containsKey("app_tray_page")) {
			mPager.setCurrentItem(state.getInt("app_tray_page"), false);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		Log.d(TAG, "onSaveInstanceState Called");

		ArrayList<ResolveInfo> apps = mApplications.getAppsList();
		outState.putParcelableArrayList("apps", (ArrayList<ResolveInfo>) apps);
		outState.putInt("app_tray_page", mPager.getCurrentItem());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

        Log.d(TAG, "onDestroy Called");

		// Remove the callback for the cached drawables or we leak
		// the previous Home screen on orientation change
		final int count = mApplications.size();
		for (int i = 0; i < count; i++) {
			mApplications.get(i).icon.setCallback(null);
		}

		unregisterReceiver(mApplicationsReceiver);
	}

    /**
     * @param isLaunching
     */
	private void loadApplications(boolean isLaunching) {

        Log.d(TAG, "loadApplications Called");

		if (isLaunching && mApplications != null) {
			return;
		}

		if (mApplications == null) {
			mApplications = new ApplicationList();
		} else {
			mApplications.loadApplications();
		}
	}

	private void updateApplicationList() {

        Log.d(TAG, "updateApplicationList Called");

		mApplications.loadApplicationList();
	}

	/**
	 * FROM ANDROID...No Changes...yet
	 * 
	 * Creates a new appplications adapter for the grid view and registers it.
	 */
	private void bindApplications() {

        Log.d(TAG, "bindApplications Called");

		initializeGrid();

		mGrid.setAdapter(new ApplicationInfoArrayAdapter(this, mApplications.getApps()));
		mGrid.setSelection(0);

		mGrid.setOnItemClickListener(new ApplicationLauncher());
	}


	private void initializeGrid() {
		if (mGrid == null) {
			mGrid = (GridView) findViewById(R.id.all_apps);
		}
	}

	private void registerIntentReceivers() {

        Log.d(TAG, "registerIntentReceivers Called");

		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		filter.addDataScheme("package");
		registerReceiver(mApplicationsReceiver, filter);
		
		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
		filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
		registerReceiver(mApplicationsReceiver, filter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

        Log.d(TAG, "onCreateOptionsMenu Called");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.apps_menu, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected Called");

		// Handle item selection
		switch (item.getItemId()) {

            // phone setting
            case R.id.phone_settings:
                Log.d(TAG, "Phone Settings!");
                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                break;

            // home settings
            case R.id.home_settings:
                Log.d(TAG, "Home Settings!");
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            // default action
            default:
                return super.onOptionsItemSelected(item);
		}

        return true;
	}

	private class ApplicationSelector implements AdapterView.OnItemLongClickListener {
		public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
            Log.d(TAG, "onItemLongClick Called");

			mSelectedApp = (ApplicationInfo) parent.getItemAtPosition(position);
			parent.setEnabled(false);
			
			return true;
		}
	}
	
	@Override
	public void onBackPressed(){
		if (mSelectedApp != null) {
			mSelectedApp = null;
			mGrid.setEnabled(true);
		} else {
			super.onBackPressed();
		}
	}

	private class ApplicationsIntentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "onReceive Called");
            Log.d(TAG, intent.getAction());

			updateApplicationList();
			loadApplications(false);
			bindApplications();
		}
	}

	private class ApplicationList {
		private ArrayList<ApplicationInfo> applications;
		private ArrayList<ResolveInfo> appsList;

		public ApplicationList() {

            Log.d(TAG, "ApplicationList() Called");

			this.applications = new ArrayList<ApplicationInfo>();
			this.loadApplicationList();
			this.loadApplications();
		}

		public ApplicationList(ArrayList<ResolveInfo> appsList) {

            Log.d(TAG, "ApplicationList(appsList) Called");

			this.applications = new ArrayList<ApplicationInfo>();
			this.setAppsList(appsList);
			this.loadApplications();
		}

		public ArrayList<ApplicationInfo> getApps() {
			return this.applications;
		}

		public ApplicationInfo get(int i) {
			return this.applications.get(i);
		}

		public int size() {
			return this.applications.size();
		}

		public ArrayList<ResolveInfo> getAppsList() {
			return this.appsList;
		}

		public void setAppsList(ArrayList<ResolveInfo> appsList) {

            Log.d(TAG, "setAppsList Called");

			this.appsList = appsList;
		}

		private void loadApplicationList() {

            Log.d(TAG, "loadApplicationList Called");

			PackageManager manager = getPackageManager();

			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

			this.appsList = (ArrayList<ResolveInfo>) manager.queryIntentActivities(mainIntent, 0);
			Collections.sort(this.appsList, new ResolveInfo.DisplayNameComparator(manager));
		}

		public void loadApplications() {

            Log.d(TAG, "loadApplications Called");

			PackageManager manager = getPackageManager();

			// mTime = System.currentTimeMillis();
			// System.out.println("%%> Time since start: "
			// + ((mTime - sTime) / 1000.0) + " - got the list");
			// sTime = System.currentTimeMillis();

			if (this.appsList != null) {
				final int count = this.appsList.size();

				if (this.applications == null) {
					this.applications = new ArrayList<ApplicationInfo>(count);
				}
				this.applications.clear();

				for (int i = 0; i < count; i++) {
					ResolveInfo info = this.appsList.get(i);

					this.applications.add(new ApplicationInfo(info, manager));
				}
			}
		}
	}
}