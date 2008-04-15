package org.dodgybits.android.shuffle.activity;

import static org.dodgybits.android.shuffle.model.Preferences.CONTEXT_VIEW_KEY;
import static org.dodgybits.android.shuffle.model.Preferences.PROJECT_VIEW_KEY;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class PreferencesLayoutActivity extends Activity {
    private static final String cTag = "PreferencesLayoutActivity";
	
	private Spinner mProjectViewSpinner;
	private Spinner mContextViewSpinner;
	private boolean mSaveChanges;	

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

        setContentView(R.layout.preferences_layout);
                
        mProjectViewSpinner = (Spinner) findViewById(R.id.project_view);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.view_types,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mProjectViewSpinner.setAdapter(adapter);

        mContextViewSpinner = (Spinner) findViewById(R.id.context_view);
        adapter = ArrayAdapter.createFromResource(this, R.array.view_types,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mContextViewSpinner.setAdapter(adapter);
        mSaveChanges = true;
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
		mProjectViewSpinner.setSelection(Preferences.getProjectView(this));
		mContextViewSpinner.setSelection(Preferences.getContextView(this));
	}
	
	private void savePrefs() {
		Log.d(cTag, "Saving prefs");
		SharedPreferences.Editor ed = Preferences.getEditor(this);
		ed.putInt(PROJECT_VIEW_KEY, mProjectViewSpinner.getSelectedItemPosition());
		ed.putInt(CONTEXT_VIEW_KEY, mContextViewSpinner.getSelectedItemPosition());
		ed.commit();
	}
	
}
