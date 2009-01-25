package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.MenuUtils;
import org.dodgybits.android.shuffle.util.BindingUtils;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ProjectTasksActivity extends AbstractTaskListActivity {

	private static final String cTag = "ProjectTasksActivity";
	private long mProjectId;
	private Project mProject;

	@Override
    public void onCreate(Bundle icicle) {
		Uri contextURI = getIntent().getData();
		mProjectId = ContentUris.parseId(contextURI);
        super.onCreate(icicle);
	}
	
	@Override
	protected Cursor createItemQuery() {
		Log.d(cTag, "Creating a cursor to find tasks for the given context");
		return managedQuery(getListContentUri(), Shuffle.Tasks.cExpandedProjection,
					Shuffle.Tasks.PROJECT_ID + " = ?", new String[] {String.valueOf(mProjectId)}, 
					Shuffle.Tasks.DISPLAY_ORDER + " ASC");
	}

	@Override
	protected Uri getListContentUri() {
		return Shuffle.Tasks.CONTENT_URI;
	}

	@Override
	protected void onResume() {
		Log.d(cTag, "Fetching project " + mProjectId);
		Cursor cursor = getContentResolver().query(Shuffle.Projects.CONTENT_URI, Shuffle.Projects.cFullProjection,
				Shuffle.Projects._ID + " = ?", new String[] {String.valueOf(mProjectId)}, null);
		if (cursor.moveToNext()) {
			mProject = BindingUtils.readProject(cursor);
		}
		cursor.close();
		
		super.onResume();
	}

	@Override
	protected CharSequence createTitle() {
		return getResources().getString(R.string.title_project_tasks, mProject.name);
	}
	
	@Override
    protected int getCurrentViewMenuId() {
    	return 0;
    }

    /**
     * Return the intent generated when a list item is clicked.
     * 
     * @param url type of data selected
     */ 
    protected Intent getClickIntent(Uri uri) {
    	long taskId = ContentUris.parseId(uri);
    	Uri taskUri = ContentUris.appendId(Shuffle.Tasks.CONTENT_URI.buildUpon(), taskId).build();
    	return new Intent(Intent.ACTION_VIEW, taskUri);
    }
    
    /**
     * Add project id to intent extras so it can be pre-filled for the task.
     */
    protected Intent getInsertIntent() {
    	Intent intent = super.getInsertIntent();
    	Bundle extras = intent.getExtras();
    	if (extras == null) extras = new Bundle();
    	extras.putString(Shuffle.Tasks.PROJECT_ID, mProject.name);
    	if (mProject.defaultContextId != null) {
    		Context context = BindingUtils.fetchContextById(this, mProject.defaultContextId);
    		if (context != null) {
        		extras.putString(Shuffle.Tasks.CONTEXT_ID, context.name);
    		}
    	}
    	intent.putExtras(extras);
    	return intent;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        
        final boolean haveItems = getItemCount() > 0;

        // If there are any projects in the list (which implies that one of
        // them is selected), then we need to generate the actions that
        // can be performed on the current selection.  This will be a combination
        // of our own specific actions along with any extensions that can be
        // found.
        if (haveItems) {
        	MenuUtils.addMoveMenuItems(menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MenuUtils.MOVE_UP_ID:
            moveUp();
            return true;
        case MenuUtils.MOVE_DOWN_ID:
            moveDown();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    protected final void moveUp() {
    	int selection = getSelectedItemPosition();
    	if (selection > 0) {
    		Cursor cursor = (Cursor) getListAdapter().getItem(selection);
    		BindingUtils.swapTaskPositions(this, cursor, selection - 1, selection);
    		setSelection(selection - 1);
    	}
    }
    
    protected final void moveDown() {
    	int selection = getSelectedItemPosition();
    	if (selection < getItemCount() - 1) {
    		Cursor cursor = (Cursor) getListAdapter().getItem(selection);
    		BindingUtils.swapTaskPositions(this, cursor, selection, selection + 1);
    		setSelection(selection + 1);
    	}
    }

}
