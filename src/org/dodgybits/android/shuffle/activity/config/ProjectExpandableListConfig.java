package org.dodgybits.android.shuffle.activity.config;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.util.MenuUtils;

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
