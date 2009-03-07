package org.dodgybits.android.shuffle.activity.config;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.util.MenuUtils;

import android.content.ContextWrapper;
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
		return Shuffle.Projects.cProjectTasksContentURI;
	}

	public boolean isTaskList() {
    	return false;
	}

	public Project readItem(Cursor cursor) {
        return BindingUtils.readProject(cursor);
	}

	public boolean supportsViewAction() {
		return false;
	}

}
