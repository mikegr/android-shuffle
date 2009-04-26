/*
 * Copyright (C) 2009 Android Shuffle Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
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
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

/**
 * A generic activity for editing an item in a database.  This can be used
 * either to simply view an item (Intent.VIEW_ACTION), view and edit an item
 * (Intent.EDIT_ACTION), or create a new item (Intent.INSERT_ACTION).  
 */
public abstract class AbstractEditorActivity<T> extends Activity 
	implements View.OnClickListener, View.OnFocusChangeListener {

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
            // set the intent to edit so we don't keep recreating if user
            // switches orientation
            intent.setAction(Intent.ACTION_EDIT);
            intent.setData(mUri);
            setIntent(intent);

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
        
        // Setup the bottom buttons
        View view = findViewById(R.id.saveButton);
        view.setOnClickListener(this);
        view = findViewById(R.id.discardButton);
        view.setOnClickListener(this);
        
    }
    
    @Override
    protected void onResume() {
        Log.d(cTag, "onResume+");
        super.onResume();
        if (mCursor != null) {
        	// need to requery otherwise values saved on onPause are not restored
        	mCursor.requery();
        }
        Log.d(cTag, "onResume-");
    }
    
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
        MenuUtils.addPrefsHelpMenuItems(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle all of the possible menu actions.
        switch (item.getItemId()) {
        case MenuUtils.SAVE_ID:
        	doSaveAction();
            break;
        case MenuUtils.SAVE_AND_ADD_ID:
        	// create an Intent for the new item based on the current item
        	startActivity(getInsertIntent());
        	doSaveAction();
            break;
        case MenuUtils.DELETE_ID:
            deleteItem();
            finish();
            break;
        case MenuUtils.DISCARD_ID:
        	doRevertAction();
            break;
        case MenuUtils.REVERT_ID:
        	doRevertAction();
            break;
        }
        if (MenuUtils.checkCommonItemsSelected(item, this, MenuUtils.INBOX_ID)) {
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onFocusChange(View v, boolean hasFocus) {
        // Because we're emulating a ListView, we need to setSelected() for
        // views as they are focused.
        v.setSelected(hasFocus);
    }    
    
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.saveButton:
            	doSaveAction();
                break;

            case R.id.discardButton:
            	doRevertAction();
                break;
        }
    }

    protected void doSaveAction() {
        // Save or create the contact if needed
//        switch (mState) {
//            case STATE_EDIT:
//                save();
//                break;
//
//            case STATE_INSERT:
//                create();
//                break;
//
//            default:
//                Log.e(TAG, "Unknown state in doSaveOrCreate: " + mState);
//                break;
//        }
        finish();
    }
    
    /**
     * Take care of canceling work on a item.  Deletes the item if we
     * had created it, otherwise reverts to the original text.
     */
    protected void doRevertAction() {
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
    
    protected final void showSaveToast() {
    	String text;
    	if (mState == State.STATE_EDIT) {
    		text = getResources().getString(R.string.itemSavedToast, getItemName());
    	} else {
    		text = getResources().getString(R.string.itemCreatedToast, getItemName());
    	}
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * @return id of layout for this view
     */
    abstract protected int getContentViewResId();

    abstract protected T restoreItem(Bundle icicle);

    abstract protected void saveItem(Bundle outState, T item);
    
    abstract protected void writeItem(ContentValues values, T item);

    abstract protected Intent getInsertIntent();
    
    abstract protected CharSequence getItemName();
    
}
