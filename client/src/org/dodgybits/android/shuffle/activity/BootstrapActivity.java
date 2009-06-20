/*
 * Copyright (C) 2009 Android Shuffle Open Source Project
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

package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.model.Preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BootstrapActivity extends Activity {
	private static final String cTag = "BootstrapActivity";

	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

        Class<? extends Activity> activityClass = null;
		boolean firstTime = Preferences.isFirstTime(this);
		if (firstTime) {
			Log.i(cTag, "First time using Shuffle. Show intro screen");
			activityClass = WelcomeActivity.class;
		} else {
        	activityClass = TopLevelActivity.class;
		}
        
        // start the cleaning service
//		TaskCleaner.getInstance(this).schedule();

        startActivity(new Intent(this, activityClass));

        // dump layouts
    	//Test.dumpResources(this, "com.google.android.contacts", R.layout.activity_list_item);
        
        finish();
	}
	
}
