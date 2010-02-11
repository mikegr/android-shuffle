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
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.persistence.provider.Shuffle;

import android.content.ContextWrapper;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;

public class ProjectListConfig implements DrilldownListConfig<Project> {

	public Uri getChildContentUri() {
		return Shuffle.Tasks.CONTENT_URI;
	}

	public String getChildName(ContextWrapper context) {
		return context.getString(R.string.task_name);
	}

	public String createTitle(ContextWrapper context) {
    	return context.getString(R.string.title_project);
	}

	public Uri getContentUri() {
		return Shuffle.Projects.CONTENT_URI;
	}

	public int getContentViewResId() {
		return R.layout.projects;
	}

	public int getCurrentViewMenuId() {
    	return MenuUtils.PROJECT_ID;
	}

	public String getItemName(ContextWrapper context) {
		return context.getString(R.string.project_name);
	}

	public Uri getListContentUri() {
		return Shuffle.Projects.CONTENT_URI;
	}

	public boolean isTaskList() {
    	return false;
	}

	public Project readItem(Cursor cursor, Resources res) {
        return BindingUtils.readProject(cursor);
	}

	public boolean supportsViewAction() {
		return false;
	}

}
