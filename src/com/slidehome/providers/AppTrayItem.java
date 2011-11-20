package com.slidehome.providers;

import android.net.Uri;
import android.provider.BaseColumns;


/**
 * 
 * @author Bradley Booms <bradley.booms@gmail.com>
 *
 */
public class AppTrayItem {
	public AppTrayItem() {}
	
	public static class AppTrayItems implements BaseColumns{
		private AppTrayItems() {};
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + SlideHomeProvider.URI_BASE + "/apptray");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.slidehome.apptray";
		
		public static final String APP_TRAY_ITEM_ID = "_id";
		
		public static final String PAGE = "page";
		
		public static final String POSITION = "position";
		
		public static final String PACKAGE = "package";
		
		public static final String CLASS = "class";
		
		public static final String[] PROJECTION = new String[] {
            _ID,
            PAGE,
            POSITION,
            PACKAGE,
            CLASS
         };

	}
}
