package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.MenuUtils;

import android.database.Cursor;
import android.util.Log;

public class TopTasksActivity extends AbstractTaskListActivity {

	private static final String cTag = "TopTasksActivity";

	@Override
	protected CharSequence createTitle() {
		return getResources().getString(R.string.title_next_tasks);
	}

	@Override
	protected Cursor createItemQuery() {
		Log.d(cTag, "Creating a cursor to find top tasks");
		return managedQuery(Shuffle.Tasks.cTopTasksContentURI,
				Shuffle.Tasks.cExpandedProjection,
				null,
				null, 
				"project.name ASC");
	}

	@Override
    protected int getCurrentViewMenuId() {
    	return MenuUtils.TOP_TASKS_ID;
    }


}
