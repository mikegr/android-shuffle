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

package org.dodgybits.shuffle.android.list.config;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.EntitySelector;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;

import android.app.Activity;
import android.content.ContextWrapper;
import android.database.Cursor;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;

public abstract class AbstractTaskListConfig implements TaskListConfig {

    private TaskPersister mPersister;
    private TaskSelector mTaskSelector;
    private ListPreferenceSettings mSettings;

    public AbstractTaskListConfig(TaskSelector selector, TaskPersister persister, ListPreferenceSettings settings) {
        mTaskSelector = selector;
        mPersister = persister;
        mSettings = settings;
    }
    
    @Override
	public int getContentViewResId() {
		return R.layout.task_list;
	}
	
    @Override
	public String getItemName(ContextWrapper context) {
		return context.getString(R.string.task_name);
	}
	
	@Override
	public EntityPersister<Task> getPersister() {
	    return mPersister;
	}

    public TaskPersister getTaskPersister() {
        return mPersister;
    }
	
    @Override
    public boolean supportsViewAction() {
		return true;
	}
    
    @Override
    public boolean isTaskList() {
    	return true;
    }
    
    @Override
    public TaskSelector getTaskSelector() {
        return mTaskSelector;
    }

    @Override
    public EntitySelector getEntitySelector() {
        return mTaskSelector;
    }
    
    @Override
    public void setTaskSelector(TaskSelector query) {
        mTaskSelector = query;
    }
    
    @Override
    public Cursor createQuery(Activity activity) {
        EntitySelector selector = getEntitySelector().builderFrom().
                applyListPreferences(activity, getListPreferenceSettings()).build();

        return activity.managedQuery(
                selector.getContentUri(),
                TaskProvider.Tasks.FULL_PROJECTION, 
                selector.getSelection(activity),
                selector.getSelectionArgs(),
                selector.getSortOrder());
    }

    @Override
    public ListPreferenceSettings getListPreferenceSettings() {
        return mSettings;
    }

    @Override
    public boolean showTaskContext() {
        return true;
    }

    @Override
    public boolean showTaskProject() {
        return true;
    }


}
