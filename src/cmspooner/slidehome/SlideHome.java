package cmspooner.slidehome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class SlideHome extends Activity {
	
	private static ArrayList<ApplicationInfo> mApplications;
	
	private ApplicationsStackLayout mApplicationsStack;
	private GridView mGrid;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        System.out.println("-->SlideHome.java: onCreate Called"); 

        setContentView(R.layout.home);
        
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        loadApplications(true);
        
//        System.out.println("------------------mApplications------------");
//        for (ApplicationInfo each: mApplications){
//        	System.out.println("     "+each.title);
//        }
//        System.out.println("-------------------------------------------");

        
        bindApplications();
        
//        //?
//        bindFavorites(true);
//        bindRecents();
//        bindButtons();

//        mGridEntry = AnimationUtils.loadAnimation(this, R.anim.grid_entry);
//        mGridExit = AnimationUtils.loadAnimation(this, R.anim.grid_exit);
        
    }
    
    /**
     * FROM ANDROID...No Changes...yet
     * 
     * Loads the list of installed applications in mApplications.
     */
    private void loadApplications(boolean isLaunching) {
    	
        System.out.println("-->SlideHome.java: loadApplications Called"); 

        if (isLaunching && mApplications != null) {
            return;
        }

        PackageManager manager = getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

        if (apps != null) {
            final int count = apps.size();

            if (mApplications == null) {
                mApplications = new ArrayList<ApplicationInfo>(count);
            }
            mApplications.clear();

            for (int i = 0; i < count; i++) {
                ApplicationInfo application = new ApplicationInfo();
                ResolveInfo info = apps.get(i);

                application.title = info.loadLabel(manager);
                application.setActivity(new ComponentName(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name),
                        Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                application.icon = info.activityInfo.loadIcon(manager);

                mApplications.add(application);
            }
        }
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
       
        mGrid.setAdapter(new ApplicationsAdapter(this, mApplications));
        mGrid.setSelection(0);

        if (mApplicationsStack == null) {
            mApplicationsStack = (ApplicationsStackLayout) findViewById(R.id.faves_and_recents);
        }
        
        System.out.println("-------bindApplications Testing----------");
        if (mGrid == null) System.out.println("mGrid did not initialize!"); 
        	else System.out.println("mGrid initialized just fine...i think!");
        if (mApplications == null) System.out.println("mApplications did not initialize!");
    		else System.out.println("mApplications initialized just fine...i think!");
        if (mApplicationsStack == null) System.out.println("mApplicationsStack did not initialize!");
    		else System.out.println("mApplicationsStack initialized just fine...i think!");
        System.out.println("-------------------------------------");

    }
    
    /**
     * FROM ANDROID...No Changes...yet
     * 
     * GridView adapter to show the list of all installed applications.
     */
    private class ApplicationsAdapter extends ArrayAdapter<ApplicationInfo> {
        private Rect mOldBounds = new Rect();

        public ApplicationsAdapter(Context context, ArrayList<ApplicationInfo> apps) {
            super(context, 0, apps);
            
            System.out.println("-->SlideHome.java.ApplicationsAdapter: ApplicationsAdapter Called"); 

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            System.out.println("-->SlideHome.java.ApplicationsAdapter: getView Called"); 

        	final ApplicationInfo info = mApplications.get(position);

            if (convertView == null) {
                final LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.application, parent, false);
            }

            Drawable icon = info.icon;

            if (!info.filtered) {
                //final Resources resources = getContext().getResources();
                int width = 42;//(int) resources.getDimension(android.R.dimen.app_icon_size);
                int height = 42;//(int) resources.getDimension(android.R.dimen.app_icon_size);

                final int iconWidth = icon.getIntrinsicWidth();
                final int iconHeight = icon.getIntrinsicHeight();

                if (icon instanceof PaintDrawable) {
                    PaintDrawable painter = (PaintDrawable) icon;
                    painter.setIntrinsicWidth(width);
                    painter.setIntrinsicHeight(height);
                }

                if (width > 0 && height > 0 && (width < iconWidth || height < iconHeight)) {
                    final float ratio = (float) iconWidth / iconHeight;

                    if (iconWidth > iconHeight) {
                        height = (int) (width / ratio);
                    } else if (iconHeight > iconWidth) {
                        width = (int) (height * ratio);
                    }

                    final Bitmap.Config c =
                            icon.getOpacity() != PixelFormat.OPAQUE ?
                                Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
                    final Bitmap thumb = Bitmap.createBitmap(width, height, c);
                    final Canvas canvas = new Canvas(thumb);
                    canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, 0));
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

            final TextView textView = (TextView) convertView.findViewById(R.id.label);
            textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
            textView.setText(info.title);

            return convertView;
        }
    }
}