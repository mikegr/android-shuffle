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
import org.dodgybits.shuffle.android.list.view.SwipeListItemListener;
import org.dodgybits.shuffle.android.list.view.SwipeListItemWrapper;
import org.dodgybits.shuffle.android.list.view.TaskView;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;

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
	implements SwipeListItemListener, View.OnClickListener {

	private static final String cTag = "AbstractTaskListActivity";
	private static final int cFilterConfig = 676;

    @Inject Provider<TaskView> mTaskViewProvider;
	
	@InjectView(R.id.add_task_button) @Nullable Button mAddTaskButton;
	protected Button mOtherButton;
	protected ImageButton mFilterButton;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		// register self as swipe listener
		SwipeListItemWrapper wrapper = (SwipeListItemWrapper) findViewById(R.id.swipe_wrapper);
		wrapper.setSwipeListItemListener(this);
		
		// lookup and setup icons in icon_bar (if present)
		if (mAddTaskButton != null) {
			Drawable addIcon = getResources().getDrawable(android.R.drawable.ic_menu_add);
			addIcon.setBounds(0, 0, 24, 24);
			mAddTaskButton.setCompoundDrawables(addIcon, null, null, null);
			mAddTaskButton.setOnClickListener(this);
	
			mOtherButton = (Button) findViewById(R.id.other_button);
			mOtherButton.setText(R.string.title_delete_completed_preference);
			Drawable deleteIcon = getResources().getDrawable(android.R.drawable.ic_menu_delete);
			deleteIcon.setBounds(0, 0, 24, 24);
			mOtherButton.setCompoundDrawables(deleteIcon, null, null, null);
			mOtherButton.setVisibility(View.VISIBLE);
			mOtherButton.setOnClickListener(this);

            mFilterButton = (ImageButton) findViewById(R.id.filter_button);
            mFilterButton.setOnClickListener(this);
		}
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
    
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.add_task_button:
				startActivityForResult(getInsertIntent(), NEW_ITEM);
                break;

            case R.id.other_button:
            	onOtherButtonClicked();
                break;

            case R.id.filter_button:
                onFilterButtonClicked();
                break;
        }
    }
    
    protected TaskPersister getTaskPersister() {
        return ((TaskListConfig)getListConfig()).getTaskPersister();
    }
    
    protected void onOtherButtonClicked() {
        int deletedTasks = getTaskPersister().deleteCompletedTasks();
		CharSequence message = getString(R.string.clean_task_message, new Object[] {deletedTasks});
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void onFilterButtonClicked() {
        Intent intent = new Intent(this, ListPreferenceActivity.class);
        ((TaskListConfig)getListConfig()).getListPreferenceSettings().addToIntent(intent);
        startActivityForResult(intent, cFilterConfig);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == cFilterConfig) {
            Log.d(cTag, "Got result " + resultCode);
            updateCursor();
        }

        super.onActivityResult(requestCode, resultCode, data);
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
    	if (position >= 0 && position < getItemCount())
    	{
	    	Cursor c = (Cursor) getListAdapter().getItem(position);
	    	getTaskPersister().toggleTaskComplete(c);
    	}
    }

    protected TaskListConfig getTaskListConfig() {
        return (TaskListConfig)getListConfig();
    }

    protected void updateCursor() {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter)getListAdapter();
        Cursor oldCursor = adapter.getCursor();
        if (oldCursor != null) {
            // changeCursor always closes the cursor,
            // so need to stop managing the old one first
            stopManagingCursor(oldCursor);
        }

        Cursor cursor = getListConfig().createQuery(this);
        adapter.changeCursor(cursor);
        setTitle(getListConfig().createTitle(this));
    }

	protected boolean showTaskContext() {
		return true;
	}
	
	protected boolean showTaskProject() {
		return true;
	}
	
	
	
}
    