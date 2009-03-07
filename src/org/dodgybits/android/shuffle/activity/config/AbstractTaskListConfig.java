package org.dodgybits.android.shuffle.activity.config;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;

import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;

public abstract class AbstractTaskListConfig implements ListConfig<Task> {

	public int getContentViewResId() {
		return R.layout.task_list;
	}

	public Uri getContentUri() {
		return Shuffle.Tasks.CONTENT_URI;
	}
	
	public String getItemName(ContextWrapper context) {
		return context.getString(R.string.task_name);
	}
	
	public Task readItem(Cursor c) {
        return BindingUtils.readTask(c);
    }
    
  
    public boolean supportsViewAction() {
		return true;
	}
    
    public boolean isTaskList() {
    	return true;
    }
	
}
