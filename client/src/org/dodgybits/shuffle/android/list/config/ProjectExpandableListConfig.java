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

import android.content.ContextWrapper;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.model.persistence.ProjectPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.EntitySelector;
import org.dodgybits.shuffle.android.core.model.persistence.selector.ProjectSelector;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.list.annotation.ExpandableProjects;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;

public class ProjectExpandableListConfig implements ExpandableListConfig<Project> {
    private ProjectPersister mGroupPersister;
    private TaskPersister mChildPersister;
    private ListPreferenceSettings mSettings;
    private TaskSelector mTaskSelector;
    private ProjectSelector mProjectSelector;

    @Inject
    public ProjectExpandableListConfig(ProjectPersister projectPersister,
                                       TaskPersister taskPersister,
                                       @ExpandableProjects ListPreferenceSettings settings) {
        mGroupPersister = projectPersister;
        mChildPersister = taskPersister;
        mSettings = settings;
        mTaskSelector = TaskSelector.newBuilder().
                setSortOrder(TaskProvider.Tasks.DISPLAY_ORDER + " ASC").build();
        mProjectSelector = ProjectSelector.newBuilder().
                setSortOrder(ProjectProvider.Projects.NAME + " ASC").build();
    }

    @Override
    public EntitySelector getGroupSelector() {
        return mProjectSelector;
    }

    @Override
    public TaskSelector getChildSelector() {
        return mTaskSelector;
    }

    @Override
	public String getChildName(ContextWrapper context) {
		return context.getString(R.string.task_name);
	}

    @Override
	public int getContentViewResId() {
		return R.layout.expandable_projects;
	}

    @Override
	public int getCurrentViewMenuId() {
    	return MenuUtils.PROJECT_ID;
	}

    @Override
	public String getGroupIdColumnName() {
		return TaskProvider.Tasks.PROJECT_ID;
	}

    @Override
	public String getGroupName(ContextWrapper context) {
		return context.getString(R.string.project_name);
	}
	
    @Override
    public TaskPersister getChildPersister() {
        return mChildPersister;
    }
    
    @Override
    public EntityPersister<Project> getGroupPersister() {
        return mGroupPersister;
    }

    @Override
    public ListPreferenceSettings getListPreferenceSettings() {
        return mSettings;
    }

}
