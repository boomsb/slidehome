package com.slidehome.activities;
/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Represents a launchable application. An application is made of a name (or title), an intent
 * and an icon.
 *
 * @author Chris Spooner <cmspooner@gmail.com>
 * @author Bradley Booms <bradley.booms@gmail.com>
 */
public class ApplicationInfo {

    private static final String TAG = ApplicationInfo.class.getCanonicalName();

    /**
     * The application name.
     */
    public CharSequence title;

    /**
     * The intent used to start the application.
     */
    public Intent intent;

    /**
     * The application icon.
     */
    public Drawable icon;

    /**
     * When set to true, indicates that the icon has been resized.
     */
    public boolean filtered;
    
    public ApplicationInfo() {}
    
    public ApplicationInfo(Drawable icon) {
    	this.icon = icon;
    }

    public ApplicationInfo(ResolveInfo info, PackageManager manager) {
		title = info.loadLabel(manager);
        
		intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(
                info.activityInfo.applicationInfo.packageName,
                info.activityInfo.name));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		
        icon = info.activityInfo.loadIcon(manager);
	}

    @Override
    public boolean equals(Object o) {
        Log.d(TAG, "equals Called");

        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationInfo)) {
            return false;
        }

        ApplicationInfo that = (ApplicationInfo) o;
        return title.equals(that.title) &&
               intent.getComponent().getClassName().equals(that.intent.getComponent().getClassName());
    }

    @Override
    public int hashCode() {
        Log.d(TAG, "hashCode Called");

        final String name = intent.getComponent().getClassName();
        int result = (title != null ? title.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        
        return result;
    }
}
