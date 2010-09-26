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

import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.list.config.ExpandableListConfig;
import org.dodgybits.shuffle.android.list.config.ProjectExpandableListConfig;
import org.dodgybits.shuffle.android.list.view.ExpandableProjectView;
import org.dodgybits.shuffle.android.list.view.ExpandableTaskView;
import org.dodgybits.shuffle.android.list.view.ProjectView;
import org.dodgybits.shuffle.android.list.view.TaskView;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class ExpandableProjectsActivity extends AbstractExpandableActivity<Project> {
	private static final String cTag = "ExpandableProjectsActivity";

	private int mChildIdColumnIndex; 
	private int mGroupIdColumnIndex; 
	private SparseIntArray mTaskCountArray;

    @Inject ProjectExpandableListConfig mListConfig;
    @Inject Provider<ExpandableTaskView> mTaskViewProvider;

    @Override
    protected ExpandableListConfig<Project> getListConfig() {
        return mListConfig;
    }
    
	@Override
	protected void refreshChildCount() {
		Cursor cursor = getContentResolver().query(
				ProjectProvider.Projects.PROJECT_TASKS_CONTENT_URI, 
				ProjectProvider.Projects.FULL_TASK_PROJECTION, null, null, null);
		mTaskCountArray = getListConfig().getChildPersister().readCountArray(cursor);
		cursor.close();
	}
		
	@Override
	protected Cursor createGroupQuery() {
		Cursor cursor = managedQuery(ProjectProvider.Projects.CONTENT_URI, ProjectProvider.Projects.FULL_PROJECTION,
				ProjectProvider.Projects.DELETED + " = 0", 
				null, 
				ProjectProvider.Projects.NAME + " ASC");
		mGroupIdColumnIndex = cursor.getColumnIndex(ProjectProvider.Projects._ID);
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
				TaskProvider.Tasks.PROJECT_ID +  " = ? AND " + TaskProvider.Tasks.DELETED + "=0", new String[] {String.valueOf(groupId)}, 
				TaskProvider.Tasks.DUE_DATE + " ASC," + TaskProvider.Tasks.DISPLAY_ORDER + " ASC");
		mChildIdColumnIndex = cursor.getColumnIndex(TaskProvider.Tasks._ID);
		return cursor;		
	}

	@Override
	protected void updateInsertExtras(Bundle extras, Project project) {
    	extras.putLong(TaskProvider.Tasks.PROJECT_ID, project.getLocalId().getId());
    	
    	final Id defaultContextId = project.getDefaultContextId();
    	if (defaultContextId.isInitialised()) {
    		extras.putLong(TaskProvider.Tasks.CONTEXT_ID, defaultContextId.getId());
    	}
	}
	
	@Override
	protected ExpandableListAdapter createExpandableListAdapter(Cursor cursor) {
		return new MyExpandableListAdapter(this, 
        		cursor,
                android.R.layout.simple_expandable_list_item_1,
                android.R.layout.simple_expandable_list_item_1,
                new String[] {ProjectProvider.Projects.NAME}, 
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
                    taskView = mTaskViewProvider.get(); 
				}
				taskView.setShowContext(true);
				taskView.setShowProject(false);
				taskView.updateView(task);
				return taskView;
	        }

	        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
	        	Cursor cursor = (Cursor) getGroup(groupPosition);
				Project project = getListConfig().getGroupPersister().read(cursor);
				ProjectView projectView;
				if (convertView instanceof ExpandableProjectView) {
					projectView = (ExpandableProjectView) convertView;
				} else {
					projectView = new ExpandableProjectView(parent.getContext());
				}
				projectView.setTaskCountArray(mTaskCountArray);
				projectView.updateView(project);
				return projectView;
	        }
			
		};
	}
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, view, menuInfo);

    	ExpandableListView.ExpandableListContextMenuInfo info;
        try {
             info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(cTag, "bad menuInfo", e);
            return;
        }
        long packedPosition = info.packedPosition;
        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
        boolean isChild = isChild(packedPosition); 
        if (isChild) {
	    	MenuUtils.addMoveMenuItems(menu, 
	    			moveUpPermitted(groupPosition, childPosition), 
	    			moveDownPermitted(groupPosition, childPosition));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	ExpandableListView.ExpandableListContextMenuInfo info;
        try {
             info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(cTag, "bad menuInfo", e);
            return false;
        }

        switch (item.getItemId()) {
	        case MenuUtils.MOVE_UP_ID:
	            moveUp(info.packedPosition);
	            return true;
	        case MenuUtils.MOVE_DOWN_ID:
	            moveDown(info.packedPosition);
	            return true;
        }
        return super.onContextItemSelected(item);
    }	
    
    private boolean moveUpPermitted(int groupPosition,int childPosition) {
    	return childPosition > 0;
    }
    
    private boolean moveDownPermitted(int groupPosition,int childPosition) {
    	int childCount = getExpandableListAdapter().getChildrenCount(groupPosition);
    	return childPosition < childCount - 1;
    }
    
    protected final void moveUp(long packedPosition) {
        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
    	if (moveUpPermitted(groupPosition, childPosition)) {
    		Cursor cursor = (Cursor) getExpandableListAdapter().getChild(
    				groupPosition, childPosition);
            getListConfig().getChildPersister().swapTaskPositions(cursor, childPosition - 1, childPosition);
    	}
    }
    
    protected final void moveDown(long packedPosition) {
        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
    	if (moveDownPermitted(groupPosition, childPosition)) {
    		Cursor cursor = (Cursor) getExpandableListAdapter().getChild(
    				groupPosition, childPosition);
            getListConfig().getChildPersister().swapTaskPositions(cursor, childPosition, childPosition + 1);
    	}	
    }
	
}
