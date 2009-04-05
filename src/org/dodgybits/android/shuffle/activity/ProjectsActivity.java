package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.activity.config.ListConfig;
import org.dodgybits.android.shuffle.activity.config.ProjectListConfig;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
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
 * Display list of projects with task children.
 */
public class ProjectsActivity extends AbstractDrilldownListActivity<Project> {

	private static final String cTag = "ProjectsActivity";

	@Override
	protected void onResume() {
		super.onResume();
		
		Cursor cursor = getContentResolver().query(
				getDrilldownListConfig().getListContentUri(), 
				Shuffle.Projects.cFullTaskProjection, null, null, null);
		mTaskCountArray = BindingUtils.readCountArray(cursor);
		cursor.close();
	}

	@Override
	protected ListConfig<Project> createListConfig()
	{
		return new ProjectListConfig();
	}

	@Override
	protected void deleteChildren(int groupId) {
		getContentResolver().delete(
				getDrilldownListConfig().getChildContentUri(), 
				Shuffle.Tasks.PROJECT_ID + " = ?", new String[] {String.valueOf(groupId)});
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
