package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Preferences;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.MenuUtils;

import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class InboxActivity extends AbstractTaskListActivity {

	private static final String cTag = "InboxActivity";
	
	@Override
	protected CharSequence createTitle() {
		return getResources().getString(R.string.title_inbox);
	}

	@Override
	protected Cursor createItemQuery() {
		Log.d(cTag, "Creating a cursor to find tasks with no projects or created since last clean");
		return managedQuery(Shuffle.Tasks.cInboxTasksContentURI, Shuffle.Tasks.cExpandedProjection, null, null, null);
	}

	@Override
    protected int getCurrentViewMenuId() {
    	return MenuUtils.INBOX_ID;
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuUtils.addCleanInboxMenuItem(menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MenuUtils.CLEAN_INBOX_ID:
        	Preferences.cleanUpInbox(this);
        	// need to restart the activity since the query has changed
        	// mCursor.requery() not enough
        	startActivity(new Intent(this, InboxActivity.class));
    		finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
