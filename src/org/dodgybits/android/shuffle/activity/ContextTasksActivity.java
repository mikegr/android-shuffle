package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ContextTasksActivity extends AbstractTaskListActivity {

	private static final String cTag = "ContextTasksActivity";
	private long mContextId;
	private Context mContext;

	@Override
    public void onCreate(Bundle icicle) {
		Uri contextUri = getIntent().getData();
		mContextId = ContentUris.parseId(contextUri);
        super.onCreate(icicle);
	}
	
    @Override
	protected Cursor createItemQuery() {
		Log.d(cTag, "Creating a cursor to find tasks for the given context");
		return managedQuery(Shuffle.Tasks.CONTENT_URI, Shuffle.Tasks.cExpandedProjection,
					Shuffle.Tasks.CONTEXT_ID + " = ?", new String[] {String.valueOf(mContextId)}, 
					Shuffle.Tasks.CREATED_DATE + " ASC");
	}

	@Override
	protected boolean showTaskContext() {
		return false;
	}

	@Override
	protected void onResume() {
		Log.d(cTag, "Fetching context " + mContextId);
		Cursor cursor = getContentResolver().query(Shuffle.Contexts.CONTENT_URI, Shuffle.Contexts.cFullProjection,
				Shuffle.Contexts._ID + " = ?", new String[] {String.valueOf(mContextId)}, null);
		if (cursor.moveToNext()) {
			mContext = BindingUtils.readContext(cursor);
		}
		cursor.close();
		
		super.onResume();
	}

    @Override
	protected CharSequence createTitle() {
		return getResources().getString(R.string.title_context_tasks, mContext.name);
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
    @Override
    protected Intent getClickIntent(Uri uri) {
    	long taskId = ContentUris.parseId(uri);
    	Uri taskURI = ContentUris.withAppendedId(Shuffle.Tasks.CONTENT_URI, taskId);
    	return new Intent(Intent.ACTION_VIEW, taskURI);
    }

    /**
     * Add context name to intent extras so it can be pre-filled for the task.
     */
    @Override
    protected Intent getInsertIntent() {
    	Intent intent = super.getInsertIntent();
    	Bundle extras = intent.getExtras();
    	if (extras == null) extras = new Bundle();
    	extras.putString(Shuffle.Tasks.CONTEXT_ID, mContext.name);
    	intent.putExtras(extras);
    	return intent;
    }

}
