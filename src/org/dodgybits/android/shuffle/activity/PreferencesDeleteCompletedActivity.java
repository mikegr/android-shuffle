package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.util.ModelUtils;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class PreferencesDeleteCompletedActivity extends PreferencesDeleteActivity {
    private static final String cTag = "PreferencesDeleteCompletedActivity";

    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        
        mDeleteButton.setText(R.string.ok_button_title);
        mText.setText(R.string.delete_completed_warning);
    }
    
	@Override
	protected void onDelete() {
    	int deletedTasks = ModelUtils.deleteCompletedTasks(this);
		CharSequence message = getString(R.string.clean_task_message, new Object[] {deletedTasks});
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		finish();
	}

}
