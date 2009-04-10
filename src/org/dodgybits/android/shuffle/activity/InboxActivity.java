package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.activity.config.AbstractTaskListConfig;
import org.dodgybits.android.shuffle.activity.config.ListConfig;
import org.dodgybits.android.shuffle.model.Preferences;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.MenuUtils;

import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class InboxActivity extends AbstractTaskListActivity {

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
            Toast.makeText(this, R.string.clean_inbox_message, Toast.LENGTH_SHORT).show();
        	// need to restart the activity since the query has changed
        	// mCursor.requery() not enough
        	startActivity(new Intent(this, InboxActivity.class));
    		finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected ListConfig<Task> createListConfig()
	{
		return new AbstractTaskListConfig() {

			public Uri getListContentUri() {
				// Tasks with no projects or created since last clean
				return Shuffle.Tasks.cInboxTasksContentURI;
			}

		    public int getCurrentViewMenuId() {
		    	return MenuUtils.INBOX_ID;
		    }
		    
		    public String createTitle(ContextWrapper context)
		    {
		    	return context.getString(R.string.title_inbox);
		    }
			
		};
	}
	    
}
