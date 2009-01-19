package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.MenuUtils;

import android.net.Uri;

public class TopTasksActivity extends AbstractTaskListActivity {

	@Override
	protected CharSequence createTitle() {
		return getResources().getString(R.string.title_next_tasks);
	}

	@Override
	protected Uri getListContentUri() {
		return Shuffle.Tasks.cTopTasksContentURI;
	}
	
	@Override
    protected int getCurrentViewMenuId() {
    	return MenuUtils.TOP_TASKS_ID;
    }


}
