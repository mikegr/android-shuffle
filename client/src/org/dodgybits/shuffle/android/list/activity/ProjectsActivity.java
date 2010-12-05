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

package org.dodgybits.shuffle.android.list.activity;

import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.list.activity.task.ProjectTasksActivity;
import org.dodgybits.shuffle.android.list.config.ListConfig;
import org.dodgybits.shuffle.android.list.config.ProjectListConfig;
import org.dodgybits.shuffle.android.list.view.ProjectView;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import com.google.inject.Inject;

/**
 * Display list of projects with task children.
 */
public class ProjectsActivity extends AbstractDrilldownListActivity<Project> {

	@SuppressWarnings("unused")
    private static final String cTag = "ProjectsActivity";

    @Inject ProjectListConfig mListConfig;
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Cursor cursor = getContentResolver().query(
				ProjectProvider.Projects.PROJECT_TASKS_CONTENT_URI, 
				ProjectProvider.Projects.FULL_TASK_PROJECTION, null, null, null);
        mTaskCountArray = getDrilldownListConfig().getChildPersister().readCountArray(cursor);
		cursor.close();
	}

	@Override
	protected ListConfig<Project> createListConfig() {
	    return mListConfig;
	}

	@Override
	protected ListAdapter createListAdapter(Cursor cursor) {
		ListAdapter adapter =
			new SimpleCursorAdapter(this,
					android.R.layout.simple_list_item_1, cursor,
					new String[] { ProjectProvider.Projects.NAME },
					new int[] { android.R.id.text1 }) {

			public View getView(int position, View convertView, ViewGroup parent) {
				Cursor cursor = (Cursor)getItem(position);
				Project project = getListConfig().getPersister().read(cursor);
				ProjectView projectView;
				if (convertView instanceof ProjectView) {
					projectView = (ProjectView) convertView;
				} else {
					projectView = new ProjectView(parent.getContext());
				}
				projectView.setTaskCountArray(mTaskCountArray);
				projectView.updateView(project);
				return projectView;
			}

		};
		return adapter;
	}
	
    /**
     * Return the intent generated when a list item is clicked.
     * 
     * @param url type of data selected
     */ 
	@Override
    protected Intent getClickIntent(Uri uri) {
    	// if a project is clicked on, show tasks for that project.
    	Intent intent = new Intent(this, ProjectTasksActivity.class);
    	intent.setData(uri);
    	return intent;
    }

}
