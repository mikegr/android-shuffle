package org.dodgybits.android.shuffle.activity.config;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.util.MenuUtils;

import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;

public class ContextExpandableListConfig implements ExpandableListConfig<Context, Task> {

	public Uri getChildContentUri() {
		return Shuffle.Tasks.CONTENT_URI;
	}

	public String getChildName(ContextWrapper context) {
		return context.getString(R.string.task_name);
	}

	public int getContentViewResId() {
		return R.layout.expandable_contexts;
	}

	public int getCurrentViewMenuId() {
    	return MenuUtils.CONTEXT_ID;
	}

	public Uri getGroupContentUri() {
		return Shuffle.Contexts.CONTENT_URI;
	}

	public String getGroupIdColumnName() {
		return Shuffle.Tasks.CONTEXT_ID;
	}

	public String getGroupName(ContextWrapper context) {
		return context.getString(R.string.context_name);
	}
	
	public Task readChild(Cursor cursor) {
        return BindingUtils.readTask(cursor);
	}

	public Context readGroup(Cursor cursor) {
        return BindingUtils.readContext(cursor);
	}
	
}
