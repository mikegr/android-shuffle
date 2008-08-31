package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Preferences;
import org.dodgybits.android.shuffle.util.MenuUtils;
import org.dodgybits.android.shuffle.util.ModelUtils;

import android.app.Activity;
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

public class WelcomeActivity extends Activity {
    private static final String cTag = "WelcomeActivity";
	
    private Button mSampleDataButton;
    private Button mCleanSlateButton;
    private Handler mHandler;
    
    @Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Log.d(cTag, "onCreate");
		
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setProgressBarIndeterminate(true);
        setContentView(R.layout.welcome);
        
        mSampleDataButton = (Button) findViewById(R.id.sample_data_button);
        mSampleDataButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	performCreateSampleData();
            }
        });
        mCleanSlateButton = (Button) findViewById(R.id.clean_slate_button);
        mCleanSlateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	performCleanSlate();
            }
        });
    	mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	updateFirstTimePref(false);
                setProgressBarVisibility(false);
                startActivity(new Intent(WelcomeActivity.this, TopLevelActivity.class));
            	finish();
            }
        };
	}
    
    private void performCreateSampleData() {
    	Log.i(cTag, "Adding sample data");
        setProgressBarVisibility(true);
    	new Thread() {
    		public void run() {
    	    	ModelUtils.createSampleData(WelcomeActivity.this, mHandler);
    		}
    	}.start();
    }
        
    private void performCleanSlate() {
    	Log.i(cTag, "Cleaning the slate");
        setProgressBarVisibility(true);
    	new Thread() {
    		public void run() {
    	    	ModelUtils.cleanSlate(WelcomeActivity.this, mHandler);
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
        MenuUtils.addPrefsHelpMenuItems(menu);

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (MenuUtils.checkCommonItemsSelected(item, this, MenuUtils.INBOX_ID)) {
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
