package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Preferences;
import org.dodgybits.android.shuffle.util.AlertUtils;
import org.dodgybits.android.shuffle.util.ModelUtils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class PreferencesCleanActivity extends Activity {
    private static final String cTag = "PreferencesCleanActivity";

	private Spinner mDeleteCompletedSpinner;
	private Button mDeleteCompletedNowButton;
	private Button mCleanSlateButton;
	private boolean mSaveChanges;
    private Handler mHandler;
	

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

        requestWindowFeature(Window.FEATURE_PROGRESS);
        setProgressBarIndeterminate(true);
        setContentView(R.layout.preferences_clean);
        
        mDeleteCompletedSpinner = (Spinner) findViewById(R.id.delete_completed);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.clean,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDeleteCompletedSpinner.setAdapter(adapter);

        mDeleteCompletedNowButton = (Button) findViewById(R.id.delete_completed_button);
        mDeleteCompletedNowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	int deletedRows = ModelUtils.deleteCompletedTasks(PreferencesCleanActivity.this);
            	AlertUtils.showDeletedTasksMessage(PreferencesCleanActivity.this, deletedRows);
            }
        });
        
        
        mCleanSlateButton = (Button) findViewById(R.id.clean_slate_button);
        mCleanSlateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		OnClickListener buttonListener = new OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) {
        				if (which == DialogInterface.BUTTON2) {
        					performCleanSlate();
        				} else {
        					Log.d(cTag, "Hit Cancel button. Do nothing.");
        				}
        			}
        		};
            	AlertUtils.showCleanSlateWarning(PreferencesCleanActivity.this, buttonListener);
            }
        });
    	mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                setProgressBarVisibility(false);
            }
        };

        mSaveChanges = true;
	}
	
    private void performCleanSlate() {
    	Log.i(cTag, "Cleaning the slate");
        setProgressBarVisibility(true);
    	new Thread() {
    		public void run() {
    	    	ModelUtils.cleanSlate(PreferencesCleanActivity.this, mHandler);
    		}
    	}.start();
    }

    @Override
	protected void onResume() {
        Log.d(cTag, "onResume+");
		super.onResume();
		
        readPrefs();
	}

	@Override
    protected void onPause() {
        Log.d(cTag, "onPause+");
        super.onPause();

        if (mSaveChanges) {
        	savePrefs();
        }
    }
    
	private void readPrefs() {
		Log.d(cTag, "Settings prefs controls");
//		mDeleteCompletedSpinner.setSelection(Preferences.getDeleteCompletedPeriod(this));
	}
	
	private void savePrefs() {
		Log.d(cTag, "Saving prefs");
		SharedPreferences.Editor ed = Preferences.getEditor(this);
		ed.putInt(Preferences.DELETE_COMPLETED_PERIOD_KEY, mDeleteCompletedSpinner.getSelectedItemPosition());
		ed.commit();
	}
	
}
