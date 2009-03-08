package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.activity.config.ExpandableListConfig;
import org.dodgybits.android.shuffle.activity.config.ProjectExpandableListConfig;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.view.ExpandableProjectView;
import org.dodgybits.android.shuffle.view.ExpandableTaskView;
import org.dodgybits.android.shuffle.view.ProjectView;
import org.dodgybits.android.shuffle.view.TaskView;

import android.database.Cursor;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

public class ExpandableProjectsActivity extends AbstractExpandableActivity<Project, Task> {
    private int mChildIdColumnIndex; 
	private int mGroupIdColumnIndex; 
	private SparseIntArray mTaskCountArray;

	@Override
	protected ExpandableListConfig<Project, Task> createListConfig() {
		return new ProjectExpandableListConfig();
	}
	
	@Override
	protected void refreshChildCount() {
		Cursor cursor = getContentResolver().query(Shuffle.Projects.cProjectTasksContentURI, Shuffle.Projects.cFullTaskProjection, null, null, null);
		mTaskCountArray = BindingUtils.readCountArray(cursor);
		cursor.close();
	}
		
	@Override
	protected Cursor createGroupQuery() {
		Cursor cursor = managedQuery(Shuffle.Projects.CONTENT_URI, Shuffle.Projects.cFullProjection,
				null, null, Shuffle.Projects.NAME + " ASC");
		mGroupIdColumnIndex = cursor.getColumnIndex(Shuffle.Projects._ID);
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
		Cursor cursor = managedQuery(Shuffle.Tasks.CONTENT_URI, Shuffle.Tasks.cExpandedProjection,
				Shuffle.Tasks.PROJECT_ID + " = ?", new String[] {String.valueOf(groupId)}, 
				Shuffle.Tasks.DISPLAY_ORDER + " ASC");
		mChildIdColumnIndex = cursor.getColumnIndex(Shuffle.Tasks._ID);
		return cursor;		
	}

	@Override
	protected ExpandableListAdapter createExpandableListAdapter(Cursor cursor) {
		return new MyExpandableListAdapter(this, 
        		cursor,
                android.R.layout.simple_expandable_list_item_1,
                android.R.layout.simple_expandable_list_item_1,
                new String[] {Shuffle.Projects.NAME}, 
                new int[] {android.R.id.text1},
                new String[] {Shuffle.Tasks.DESCRIPTION},
                new int[] {android.R.id.text1}) {

	        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
	        	Cursor cursor = (Cursor) getChild(groupPosition, childPosition);
				Task task = getListConfig().readChild(cursor);
				TaskView taskView;
				if (convertView instanceof ExpandableTaskView) {
					taskView = (ExpandableTaskView) convertView;
				} else {
					taskView = new ExpandableTaskView(parent.getContext(), true);
				}
				taskView.updateView(task);
				return taskView;
	        }

	        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
	        	Cursor cursor = (Cursor) getGroup(groupPosition);
				Project project = getListConfig().readGroup(cursor);
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

}
