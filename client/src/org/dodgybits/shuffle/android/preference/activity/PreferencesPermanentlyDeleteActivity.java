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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import org.dodgybits.shuffle.android.core.model.persistence.InitialDataGenerator;
import org.dodgybits.shuffle.android.core.model.persistence.ProjectPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.view.AlertUtils;
import roboguice.util.Ln;

public class PreferencesPermanentlyDeleteActivity extends PreferencesDeleteActivity {

    @Inject private TaskPersister mTaskPersister;
    @Inject private ProjectPersister mProjectPersister;
    @Inject private ContextPersister mContextPersister;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        setProgressBarIndeterminate(true);
        
        mDeleteButton.setText(R.string.menu_delete);
        mText.setText(R.string.warning_empty_trash);
    }
    
	@Override
	protected void onDelete() {
        int taskCount = mTaskPersister.emptyTrash();
        int projectCount = mProjectPersister.emptyTrash();
        int contextCount = mContextPersister.emptyTrash();

        Ln.i("Permanently deleted %s tasks, %s contexts and %s projects", taskCount, contextCount, projectCount);
        CharSequence message = getString(R.string.toast_empty_trash, new Object[] {taskCount, contextCount, projectCount});
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    	finish();
	}

}
