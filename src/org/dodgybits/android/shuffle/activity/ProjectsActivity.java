package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.util.MenuUtils;
import org.dodgybits.android.shuffle.view.ProjectView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

/**
 * Display list of projects.
 */
public class ProjectsActivity extends AbstractDrilldownListActivity<Project> {

	private static final String cTag = "ProjectsActivity";

	@Override
	protected void onResume() {
		super.onResume();
		
		Cursor cursor = getContentResolver().query(Shuffle.Projects.cProjectTasksContentURI, Shuffle.Projects.cFullTaskProjection, null, null, null);
		mTaskCountArray = BindingUtils.readCountArray(cursor);
		cursor.close();
	}

	@Override
	protected int getContentViewResId() {
		return R.layout.projects;
	}

	@Override
	protected Uri getContentUri() {
		return Shuffle.Projects.CONTENT_URI;
	}
	
	@Override
	protected Uri getChildContentUri() {
		return Shuffle.Tasks.CONTENT_URI;
	}
	
	@Override
	protected String getChildName() {
		return getString(R.string.task_name);
	}

	@Override
	protected void deleteChildren(int groupId) {
		getContentResolver().delete(getChildContentUri(), Shuffle.Tasks.PROJECT_ID + " = ?", new String[] {String.valueOf(groupId)});
	}
	
	@Override
	protected Cursor createItemQuery() {
		Log.d(cTag, "Creating a cursor over all projects");
		return managedQuery(getIntent().getData(), Shuffle.Projects.cFullProjection,
				null, null, 
				Shuffle.Projects.NAME + " ASC");
	}
	
	@Override
	protected ListAdapter createListAdapter(Cursor cursor) {
		ListAdapter adapter =
			new SimpleCursorAdapter(this,
					android.R.layout.simple_list_item_1, cursor,
					new String[] { Shuffle.Projects.NAME },
					new int[] { android.R.id.text1 }) {

			public View getView(int position, View convertView, ViewGroup parent) {
				Cursor cursor = (Cursor)getItem(position);
				Project project = BindingUtils.readProject(cursor);
				ProjectView projectView;
				if (convertView instanceof ProjectView) {
					projectView = (ProjectView) convertView;
				} else {
					projectView = new ProjectView(parent.getContext());
				}
				projectView.setTaskCountArray(mTaskCountArray);
				boolean isSelected = false;
				projectView.updateView(project, isSelected);
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

	@Override
    protected Project readItem(Cursor c) {
        return BindingUtils.readProject(c);
    }

	@Override
    protected int getCurrentViewMenuId() {
    	return MenuUtils.PROJECT_ID;
    }
	@Override
	protected String getItemName() {
		return getString(R.string.project_name);
	}
	
    @Override
    protected boolean isTaskList() {
    	return false;
    }


}
