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

package org.dodgybits.shuffle.android.list.activity.expandable;

import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.list.config.ContextExpandableListConfig;
import org.dodgybits.shuffle.android.list.config.ExpandableListConfig;
import org.dodgybits.shuffle.android.list.view.ContextView;
import org.dodgybits.shuffle.android.list.view.ExpandableContextView;
import org.dodgybits.shuffle.android.list.view.ExpandableTaskView;
import org.dodgybits.shuffle.android.list.view.TaskView;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;

import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

import com.google.inject.Inject;

public class ExpandableContextsActivity extends AbstractExpandableActivity<Context> {
    private int mChildIdColumnIndex; 
    private int mGroupIdColumnIndex; 
	private SparseIntArray mTaskCountArray;
	
    @Inject ContextExpandableListConfig mListConfig;
	
    @Override
    protected ExpandableListConfig<Context> getListConfig() {
        return mListConfig;
    }
	
	@Override
	protected void refreshChildCount() {
		Cursor cursor = getContentResolver().query(
				ContextProvider.Contexts.cContextTasksContentURI, 
				ContextProvider.Contexts.cFullTaskProjection, null, null, null);
        mTaskCountArray = getListConfig().getChildPersister().readCountArray(cursor);
		cursor.close();
	}
	
	@Override
	protected Cursor createGroupQuery() {
		Cursor cursor = managedQuery(ContextProvider.Contexts.CONTENT_URI, ContextProvider.Contexts.cFullProjection,
				null, null, ContextProvider.Contexts.NAME + " ASC");
		mGroupIdColumnIndex = cursor.getColumnIndex(ContextProvider.Contexts._ID);
		return cursor;
	}

	@Override
	protected int getGroupIdColumnIndex() {
		return mGroupIdColumnIndex;
	}
	
	@Override
	protected int getChildIdColumnIndex() {
		return mChildIdColumnIndex;
	}

	@Override
	protected Cursor createChildQuery(long groupId) {
		Cursor cursor = managedQuery(TaskProvider.Tasks.CONTENT_URI, TaskProvider.Tasks.cFullProjection,
				TaskProvider.Tasks.CONTEXT_ID + " = ?", new String[] {String.valueOf(groupId)}, 
				TaskProvider.Tasks.CREATED_DATE + " ASC");
		mChildIdColumnIndex = cursor.getColumnIndex(TaskProvider.Tasks._ID);
		return cursor;
	}

	@Override
	protected void updateInsertExtras(Bundle extras, Context context) {
   		extras.putLong(TaskProvider.Tasks.CONTEXT_ID, context.getLocalId().getId());
	}
	
	@Override
	protected ExpandableListAdapter createExpandableListAdapter(Cursor cursor) {
		return new MyExpandableListAdapter(this, 
        		cursor,
                android.R.layout.simple_expandable_list_item_1,
                android.R.layout.simple_expandable_list_item_1,
                new String[] {ContextProvider.Contexts.NAME}, 
                new int[] {android.R.id.text1},
                new String[] {TaskProvider.Tasks.DESCRIPTION},
                new int[] {android.R.id.text1}) {

	        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
	        	Cursor cursor = (Cursor) getChild(groupPosition, childPosition);
				Task task = getListConfig().getChildPersister().read(cursor);
				TaskView taskView;
				if (convertView instanceof ExpandableTaskView) {
					taskView = (ExpandableTaskView) convertView;
				} else {
					taskView = new ExpandableTaskView(parent.getContext(), mContextCache, mProjectCache);
				}
				taskView.setShowContext(false);
				taskView.setShowProject(true);
				taskView.updateView(task);
				return taskView;
	        }

	        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
	        	Cursor cursor = (Cursor) getGroup(groupPosition);
	        	Context context = getListConfig().getGroupPersister().read(cursor);
				ContextView contextView;
				if (convertView instanceof ExpandableContextView) {
					contextView = (ExpandableContextView) convertView;
				} else {
					contextView = new ExpandableContextView(parent.getContext());
				}
				contextView.setTaskCountArray(mTaskCountArray);
				contextView.updateView(context);
				return contextView;
	        }
			
		};
	}

}
