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
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.model.persistence.ProjectPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.database.Cursor;

public class ProjectListConfig implements DrilldownListConfig<Project> {
    private ProjectPersister mGroupPersister;
    private TaskPersister mChildPersister;
    
    public ProjectListConfig(ContentResolver resolver) {
        mGroupPersister = new ProjectPersister(resolver);
        mChildPersister = new TaskPersister(resolver);
    }
    
    @Override
	public String getChildName(ContextWrapper context) {
		return context.getString(R.string.task_name);
	}

    @Override
	public String createTitle(ContextWrapper context) {
    	return context.getString(R.string.title_project);
	}

    @Override
	public int getContentViewResId() {
		return R.layout.projects;
	}

    @Override
	public int getCurrentViewMenuId() {
    	return MenuUtils.PROJECT_ID;
	}

    @Override
	public String getItemName(ContextWrapper context) {
		return context.getString(R.string.project_name);
	}

    @Override
	public boolean isTaskList() {
    	return false;
	}

    @Override
	public boolean supportsViewAction() {
		return false;
	}
	
	@Override
	public EntityPersister<Project> getPersister() {
	    return mGroupPersister;
	}
	
	@Override
	public TaskPersister getChildPersister() {
	    return mChildPersister;
	}
	
	@Override
	public Cursor createQuery(Activity activity) {
	    return activity.managedQuery(
	            getPersister().getContentUri(), 
	            ProjectProvider.Projects.cFullProjection,
                null, null, 
                ProjectProvider.Projects.NAME + " ASC");
	}

}
