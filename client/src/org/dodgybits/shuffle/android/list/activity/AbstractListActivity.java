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

package org.dodgybits.shuffle.android.list.activity;

import android.widget.*;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.FlurryEnabledListActivity;
import org.dodgybits.shuffle.android.core.model.Entity;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.list.config.ListConfig;
import org.dodgybits.shuffle.android.list.view.ButtonBar;
import org.dodgybits.shuffle.android.preference.model.Preferences;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import roboguice.event.Observes;
import roboguice.inject.InjectView;

public abstract class AbstractListActivity<T extends Entity> extends FlurryEnabledListActivity {

	public static final String cSelectedItem = "SELECTED_ITEM";

	private static final String cTag = "AbstractListActivity";


	protected final int NEW_ITEM = 1;
    protected static final int FILTER_CONFIG = 600;

	// after a new item is added, select it
	private Long mItemIdToSelect = null;

	private ListConfig<T> mConfig;

    @InjectView(R.id.button_bar)
    protected ButtonBar mButtonBar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		mConfig = createListConfig();
		
		setContentView(getListConfig().getContentViewResId());
		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
		// If no data was given in the intent (because we were started
		// as a MAIN activity), then use our default content provider.
		Intent intent = getIntent();
		if (intent.getData() == null) {
			intent.setData(getListConfig().getPersister().getContentUri());
		}

		// Inform the view we provide context menus for items
        getListView().setOnCreateContextMenuListener(this);
		
		Cursor cursor = getListConfig().createQuery(this);
		setListAdapter(createListAdapter(cursor));
	}

	@Override
	protected void onResume() {
		super.onResume();
        setTitle(getListConfig().createTitle(this));
		
		// attempt to select newly created item (if any)
		if (mItemIdToSelect != null) {
			Log.d(cTag, "New item id = " + mItemIdToSelect);
			// see if list contains the new item
			int count = getItemCount();
			CursorAdapter adapter = (CursorAdapter) getListAdapter();
			for (int i = 0; i < count; i++) {
				long currentItemId = adapter.getItemId(i);
				Log.d(cTag, "Current id=" + currentItemId + " pos=" + i);
				if (currentItemId == mItemIdToSelect) {
					Log.d(cTag, "Found new item - selecting");
					setSelection(i);
					break;
				}
			}
			mItemIdToSelect = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		Log.d(cTag, "Got resultCode " + resultCode + " with data " + data);
		switch (requestCode) {
		case NEW_ITEM:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					String selectedItem = data.getStringExtra(cSelectedItem);
					if (selectedItem != null) {
						Uri uri = Uri.parse(selectedItem);
						mItemIdToSelect = ContentUris.parseId(uri);
						// need to do the actual checking and selecting
						// in onResume, otherwise getItemId(i) is always 0
						// and setSelection(i) does nothing
					}
				}
			}
			break;

        case FILTER_CONFIG:
            Log.d(cTag, "Got result " + resultCode);
            updateCursor();
            break;

		default:
			Log.e(cTag, "Unknown requestCode: " + requestCode);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_N:
			// go to previous view
			int prevView = getListConfig().getCurrentViewMenuId() - 1;
			if (prevView < MenuUtils.INBOX_ID) {
				prevView = MenuUtils.CONTEXT_ID;
			}
			MenuUtils.checkCommonItemsSelected(prevView, this,
					getListConfig().getCurrentViewMenuId());
			return true;
		case KeyEvent.KEYCODE_M:
			// go to previous view
			int nextView = getListConfig().getCurrentViewMenuId() + 1;
			if (nextView > MenuUtils.CONTEXT_ID) {
				nextView = MenuUtils.INBOX_ID;
			}
			MenuUtils.checkCommonItemsSelected(nextView, this,
					getListConfig().getCurrentViewMenuId());
			return true;
		}
		return super.onKeyDown(keyCode, event);
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

		MenuUtils.addInsertMenuItems(menu, getListConfig().getItemName(this), getListConfig().isTaskList(), this);
		MenuUtils.addViewMenuItems(menu, getListConfig().getCurrentViewMenuId());
		MenuUtils.addPrefsHelpMenuItems(this, menu);
		MenuUtils.addSearchMenuItem(this, menu);
		return true;
	}


    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(cTag, "bad menuInfo", e);
            return;
        }

        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }

        // Setup the menu header
        menu.setHeaderTitle(cursor.getString(1));

    	Uri selectedUri = ContentUris.withAppendedId(getListConfig().getPersister().getContentUri(), info.id);
        MenuUtils.addSelectedAlternativeMenuItems(menu, selectedUri, false);
        
		// ... and ends with the delete command.
		MenuUtils.addDeleteMenuItem(menu);
    }
        
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(cTag, "bad menuInfo", e);
            return false;
        }

        switch (item.getItemId()) {
            case MenuUtils.DELETE_ID: {
                // Delete the item that the context menu is for
    			deleteItem(Id.create(info.id));
                return true;
            }
        }
        return false;
    }	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MenuUtils.INSERT_ID:
				// Launch activity to insert a new item
				startActivityForResult(getInsertIntent(), NEW_ITEM);
				return true;
		}
		if (MenuUtils.checkCommonItemsSelected(item, this,
				getListConfig().getCurrentViewMenuId())) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Uri url = ContentUris.withAppendedId(getListConfig().getPersister().getContentUri(), id);

		String action = getIntent().getAction();
		if (Intent.ACTION_PICK.equals(action)
				|| Intent.ACTION_GET_CONTENT.equals(action)) {
			// The caller is waiting for us to return a task selected by
			// the user. They have clicked on one, so return it now.
			Bundle bundle = new Bundle();
		    bundle.putString(cSelectedItem, url.toString());
		    Intent mIntent = new Intent();
		    mIntent.putExtras(bundle);
		    setResult(RESULT_OK, mIntent);			
		} else {
			// Launch activity to view/edit the currently selected item
			startActivity(getClickIntent(url));
		}
	}

	abstract protected ListConfig<T> createListConfig();
	
	abstract protected ListAdapter createListAdapter(Cursor cursor);
		
	// custom helper methods
	
	protected final ListConfig<T> getListConfig()
	{
		return mConfig;
	}


    protected void updateCursor() {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter)getListAdapter();
        Cursor oldCursor = adapter.getCursor();
        if (oldCursor != null) {
            // changeCursor always closes the cursor,
            // so need to stop managing the old one first
            stopManagingCursor(oldCursor);
            oldCursor.close();
        }

        Cursor cursor = getListConfig().createQuery(this);
        adapter.changeCursor(cursor);
        setTitle(getListConfig().createTitle(this));
    }


	/**
	 * Permanently delete the given list item.
	 */
	protected void deleteItem(Id id) {
	    getListConfig().getPersister().moveToTrash(id);
    	String text = getResources().getString(
    			R.string.itemDeletedToast, getListConfig().getItemName(this));
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();        
	}

	/**
	 * @return Number of items in the list.
	 */
	protected final int getItemCount() {
		return getListAdapter().getCount();
	}
	
	/**
	 * The intent to insert a new item in this list. Default to an insert action
	 * on the list type which is all you need most of the time.
	 */
	protected Intent getInsertIntent() {
		return new Intent(Intent.ACTION_INSERT, getListConfig().getPersister().getContentUri());
	}
	
	/**
	 * Return the intent generated when a list item is clicked.
	 * 
	 * @param uri
	 *            type of data selected
	 */
	protected Intent getClickIntent(Uri uri) {
		return new Intent(Intent.ACTION_EDIT, uri);
	}

    protected void onAddItem( @Observes ButtonBar.AddItemButtonClickEvent event ) {
        startActivityForResult(getInsertIntent(), NEW_ITEM);
    }

    protected void onOther( @Observes ButtonBar.OtherButtonClickEvent event ) {
    }

    protected void onFilter( @Observes ButtonBar.FilterButtonClickEvent event ) {
        Intent intent = new Intent(this, ListPreferenceActivity.class);
        getListConfig().getListPreferenceSettings().addToIntent(intent);
        startActivityForResult(intent, FILTER_CONFIG);
    }

	
}
