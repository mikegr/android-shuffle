package org.dodgybits.android.shuffle.activity.config;

import android.content.ContextWrapper;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.util.MenuUtils;

public class ContextListConfig implements DrilldownListConfig<Context> {

	public String createTitle(ContextWrapper context) {
    	return context.getString(R.string.title_context);
	}

	public Uri getContentUri() {
		return Shuffle.Contexts.CONTENT_URI;
	}

	public int getContentViewResId() {
		return R.layout.contexts;
	}

	public int getCurrentViewMenuId() {
    	return MenuUtils.CONTEXT_ID;
	}

	public String getItemName(ContextWrapper context) {
		return context.getString(R.string.context_name);
	}

	public Uri getListContentUri() {
		return Shuffle.Contexts.cContextTasksContentURI;
	}

	public boolean isTaskList() {
		return false;
	}

	public Context readItem(Cursor cursor, Resources res) {
        return BindingUtils.readContext(cursor, res);
	}

	public boolean supportsViewAction() {
		return false;
	}
	
	public Uri getChildContentUri() {
		return Shuffle.Tasks.CONTENT_URI;
	}
	
	public String getChildName(ContextWrapper context) {
		return context.getString(R.string.task_name);
	}		

}
