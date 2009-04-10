package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.model.Preferences;
import org.dodgybits.android.shuffle.service.TaskCleaner;

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
