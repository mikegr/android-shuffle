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

import android.content.ContentResolver;
import android.content.ContextWrapper;

public abstract class AbstractTaskListConfig implements TaskListConfig {

    private TaskPersister mPersister;
    
    public AbstractTaskListConfig(ContentResolver resolver) {
        mPersister = new TaskPersister(resolver);
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
	
}
