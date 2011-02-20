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

package org.dodgybits.shuffle.android.list.activity.task;

import static org.dodgybits.shuffle.android.core.model.persistence.selector.Flag.no;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.source.util.TaskListener;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.list.annotation.ContextTasks;
import org.dodgybits.shuffle.android.list.config.AbstractTaskListConfig;
import org.dodgybits.shuffle.android.list.config.ContextTasksListConfig;
import org.dodgybits.shuffle.android.list.config.ListConfig;
import org.dodgybits.shuffle.android.list.config.TaskListConfig;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;

import android.content.ContentUris;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.inject.Inject;

public class ContextTasksActivity extends AbstractTaskListActivity {

	private static final String cTag = "ContextTasksActivity";
	private Id mContextId;
	private Context mContext;
	
    @Inject private EntityPersister<Context> mContextPersister;
    @Inject private TaskPersister mTaskPersister;

    @Inject @ContextTasks
    private ContextTasksListConfig mTaskListConfig;

	@Override
    public void onCreate(Bundle icicle) {
		Uri contextUri = getIntent().getData();
		mContextId = Id.create(ContentUris.parseId(contextUri));
        super.onCreate(icicle);
	}

	@Override
    protected ListConfig<Task> createListConfig()
	{
        mTaskListConfig.setContextId(mContextId);
        return mTaskListConfig;
	}


	@Override
	protected void onResume() {
		Log.d(cTag, "Fetching context " + mContextId);
		Cursor cursor = getContentResolver().query(ContextProvider.Contexts.CONTENT_URI, ContextProvider.Contexts.FULL_PROJECTION,
				ContextProvider.Contexts._ID + " = ? ", new String[] {String.valueOf(mContextId)}, null);
		if (cursor.moveToNext()) {
			mContext = mContextPersister.read(cursor);
            mTaskListConfig.setContext(mContext);
		}
		cursor.close();
		
		super.onResume();
	}
    
    /**
     * Return the intent generated when a list item is clicked.
     * 
     * @param uri type of data selected
     */ 
    @Override
    protected Intent getClickIntent(Uri uri) {
    	long taskId = ContentUris.parseId(uri);
    	Uri taskURI = ContentUris.withAppendedId(TaskProvider.Tasks.CONTENT_URI, taskId);
    	return new Intent(Intent.ACTION_EDIT, taskURI);
    }

    /**
     * Add context name to intent extras so it can be pre-filled for the task.
     */
    @Override
    protected Intent getInsertIntent() {
    	Intent intent = super.getInsertIntent();
    	Bundle extras = intent.getExtras();
    	if (extras == null) extras = new Bundle();
    	extras.putLong(TaskProvider.Tasks.CONTEXT_ID, mContext.getLocalId().getId());
    	intent.putExtras(extras);
    	return intent;
    }    

}
