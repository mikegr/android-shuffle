package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.util.ModelUtils;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class PreferencesDeleteAllActivity extends PreferencesDeleteActivity {
    private static final String cTag = "PreferencesDeleteAllActivity";

    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        
        setProgressBarIndeterminate(true);
        
        mDeleteButton.setText(R.string.clean_slate_button_title);
        mText.setText(R.string.clean_slate_warning);
    }
    
	@Override
	protected void onDelete() {
    	Log.i(cTag, "Cleaning the slate");
    	ModelUtils.cleanSlate(this, null);
        Toast.makeText(this, R.string.clean_slate_message, Toast.LENGTH_SHORT).show();
    	finish();
	}
		
}
