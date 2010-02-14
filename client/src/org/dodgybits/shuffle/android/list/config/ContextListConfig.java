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
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.view.MenuUtils;

import android.content.ContentResolver;
import android.content.ContextWrapper;

public class ContextListConfig implements DrilldownListConfig<Context> {

    private ContextPersister mGroupPersister;
    private TaskPersister mChildPersister;
    
    public ContextListConfig(ContentResolver resolver) {
        mGroupPersister = new ContextPersister(resolver);
        mChildPersister = new TaskPersister(resolver);
    }
    
    @Override
	public String createTitle(ContextWrapper context) {
    	return context.getString(R.string.title_context);
	}

    @Override
	public int getContentViewResId() {
		return R.layout.contexts;
	}

    @Override
	public int getCurrentViewMenuId() {
    	return MenuUtils.CONTEXT_ID;
	}

    @Override
	public String getItemName(ContextWrapper context) {
		return context.getString(R.string.context_name);
	}

    @Override
	public boolean isTaskList() {
		return false;
	}

	@Override
	public EntityPersister<Context> getPersister() {
	    return mGroupPersister;
	}
	
	@Override
	public TaskPersister getChildPersister() {
	    return mChildPersister;
	}
	
    @Override
	public boolean supportsViewAction() {
		return false;
	}
	
    @Override
	public String getChildName(ContextWrapper context) {
		return context.getString(R.string.task_name);
	}		

}
