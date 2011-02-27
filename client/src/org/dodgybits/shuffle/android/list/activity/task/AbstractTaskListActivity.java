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
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.list.activity.AbstractListActivity;
import org.dodgybits.shuffle.android.list.activity.ListPreferenceActivity;
import org.dodgybits.shuffle.android.list.config.TaskListConfig;
import org.dodgybits.shuffle.android.list.view.ButtonBar;
import org.dodgybits.shuffle.android.list.view.SwipeListItemListener;
import org.dodgybits.shuffle.android.list.view.SwipeListItemWrapper;
import org.dodgybits.shuffle.android.list.view.TaskView;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;

import roboguice.event.Observes;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.internal.Nullable;

public abstract class AbstractTaskListActivity extends AbstractListActivity<Task> 
	implements SwipeListItemListener {

	private static final String cTag = "AbstractTaskListActivity";

    @Inject Provider<TaskView> mTaskViewProvider;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		// register self as swipe listener
		SwipeListItemWrapper wrapper = (SwipeListItemWrapper) findViewById(R.id.swipe_wrapper);
		wrapper.setSwipeListItemListener(this);
	}

    @Override
    protected void OnCreateEntityContextMenu(ContextMenu menu, int position, Task task) {
		// ... add complete command.
    	MenuUtils.addCompleteMenuItem(menu, task.isComplete());
    }

    @Override
    protected boolean onContextEntitySelected(MenuItem item, int position, Task task) {
        switch (item.getItemId()) {
	        case MenuUtils.COMPLETE_ID:
	            toggleComplete(task);
	            return true;
        }
        return super.onContextEntitySelected(item, position, task);
    }	
    
    protected TaskPersister getTaskPersister() {
        return getTaskListConfig().getTaskPersister();
    }

    @Override
    protected Intent getClickIntent(Uri uri) {
    	return new Intent(Intent.ACTION_EDIT, uri);
    }
    	
	@Override
	protected ListAdapter createListAdapter(Cursor cursor) {
		ListAdapter adapter = new SimpleCursorAdapter(this,
						R.layout.list_task_view, cursor,
						new String[] { TaskProvider.Tasks.DESCRIPTION },
						new int[] { R.id.description }) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Log.d(cTag, "getView position=" + position + ". Old view=" + convertView);
				Cursor cursor = (Cursor)getItem(position);
				Task task = getListConfig().getPersister().read(cursor);
				TaskView taskView;
				if (convertView instanceof TaskView) {
					taskView = (TaskView) convertView;
				} else {
                    taskView = mTaskViewProvider.get();
				}
				taskView.setShowContext(getTaskListConfig().showTaskContext());
				taskView.setShowProject(getTaskListConfig().showTaskProject());
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
    	if (position >= 0 && position < getItemCount())
    	{
	    	Cursor c = (Cursor) getListAdapter().getItem(position);
            Task task = getTaskPersister().read(c);
	    	toggleComplete(task);
    	}
    }

    protected final void toggleComplete(Task task) {
    	if (task != null)
    	{
	    	getTaskPersister().updateCompleteFlag(task.getLocalId(), !task.isComplete());
    	}
    }

    protected TaskListConfig getTaskListConfig() {
        return (TaskListConfig)getListConfig();
    }

    @Override
    protected void onOther( @Observes ButtonBar.OtherButtonClickEvent event ) {
        int deletedTasks = getTaskPersister().deleteCompletedTasks();
		CharSequence message = getString(R.string.clean_task_message, new Object[] {deletedTasks});
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
    