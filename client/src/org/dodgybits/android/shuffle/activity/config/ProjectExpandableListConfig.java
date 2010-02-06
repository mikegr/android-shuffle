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

package org.dodgybits.android.shuffle.activity.config;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.persistence.provider.Shuffle;

import android.content.ContextWrapper;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;

public class ProjectExpandableListConfig implements ExpandableListConfig<Project, Task> {

	public Uri getChildContentUri() {
		return Shuffle.Tasks.CONTENT_URI;
	}

	public String getChildName(ContextWrapper context) {
		return context.getString(R.string.task_name);
	}

	public int getContentViewResId() {
		return R.layout.expandable_projects;
	}

	public int getCurrentViewMenuId() {
    	return MenuUtils.PROJECT_ID;
	}

	public Uri getGroupContentUri() {
		return Shuffle.Projects.CONTENT_URI;
	}

	public String getGroupIdColumnName() {
		return Shuffle.Tasks.PROJECT_ID;
	}

	public String getGroupName(ContextWrapper context) {
		return context.getString(R.string.project_name);
	}
	
	public Task readChild(Cursor cursor, Resources res) {
        return BindingUtils.readTask(cursor, res);
	}

	public Project readGroup(Cursor cursor, Resources res) {
        return BindingUtils.readProject(cursor);
	}
	
}
