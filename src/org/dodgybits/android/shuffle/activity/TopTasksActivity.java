package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.activity.config.AbstractTaskListConfig;
import org.dodgybits.android.shuffle.activity.config.ListConfig;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.MenuUtils;

import android.content.ContextWrapper;
import android.net.Uri;

public class TopTasksActivity extends AbstractTaskListActivity {

	@Override
	protected ListConfig<Task> createListConfig()
	{
		return new AbstractTaskListConfig() {

			public Uri getListContentUri() {
				// Tasks with no projects or created since last clean
				return Shuffle.Tasks.cTopTasksContentURI;
			}

		    public int getCurrentViewMenuId() {
		    	return MenuUtils.TOP_TASKS_ID;
		    }
		    
		    public String createTitle(ContextWrapper context)
		    {
		    	return context.getString(R.string.title_next_tasks);
		    }
			
		};
	}

}
