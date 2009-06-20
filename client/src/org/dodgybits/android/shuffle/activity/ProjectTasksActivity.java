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

package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.activity.config.AbstractTaskListConfig;
import org.dodgybits.android.shuffle.activity.config.ListConfig;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.util.MenuUtils;

import android.content.ContentUris;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;

public class ProjectTasksActivity extends AbstractTaskListActivity {

	private static final String cTag = "ProjectTasksActivity";
	private long mProjectId;
	private Project mProject;

	@Override
    public void onCreate(Bundle icicle) {
		Uri contextURI = getIntent().getData();
		mProjectId = ContentUris.parseId(contextURI);
        super.onCreate(icicle);
	}
	
	@Override
	protected Cursor createItemQuery() {
		Log.d(cTag, "Creating a cursor to find tasks for the given context");
		return managedQuery(getListConfig().getListContentUri(), 
				Shuffle.Tasks.cExpandedProjection,
				Shuffle.Tasks.PROJECT_ID + " = ?", 
				new String[] {String.valueOf(mProjectId)}, 
				Shuffle.Tasks.DUE_DATE + " ASC," + Shuffle.Tasks.DISPLAY_ORDER + " ASC");
	}

	@Override
	protected ListConfig<Task> createListConfig()
	{
		return new AbstractTaskListConfig() {

			public Uri getListContentUri() {
				return Shuffle.Tasks.CONTENT_URI;
			}

		    public int getCurrentViewMenuId() {
		    	return 0;
		    }
		    
		    public String createTitle(ContextWrapper context)
		    {
		    	return context.getString(R.string.title_project_tasks, mProject.name);
		    }
			
		};
	}
	
	@Override
	protected void onResume() {
		Log.d(cTag, "Fetching project " + mProjectId);
		Cursor cursor = getContentResolver().query(Shuffle.Projects.CONTENT_URI, Shuffle.Projects.cFullProjection,
				Shuffle.Projects._ID + " = ?", new String[] {String.valueOf(mProjectId)}, null);
		if (cursor.moveToNext()) {
			mProject = BindingUtils.readProject(cursor);
		}
		cursor.close();
		
		super.onResume();
	}

    /**
     * Return the intent generated when a list item is clicked.
     * 
     * @param url type of data selected
     */ 
    protected Intent getClickIntent(Uri uri) {
    	long taskId = ContentUris.parseId(uri);
    	Uri taskUri = ContentUris.appendId(Shuffle.Tasks.CONTENT_URI.buildUpon(), taskId).build();
    	return new Intent(Intent.ACTION_EDIT, taskUri);
    }
    
    /**
     * Add project id to intent extras so it can be pre-filled for the task.
     */
    protected Intent getInsertIntent() {
    	Intent intent = super.getInsertIntent();
    	Bundle extras = intent.getExtras();
    	if (extras == null) extras = new Bundle();
    	extras.putLong(Shuffle.Tasks.PROJECT_ID, mProject.id);
    	if (mProject.defaultContextId != null) {
    		Context context = BindingUtils.fetchContextById(this, mProject.defaultContextId);
    		if (context != null) {
        		extras.putLong(Shuffle.Tasks.CONTEXT_ID, context.id);
    		}
    	}
    	intent.putExtras(extras);
    	return intent;
    }

    @Override
	protected boolean showTaskProject() {
		return false;
	}
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, view, menuInfo);

    	AdapterView.AdapterContextMenuInfo info = 
    			(AdapterView.AdapterContextMenuInfo) menuInfo;
    	MenuUtils.addMoveMenuItems(menu, 
    			moveUpPermitted(info.position), moveDownPermitted(info.position));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(cTag, "bad menuInfo", e);
            return false;
        }

        switch (item.getItemId()) {
	        case MenuUtils.MOVE_UP_ID:
	            moveUp(info.position);
	            return true;
	        case MenuUtils.MOVE_DOWN_ID:
	            moveDown(info.position);
	            return true;
        }
        return super.onContextItemSelected(item);
    }	
    
    private boolean moveUpPermitted(int selection) {
    	return selection > 0;
    }
    
    private boolean moveDownPermitted(int selection) {
    	return selection < getItemCount() - 1;
    }
    
    protected final void moveUp(int selection) {
    	if (moveUpPermitted(selection)) {
    		Cursor cursor = (Cursor) getListAdapter().getItem(selection);
    		BindingUtils.swapTaskPositions(this, cursor, selection - 1, selection);
    	}
    }
    
    protected final void moveDown(int selection) {
    	if (moveDownPermitted(selection)) {
    		Cursor cursor = (Cursor) getListAdapter().getItem(selection);
    		BindingUtils.swapTaskPositions(this, cursor, selection, selection + 1);
    	}
    }

}
