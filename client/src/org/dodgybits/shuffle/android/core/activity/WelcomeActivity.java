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

package org.dodgybits.shuffle.android.core.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.FlurryEnabledActivity;
import org.dodgybits.shuffle.android.core.model.persistence.InitialDataGenerator;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.preference.model.Preferences;

import roboguice.inject.InjectView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.inject.Inject;

public class WelcomeActivity extends FlurryEnabledActivity {
    private static final String cTag = "WelcomeActivity";
	
    @InjectView(R.id.sample_data_button) Button mSampleDataButton;
    @InjectView(R.id.clean_slate_button) Button mCleanSlateButton;
    @Inject InitialDataGenerator mGenerator;
    
    private Handler mHandler;
    
    @Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Log.d(cTag, "onCreate");
		
		
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.welcome);
        
        mSampleDataButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	disableButtons();
            	startProgressAnimation();
            	performCreateSampleData();
            }
        });
        mCleanSlateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	disableButtons();
            	startProgressAnimation();
            	performCleanSlate();
            }
        });
    	mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	updateFirstTimePref(false);
            	
                // Stop the spinner
                getWindow().setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS,
                        Window.PROGRESS_VISIBILITY_OFF);
                
                startActivity(new Intent(WelcomeActivity.this, TopLevelActivity.class));
            	finish();
            }
        };
	}
    
    private void disableButtons() {
    	mCleanSlateButton.setEnabled(false);
    	mSampleDataButton.setEnabled(false);
    }
    
    private void startProgressAnimation() {
        // Start the spinner
        getWindow().setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS,
                Window.PROGRESS_VISIBILITY_ON);
    }
    
    private void performCreateSampleData() {
    	Log.i(cTag, "Adding sample data");
        setProgressBarVisibility(true);
    	new Thread() {
    		public void run() {
    		    mGenerator.createSampleData(mHandler);
    		}
    	}.start();
    }
        
    private void performCleanSlate() {
    	Log.i(cTag, "Cleaning the slate");
        setProgressBarVisibility(true);
    	new Thread() {
    		public void run() {
    		    mGenerator.cleanSlate(mHandler);
    		}
    	}.start();
    }
    
    private void updateFirstTimePref(boolean value) {
		SharedPreferences.Editor editor = Preferences.getEditor(this);
		editor.putBoolean(Preferences.FIRST_TIME, value);
		editor.commit();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuUtils.addPrefsHelpMenuItems(this, menu);

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuUtils.checkCommonItemsSelected(item, this, MenuUtils.INBOX_ID) || super.onOptionsItemSelected(item);
    }



}
