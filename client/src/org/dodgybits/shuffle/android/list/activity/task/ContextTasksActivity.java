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

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import org.dodgybits.shuffle.android.list.config.AbstractTaskListConfig;
import org.dodgybits.shuffle.android.list.config.ListConfig;
import org.dodgybits.shuffle.android.persistence.provider.Shuffle;

import android.content.ContentUris;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ContextTasksActivity extends AbstractTaskListActivity {

	private static final String cTag = "ContextTasksActivity";
	private long mContextId;
	private Context mContext;

	@Override
    public void onCreate(Bundle icicle) {
		Uri contextUri = getIntent().getData();
		mContextId = ContentUris.parseId(contextUri);
        super.onCreate(icicle);

	}
	
    @Override
	protected Cursor createItemQuery() {
		Log.d(cTag, "Creating a cursor to find tasks for the given context");
		return managedQuery(
		        getListConfig().getPersister().getContentUri(), 
				Shuffle.Tasks.cFullProjection,
				Shuffle.Tasks.CONTEXT_ID + " = ?", 
				new String[] {String.valueOf(mContextId)}, 
				Shuffle.Tasks.CREATED_DATE + " ASC");
	}

	@Override
	protected boolean showTaskContext() {
		return false;
	}

	@Override
	protected ListConfig<Task> createListConfig()
	{
		return new AbstractTaskListConfig(getContentResolver()) {

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
	protected void onResume() {
		Log.d(cTag, "Fetching context " + mContextId);
		Cursor cursor = getContentResolver().query(Shuffle.Contexts.CONTENT_URI, Shuffle.Contexts.cFullProjection,
				Shuffle.Contexts._ID + " = ?", new String[] {String.valueOf(mContextId)}, null);
		if (cursor.moveToNext()) {
		    ContextPersister persister = new ContextPersister(getContentResolver());
			mContext = persister.read(cursor);
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
    	Uri taskURI = ContentUris.withAppendedId(Shuffle.Tasks.CONTENT_URI, taskId);
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
    	extras.putLong(Shuffle.Tasks.CONTEXT_ID, mContext.getLocalId().getId());
    	intent.putExtras(extras);
    	return intent;
    }    

}
