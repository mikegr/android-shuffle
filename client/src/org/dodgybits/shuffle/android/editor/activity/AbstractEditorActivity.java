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

package org.dodgybits.shuffle.android.editor.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Entity;
import org.dodgybits.shuffle.android.core.model.encoding.EntityEncoder;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.list.activity.State;
import org.dodgybits.shuffle.android.preference.model.Preferences;

import roboguice.activity.GuiceActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * A generic activity for editing an item in a database.  This can be used
 * either to simply view an item (Intent.VIEW_ACTION), view and edit an item
 * (Intent.EDIT_ACTION), or create a new item (Intent.INSERT_ACTION).  
 */
public abstract class AbstractEditorActivity<E extends Entity> extends GuiceActivity 
	implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String cTag = "AbstractEditorActivity";

    protected int mState;
    protected Uri mUri;
    protected Cursor mCursor;
    protected E mOriginalItem;
    protected Intent mNextIntent;
    
    protected EntityPersister<E> mPersister;
    protected EntityEncoder<E> mEncoder;
    
    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);

        mPersister = createPersister();
        mEncoder = createEncoder();
        
        processIntent();
        
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        setContentView(getContentViewResId());
        
        addSavePanelListeners();
        Log.d(cTag, "onCreate-");
    }
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            	doSaveAction();
                break;
        }

        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public void finish() {
		if (mNextIntent != null) {
    		startActivity(mNextIntent);
		}
		super.finish();
    }    

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.findItem(MenuUtils.SYNC_ID);
        if (item != null) {
            item.setVisible(Preferences.validateTracksSettings(this));
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuUtils.addEditorMenuItems(menu, mState);
        MenuUtils.addPrefsHelpMenuItems(this, menu);

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
            doDeleteAction();
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
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	E item = createItemFromUI();
    	saveItem(outState, item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	E item = restoreItem(savedInstanceState);
    	updateUIFromItem(item);
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
    	Uri result = null;
        switch (mState) {
            case State.STATE_EDIT:
                result = save();
                break;

            case State.STATE_INSERT:
                result = create();
                break;

            default:
                Log.e(cTag, "Unknown state in doSaveAction: " + mState);
                break;
        }
        
        if (result == null) {
        	setResult(RESULT_CANCELED);
        } else {
        	setResult(RESULT_OK, new Intent().setData(result));
        }
        finish();
    }
    
    /**
     * Take care of canceling work on a item.  Deletes the item if we
     * had created it, otherwise reverts to the original text.
     */
    protected void doRevertAction() {
        if (mCursor != null) {
            if (mState == State.STATE_EDIT) {
                // Put the original item back into the database
                mCursor.close();
                mCursor = null;
                mPersister.update(mOriginalItem);
            } else if (mState == State.STATE_INSERT) {
            	// if inserting, there's nothing to delete
            }
        }
        setResult(RESULT_CANCELED);
        finish();
    }
    
    /**
     * Take care of deleting a item.  Simply deletes the entry.
     */
    protected void doDeleteAction() {
    	// if inserting, there's nothing to delete
        if (mState == State.STATE_EDIT && mCursor != null) {
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
    
    protected boolean isValid() {
    	return true;
    }
    
    protected Uri create() {
    	Uri uri = null;
    	if (isValid()) {
   	       E item = createItemFromUI();
   	       uri = mPersister.insert(item);
   	       showSaveToast();
    	}
    	return uri;
    }
    
    protected Uri save() {
    	Uri uri = null;
    	if (isValid()) {
            E item = createItemFromUI();
            mPersister.update(item);
	    	showSaveToast();
	    	uri = mUri;
    	}
    	return uri;
    }
    
    protected final E restoreItem(Bundle icicle) {
        return mEncoder.restore(icicle);
    }
    
    protected final void saveItem(Bundle outState, E item) {
        mEncoder.save(outState, item);
    }
        
    /**
     * @return id of layout for this view
     */
    abstract protected int getContentViewResId();
    
    abstract protected E createItemFromUI();
    abstract protected void updateUIFromItem(E item);
    abstract protected void updateUIFromExtras(Bundle extras);
    
    abstract protected EntityPersister<E> createPersister();
    abstract protected EntityEncoder<E> createEncoder();
    
    abstract protected Intent getInsertIntent();
    
    abstract protected CharSequence getItemName();
    
    private void processIntent() {
        final Intent intent = getIntent();
        // Do some setup based on the action being performed.
        final String action = intent.getAction();
        mUri = intent.getData();
        if (action.equals(Intent.ACTION_EDIT)) {
            // Requested to edit: set that state, and the data being edited.
            mState = State.STATE_EDIT;
        } else if (action.equals(Intent.ACTION_INSERT)) {
            // Requested to insert: set that state, and create a new entry
            // in the container.
            mState = State.STATE_INSERT;
        } else {
            // Whoops, unknown action!  Bail.
            Log.e(cTag, "Unknown action " + action + ", exiting");
            finish();
            return;
        }
    }
    
    private void addSavePanelListeners() {
        // Setup the bottom buttons
        View view = findViewById(R.id.saveButton);
        view.setOnClickListener(this);
        view = findViewById(R.id.discardButton);
        view.setOnClickListener(this);
    }
    
}
