package com.slidehome.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;


/**
 * 
 * @author Bradley Booms <bradley.booms@gmail.com>
 *
 */
public class AppTrayItem implements Parcelable {
	int page;
	int position;
	String packageName;
	String className;
	
	public AppTrayItem(Parcel in) {
		page = in.readInt();
		position = in.readInt();
		packageName = in.readString();
		className = in.readString();
	}
	
	public AppTrayItem(Cursor cr) { 
        int pageColumn = cr.getColumnIndex(AppTrayItems.PAGE); 
        int positionColumn = cr.getColumnIndex(AppTrayItems.POSITION);
        int packageNameColumn = cr.getColumnIndex(AppTrayItems.PACKAGE); 
        int classNameColumn = cr.getColumnIndex(AppTrayItems.CLASS); 

		page = cr.getInt(pageColumn);
		position = cr.getInt(positionColumn);
		packageName = cr.getString(packageNameColumn);
		className = cr.getString(classNameColumn);
	}
	
	public ContentValues getValues(){
		ContentValues values = new ContentValues();
		values.put(AppTrayItems.PAGE, page);
		values.put(AppTrayItems.POSITION, position);
		values.put(AppTrayItems.PACKAGE, packageName);
		values.put(AppTrayItems.CLASS, className);
		return values;
	}

	public int getPage() {
		return page;
	}

	public int getPosition() {
		return position;
	}
	
	public String getPackageName(){
		return packageName;
	}
	
	public String getClassName(){
		return className;
	}
	
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

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(page);
		dest.writeInt(position);
		dest.writeString(packageName);
		dest.writeString(className);
	}
	
    public static final Parcelable.Creator<AppTrayItem> CREATOR
    	= new Parcelable.Creator<AppTrayItem>() {
			@Override
			public AppTrayItem createFromParcel(Parcel arg0) {
				return new AppTrayItem(arg0);
			}

			@Override
			public AppTrayItem[] newArray(int arg0) {
				// TODO Auto-generated method stub
				return new AppTrayItem[arg0];
			}
    };
}
