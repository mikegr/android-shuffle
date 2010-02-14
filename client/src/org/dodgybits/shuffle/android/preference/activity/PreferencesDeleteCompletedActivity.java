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

package org.dodgybits.shuffle.android.preference.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class PreferencesDeleteCompletedActivity extends PreferencesDeleteActivity {
    private static final String cTag = "PreferencesDeleteCompletedActivity";

    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        
        mDeleteButton.setText(R.string.delete_completed_button_title);
        mText.setText(R.string.delete_completed_warning);
    }
    
	@Override
	protected void onDelete() {
	    TaskPersister persister = new TaskPersister(getContentResolver());
    	int deletedTasks = persister.deleteCompletedTasks();
		CharSequence message = getString(R.string.clean_task_message, new Object[] {deletedTasks});
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		finish();
	}

}
