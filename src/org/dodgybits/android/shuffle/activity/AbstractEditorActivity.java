package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.model.State;
import org.dodgybits.android.shuffle.util.MenuUtils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * A generic activity for editing an item in a database.  This can be used
 * either to simply view an item (Intent.VIEW_ACTION), view and edit an item
 * (Intent.EDIT_ACTION), or create a new item (Intent.INSERT_ACTION).  
 */
public abstract class AbstractEditorActivity<T> extends Activity {

    private static final String cTag = "AbstractEditorActivity";

    protected int mState;
    protected Uri mUri;
    protected Cursor mCursor;
    protected T mOriginalItem;

    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        final Intent intent = getIntent();
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        // Do some setup based on the action being performed.
        final String action = intent.getAction();
        if (action.equals(Intent.ACTION_EDIT)) {
            // Requested to edit: set that state, and the data being edited.
            mState = State.STATE_EDIT;
            mUri = intent.getData();
        } else if (action.equals(Intent.ACTION_INSERT)) {
            // Requested to insert: set that state, and create a new entry
            // in the container.
            mState = State.STATE_INSERT;
            mUri = getContentResolver().insert(intent.getData(), null);

            // If we were unable to create a new item, then just finish
            // this activity.  A RESULT_CANCELED will be sent back to the
            // original activity if they requested a result.
            if (mUri == null) {
                Log.e(cTag, "Failed to insert new item into "
                        + getIntent().getData());
                finish();
                return;
            }
            // The new entry was created, so assume all will end well and
            // set the result to be returned.
    		Bundle bundle = new Bundle();
    	    bundle.putString(AbstractListActivity.cSelectedItem, mUri.toString());
    	    Intent mIntent = new Intent();
    	    mIntent.putExtras(bundle);
    	    setResult(RESULT_OK, mIntent);
        } else {
            // Whoops, unknown action!  Bail.
            Log.e(cTag, "Unknown action " + action + ", exiting");
            finish();
            return;
        }
        // If an instance of this activity had previously stopped, we can
        // get the original text it started with.
        if (icicle != null) {
            mOriginalItem = restoreItem(icicle);
        } else {
        	mOriginalItem = null;
        }

        setContentView(getContentViewResId());
    }
    
    /**
     * @return id of layout for this view
     */
    abstract int getContentViewResId();

    abstract T restoreItem(Bundle icicle);

    abstract void saveItem(Bundle outState, T item);
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(cTag, "onSaveInstanceState+");

        // Save away the original item, so we still have it if the activity
        // needs to be killed while paused
        saveItem(outState, mOriginalItem);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuUtils.addEditorMenuItems(menu, mState);
        MenuUtils.addAlternativeMenuItems(menu, getIntent().getData(), this);
        MenuUtils.addPrefsHelpMenuItems(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle all of the possible menu actions.
        switch (item.getItemId()) {
        case MenuUtils.SAVE_ID:
            finish();
            break;
        case MenuUtils.DELETE_ID:
            deleteItem();
            finish();
            break;
        case MenuUtils.DISCARD_ID:
            cancelItem();
            break;
        case MenuUtils.REVERT_ID:
            cancelItem();
            break;
        }
        if (MenuUtils.checkCommonItemsSelected(item, this, MenuUtils.INBOX_ID)) {
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }

    abstract void writeItem(ContentValues values, T item);
    
    /**
     * Take care of canceling work on a item.  Deletes the item if we
     * had created it, otherwise reverts to the original text.
     */
    protected void cancelItem() {
        if (mCursor != null) {
            if (mState == State.STATE_EDIT) {
                // Put the original note text back into the database
                mCursor.close();
                mCursor = null;
                ContentValues values = new ContentValues();
            	writeItem(values, mOriginalItem);
                getContentResolver().update(mUri, values, null, null);
            } else if (mState == State.STATE_INSERT) {
                deleteItem();
            }
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * Take care of deleting a item.  Simply deletes the entry.
     */
    protected void deleteItem() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
            getContentResolver().delete(mUri, null, null);
        }
    }
    
}
