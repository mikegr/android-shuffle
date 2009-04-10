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
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.util.MenuUtils;
import org.dodgybits.android.shuffle.view.SwipeListItemListener;
import org.dodgybits.android.shuffle.view.SwipeListItemWrapper;
import org.dodgybits.android.shuffle.view.TaskView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public abstract class AbstractTaskListActivity extends AbstractListActivity<Task> 
	implements SwipeListItemListener {

	private static final String cTag = "AbstractTaskListActivity";
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		// register self as swipe listener
		SwipeListItemWrapper wrapper = (SwipeListItemWrapper) findViewById(R.id.swipe_wrapper);
		wrapper.setSwipeListItemListener(this);
	}
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, view, menuInfo);

		// ... add complete command.
    	MenuUtils.addCompleteMenuItem(menu);
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
	        case MenuUtils.COMPLETE_ID:
	            toggleComplete(info.position);
	            return true;
        }
        return super.onContextItemSelected(item);
    }	
    
	@Override
    protected Intent getClickIntent(Uri uri) {
    	return new Intent(Intent.ACTION_EDIT, uri);
    }
    
	@Override
	protected Cursor createItemQuery() {
		return managedQuery(getListConfig().getListContentUri(), 
				Shuffle.Tasks.cExpandedProjection, null, null, null);
	}
	
	@Override
	protected ListAdapter createListAdapter(Cursor cursor) {
		ListAdapter adapter = new SimpleCursorAdapter(this,
						R.layout.list_task_view, cursor,
						new String[] { Shuffle.Tasks.DESCRIPTION },
						new int[] { R.id.description }) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Log.d(cTag, "getView position=" + position + ". Old view=" + convertView);
				Cursor cursor = (Cursor)getItem(position);
				Task task = getListConfig().readItem(cursor, getResources());
				TaskView taskView;
				if (convertView instanceof TaskView) {
					taskView = (TaskView) convertView;
				} else {
					taskView = new TaskView(parent.getContext());
				}
				taskView.setShowContext(showTaskContext());
				taskView.setShowProject(showTaskProject());
				taskView.updateView(task);
				return taskView;
			}

		};
		return adapter;
	}
	
	public void onListItemSwiped(int position) {
		toggleComplete(position);
	}

	protected final void toggleComplete() {
    	toggleComplete(getSelectedItemPosition());
    }

    protected final void toggleComplete(int position) {
    	long id = getListAdapter().getItemId(position);
    	Cursor c = (Cursor) getListAdapter().getItem(position);
        BindingUtils.toggleTaskComplete(this, c, getListConfig().getListContentUri(), id);
    }
	
	protected boolean showTaskContext() {
		return true;
	}
	
	protected boolean showTaskProject() {
		return true;
	}
	
	
	
}
    