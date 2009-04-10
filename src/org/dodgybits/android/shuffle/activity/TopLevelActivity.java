package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.MenuUtils;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Displays a list of the main activities.
 */
public class TopLevelActivity extends ListActivity {
    private static final String cTag = "TopLevelActivity";

    private static final int INBOX = 0;
    private static final int DUE_TASKS = 1;
    private static final int TOP_TASKS = 2;
    private static final int PROJECTS = 3;
    private static final int CONTEXTS = 4;
    
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.top_level);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuUtils.addPrefsHelpMenuItems(menu);
        return true;
    }

	@Override
	protected void onResume() {
        Log.d(cTag, "onResume+");
		super.onResume();

		// get counts for each item
		Cursor cursor;
		String[] projection = new String[] {"_id"};
		cursor = getContentResolver().query(Shuffle.Tasks.cInboxTasksContentURI, projection, null, null, null);
		int inboxCount = cursor.getCount();
		cursor.close();
		Uri dueTaskUri = Shuffle.Tasks.cDueTasksContentURI.buildUpon().appendPath(String.valueOf(Shuffle.Tasks.DAY_MODE)).build();
		cursor = getContentResolver().query(dueTaskUri, projection, null, null, null);
		int dueTasksCount = cursor.getCount();
		cursor.close();
		cursor = getContentResolver().query(Shuffle.Tasks.cTopTasksContentURI, projection, null, null, null);
		int topTasksCount = cursor.getCount();
		cursor.close();
		cursor = getContentResolver().query(Shuffle.Projects.CONTENT_URI, projection, null, null, null);
		int projectCount = cursor.getCount();
		cursor.close();
		cursor = getContentResolver().query(Shuffle.Contexts.CONTENT_URI, projection, null, null, null);
		int contextCount = cursor.getCount();
		cursor.close();

		Log.d(cTag, 
				"Inbox=" + inboxCount +
				" due=" + dueTasksCount +
				" top=" + topTasksCount +
				" projects=" + projectCount +
				" contexts=" + contextCount
				);
		
        String[] perspectives = getResources().getStringArray(R.array.perspectives);
        perspectives[INBOX] += " (" + inboxCount + ")";
        perspectives[DUE_TASKS] += " (" + dueTasksCount + ")";
        perspectives[TOP_TASKS] += " (" + topTasksCount + ")";
        perspectives[PROJECTS] += " (" + projectCount + ")";
        perspectives[CONTEXTS] += " (" + contextCount + ")";
        
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
        		this, R.layout.list_item_view, R.id.name, perspectives);
        setListAdapter(adapter);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (MenuUtils.checkCommonItemsSelected(item, this, -1)) {
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		MenuUtils.checkCommonItemsSelected(position + MenuUtils.INBOX_ID, this, -1, false);
    }

}
