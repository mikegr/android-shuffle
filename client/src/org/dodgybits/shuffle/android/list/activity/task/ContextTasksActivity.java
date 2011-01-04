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

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.list.config.AbstractTaskListConfig;
import org.dodgybits.shuffle.android.list.config.ListConfig;
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
	
	@Override
    public void onCreate(Bundle icicle) {
		Uri contextUri = getIntent().getData();
		mContextId = Id.create(ContentUris.parseId(contextUri));
        super.onCreate(icicle);
	}

	@Override
	protected boolean showTaskContext() {
		return false;
	}

	@Override
    protected ListConfig<Task> createListConfig()
	{
        ListPreferenceSettings settings = new ListPreferenceSettings("context_tasks");
		return new AbstractTaskListConfig(createTaskQuery(), mTaskPersister, settings) {

		    public int getCurrentViewMenuId() {
		    	return 0;
		    }
		    
		    public String createTitle(ContextWrapper context)
		    {
		    	return context.getString(R.string.title_context_tasks, mContext.getName());
		    }
			
		};
	}


    @Override
    protected TaskSelector createTaskQuery() {
        List<Id> ids = Arrays.asList(new Id[] {mContextId});
        TaskSelector query = TaskSelector.newBuilder()
            .setContexts(new ArrayList<Id>(ids))
            .setDeleted(no)
            .setSortOrder(TaskProvider.Tasks.CREATED_DATE + " ASC")
            .build();
        return query;
    }

	@Override
	protected void onResume() {
		Log.d(cTag, "Fetching context " + mContextId);
		Cursor cursor = getContentResolver().query(ContextProvider.Contexts.CONTENT_URI, ContextProvider.Contexts.FULL_PROJECTION,
				ContextProvider.Contexts._ID + " = ? ", new String[] {String.valueOf(mContextId)}, null);
		if (cursor.moveToNext()) {
			mContext = mContextPersister.read(cursor);
		}
		cursor.close();
		
		super.onResume();
	}
    
    /**
     * Return the intent generated when a list item is clicked.
     * 
     * @param url type of data selected
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
