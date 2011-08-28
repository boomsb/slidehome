package cmspooner.slidehome.activities;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import cmspooner.slidehome.R;

/**
 * SlideHome main activity
 *
 * @author Chris Spooner <cmspooner@gmail.com>
 * @author Jakub Chrzanowski <jakub@chrzanowski.info>
 */
public class SlideHome extends Activity {

	private boolean isFullscreen;

	private static ApplicationList mApplications;

	private GridView mGrid;

	private final BroadcastReceiver mApplicationsReceiver = new ApplicationsIntentReceiver();

	private boolean mHomeDown;
	private boolean mBackDown;
	private boolean mMenuDown;
	private boolean mSearchDown;

	// private Bundle mSavedInstanceState;

	// Test Tools
	// private long sTime;
	// private long mTime;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		System.out.println("-->SlideHome.java: onCreate Called");

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (isFullscreen) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		setContentView(R.layout.home);

		registerIntentReceivers();

		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

		loadApplications(true);

		// System.out.println("------------------mApplications------------");
		// for (ApplicationInfo each: mApplications){
		// System.out.println("     "+each.title);
		// }
		// System.out.println("-------------------------------------------");

		bindApplications();

		// ArrayList<ResolveInfo> apps = mApplications.getAppsList();
		// savedInstanceState.putParcelableArrayList("apps",(ArrayList<ResolveInfo>)
		// apps);
	}

	@Override
	protected void onResume() {
		super.onResume();

		System.out.println("-->SlideHome.java: onResume Called");

	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);

		System.out.println("-->SlideHome.java: restoreState Called");

		if (state == null) {
			System.out.println("---> no Bundle");
			return;
		}
		System.out.println("---> Bundle!");

		if (mApplications == null) {
			System.out.println("**> no mApplications");

			if (state.containsKey("apps")) {
				System.out.println("**> apps was saved!");
				ArrayList<ResolveInfo> apps = state
						.getParcelableArrayList("apps");
				mApplications = new ApplicationList(apps);
			} else {
				System.out.println("---> no 'apps' key");
				mApplications = new ApplicationList();
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		System.out.println("-->SlideHome.java: onSaveInstanceState Called");

		ArrayList<ResolveInfo> apps = mApplications.getAppsList();

		outState.putParcelableArrayList("apps", (ArrayList<ResolveInfo>) apps);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		System.out.println("-->SlideHome.java: onDestroy Called");

		// Remove the callback for the cached drawables or we leak
		// the previous Home screen on orientation change
		final int count = mApplications.size();
		for (int i = 0; i < count; i++) {
			mApplications.get(i).icon.setCallback(null);
		}

		unregisterReceiver(mApplicationsReceiver);
	}

	private void loadApplications(boolean isLaunching) {

		System.out.println("-->SlideHome.java: loadApplications Called");

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

		System.out.println("-->SlideHome.java: updateApplicationList Called");

		mApplications.loadApplicationList();
	}

	/**
	 * FROM ANDROID...No Changes...yet
	 * 
	 * Creates a new appplications adapter for the grid view and registers it.
	 */
	private void bindApplications() {

		System.out.println("-->SlideHome.java: bindApplications Called");

		if (mGrid == null) {
			mGrid = (GridView) findViewById(R.id.all_apps);
		}

		mGrid.setAdapter(new ApplicationsAdapter(this, mApplications.getApps()));
		mGrid.setSelection(0);

		mGrid.setOnItemClickListener(new ApplicationLauncher());
	}

	private void registerIntentReceivers() {

		System.out.println("-->SlideHome.java: registerIntentReceivers Called");

		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		registerReceiver(mApplicationsReceiver, filter);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		System.out.println("-->SlideHome.java: dispatchKeyEvent Called");

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				mBackDown = true;
				return true;
			case KeyEvent.KEYCODE_HOME:
				mHomeDown = true;
				return true;
			case KeyEvent.KEYCODE_MENU:
				mMenuDown = true;
				return true;
			case KeyEvent.KEYCODE_SEARCH:
				mSearchDown = true;
				return true;
			}
		} else if (event.getAction() == KeyEvent.ACTION_UP) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				if (!event.isCanceled()) {
					// Do BACK behavior.
				}
				mBackDown = true;
				return true;
			case KeyEvent.KEYCODE_HOME:
				if (!event.isCanceled()) {
					// Do HOME behavior.
				}
				mHomeDown = true;
				return true;
			case KeyEvent.KEYCODE_MENU:
				if (!event.isCanceled()) {
					// Do Menu behavior.
					openOptionsMenu();
				}
				mMenuDown = true;
				return true;
			case KeyEvent.KEYCODE_SEARCH:
				if (!event.isCanceled()) {
					// Do Search behavior.
				}
				mSearchDown = true;
				return true;
			}
		}

		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		System.out.println("-->SlideHome.java: onCreateOptionsMenu Called");

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.apps_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		System.out.println("-->SlideHome.java: onOptionsItemSelected Called");

		// Handle item selection
		switch (item.getItemId()) {

            // phone setting
            case R.id.phone_settings:
                System.out.println("--->Phone Settings!");
                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                break;

            // home settings
            case R.id.home_settings:
                System.out.println("--->Home Settings!");
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            // default action
            default:
                return super.onOptionsItemSelected(item);
		}

        return true;
	}

	/**
	 * FROM ANDROID...No Changes...yet
	 * 
	 * GridView adapter to show the list of all installed applications.
	 */
	private class ApplicationsAdapter extends ArrayAdapter<ApplicationInfo> {
		private Rect mOldBounds = new Rect();

		public ApplicationsAdapter(Context context,
				ArrayList<ApplicationInfo> apps) {
			super(context, 0, apps);

			System.out
					.println("-->SlideHome.java.ApplicationsAdapter: ApplicationsAdapter Called");

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			System.out
					.println("-->SlideHome.java.ApplicationsAdapter: getView Called");

			final ApplicationInfo info = mApplications.get(position);

			if (convertView == null) {
				final LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.application, parent,
						false);
			}

			Drawable icon = info.icon;

			if (!info.filtered) {
				final Resources resources = getContext().getResources();
				int width = (int) resources
						.getDimension(android.R.dimen.app_icon_size);
				int height = (int) resources
						.getDimension(android.R.dimen.app_icon_size);

				final int iconWidth = icon.getIntrinsicWidth();
				final int iconHeight = icon.getIntrinsicHeight();

				if (icon instanceof PaintDrawable) {
					PaintDrawable painter = (PaintDrawable) icon;
					painter.setIntrinsicWidth(width);
					painter.setIntrinsicHeight(height);
				}

				if (width > 0 && height > 0
						&& (width < iconWidth || height < iconHeight)) {
					final float ratio = (float) iconWidth / iconHeight;

					if (iconWidth > iconHeight) {
						height = (int) (width / ratio);
					} else if (iconHeight > iconWidth) {
						width = (int) (height * ratio);
					}

					final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
							: Bitmap.Config.RGB_565;
					final Bitmap thumb = Bitmap.createBitmap(width, height, c);
					final Canvas canvas = new Canvas(thumb);
					canvas.setDrawFilter(new PaintFlagsDrawFilter(
							Paint.DITHER_FLAG, 0));
					// Copy the old bounds to restore them later
					// If we were to do oldBounds = icon.getBounds(),
					// the call to setBounds() that follows would
					// change the same instance and we would lose the
					// old bounds
					mOldBounds.set(icon.getBounds());
					icon.setBounds(0, 0, width, height);
					icon.draw(canvas);
					icon.setBounds(mOldBounds);
					icon = info.icon = new BitmapDrawable(thumb);
					info.filtered = true;
				}
			}

			final TextView textView = (TextView) convertView
					.findViewById(R.id.label);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null,
					null);
			textView.setText(info.title);

			return convertView;
		}
	}

	private class ApplicationLauncher implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView parent, View v, int position,
				long id) {

			System.out
					.println("-->SlideHome.java.ApplicationLauncher: onItemClick Called");

			ApplicationInfo app = (ApplicationInfo) parent
					.getItemAtPosition(position);
			startActivity(app.intent);
		}
	}

	private class ApplicationsIntentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			System.out
					.println("-->SlideHome.java.ApplicationsIntentReceiver: onReceive Called");

			updateApplicationList();
			loadApplications(false);
			bindApplications();
		}
	}

	private class ApplicationList {
		private ArrayList<ApplicationInfo> applications;
		private ArrayList<ResolveInfo> appsList;

		public ApplicationList() {

			System.out.println("-->ApplicationList: ApplicationList() Called");

			this.applications = new ArrayList<ApplicationInfo>();
			this.loadApplicationList();
			this.loadApplications();
		}

		public ApplicationList(ArrayList<ResolveInfo> appsList) {

			System.out
					.println("-->ApplicationList: ApplicationList(appsList) Called");

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

			System.out.println("-->ApplicationList: setAppsList Called");

			this.appsList = appsList;
		}

		private void loadApplicationList() {

			System.out
					.println("-->ApplicationList: loadApplicationList Called");

			PackageManager manager = getPackageManager();

			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

			this.appsList = (ArrayList<ResolveInfo>) manager
					.queryIntentActivities(mainIntent, 0);
			Collections.sort(this.appsList,
					new ResolveInfo.DisplayNameComparator(manager));
		}

		public void loadApplications() {

			System.out.println("-->ApplicationList: loadApplications Called");

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
					ApplicationInfo application = new ApplicationInfo();
					ResolveInfo info = this.appsList.get(i);

					application.title = info.loadLabel(manager);
					application
							.setActivity(
									new ComponentName(
											info.activityInfo.applicationInfo.packageName,
											info.activityInfo.name),
									Intent.FLAG_ACTIVITY_NEW_TASK
											| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					application.icon = info.activityInfo.loadIcon(manager);

					this.applications.add(application);
				}
			}
		}
	}
}