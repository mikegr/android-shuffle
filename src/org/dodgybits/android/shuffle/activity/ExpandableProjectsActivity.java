package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.MenuUtils;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.view.ExpandableProjectView;
import org.dodgybits.android.shuffle.view.ExpandableTaskView;
import org.dodgybits.android.shuffle.view.ProjectView;
import org.dodgybits.android.shuffle.view.TaskView;

import android.database.Cursor;
import android.net.Uri;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

public class ExpandableProjectsActivity extends AbstractExpandableActivity<Project, Task> {
	@SuppressWarnings("unused")
	private static final String cTag = "ExpandableProjectsActivity";
	
    private int mChildIdColumnIndex; 
	private int mGroupIdColumnIndex; 
	private SparseIntArray mTaskCountArray;

	@Override
	protected void refreshChildCount() {
		Cursor cursor = getContentResolver().query(Shuffle.Projects.cProjectTasksContentURI, Shuffle.Projects.cFullTaskProjection, null, null, null);
		mTaskCountArray = BindingUtils.readCountArray(cursor);
		cursor.close();
	}
	
	@Override
	protected int getContentViewResId() {
		return R.layout.expandable_projects;
	}
	
	@Override
	protected Uri getGroupContentUri() {
		return Shuffle.Projects.CONTENT_URI;
	}

	@Override
	protected Uri getChildContentUri() {
		return Shuffle.Tasks.CONTENT_URI;
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
    protected int getCurrentViewMenuId() {
    	return MenuUtils.PROJECT_ID;
    }

	@Override
	protected String getChildName() {
		return getString(R.string.task_name);
	}

	@Override
	protected String getGroupName() {
		return getString(R.string.project_name);
	}
	
	@Override
	protected String getGroupIdColumnName() {
		return Shuffle.Tasks.PROJECT_ID;
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
				Task task = readChild(cursor);
				TaskView taskView;
				if (convertView instanceof ExpandableTaskView) {
					taskView = (ExpandableTaskView) convertView;
				} else {
					taskView = new ExpandableTaskView(parent.getContext(), true);
				}
				boolean isSelected = false;
				taskView.updateView(task, isSelected);
				return taskView;
	        }

	        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
	        	Cursor cursor = (Cursor) getGroup(groupPosition);
				Project project = readGroup(cursor);
				ProjectView projectView;
				if (convertView instanceof ExpandableProjectView) {
					projectView = (ExpandableProjectView) convertView;
				} else {
					projectView = new ExpandableProjectView(parent.getContext());
				}
				projectView.setTaskCountArray(mTaskCountArray);
				boolean isSelected = false;
				projectView.updateView(project, isSelected);
				return projectView;
	        }
			
		};
	}

	@Override
	protected Task readChild(Cursor cursor) {
        return BindingUtils.readTask(cursor);
	}

	@Override
	protected Project readGroup(Cursor cursor) {
        return BindingUtils.readProject(cursor);
	}

}
