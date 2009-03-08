package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.activity.config.ListConfig;
import org.dodgybits.android.shuffle.util.MenuUtils;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class AbstractListActivity<T> extends ListActivity {

	public static final String cSelectedItem = "SELECTED_ITEM";

	private static final String cTag = "AbstractListActivity";

	protected final int NEW_ITEM = 1;
	// after a new item is added, select it
	private Long mItemIdToSelect = null;

	private ListConfig<T> mConfig;

	public AbstractListActivity()
	{
		mConfig = createListConfig();
	}
	
	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		setContentView(getListConfig().getContentViewResId());
		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
		// If no data was given in the intent (because we were started
		// as a MAIN activity), then use our default content provider.
		Intent intent = getIntent();
		if (intent.getData() == null) {
			intent.setData(getListConfig().getContentUri());
		}

		// Inform the view we provide context menus for items
        getListView().setOnCreateContextMenuListener(this);
		
		Cursor cursor = createItemQuery();
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
					Uri uri = Uri.parse(data.getStringExtra(cSelectedItem));
					mItemIdToSelect = ContentUris.parseId(uri);
					// need to do the actual checking and selecting
					// in onResume, otherwise getItemId(i) is always 0
					// and setSelection(i) does nothing
				}
			}
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
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuUtils.addInsertMenuItems(menu, getListConfig().getItemName(this), getListConfig().isTaskList(), this);
		MenuUtils.addAlternativeMenuItems(menu, getListConfig().getContentUri(), this);
		MenuUtils.addViewMenuItems(menu, getListConfig().getCurrentViewMenuId());
		MenuUtils.addPrefsHelpMenuItems(menu);

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

    	Uri selectedUri = ContentUris.withAppendedId(getListConfig().getContentUri(), info.id);
        MenuUtils.addSelectedAlternativeMenuItems(menu, selectedUri, this, false);
        
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
    			deleteItem(info.id);
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
		Uri url = ContentUris.withAppendedId(getListConfig().getContentUri(), id);

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
	
	/**
	 * @return a cursor selecting the items to display in the list.
	 */
	abstract protected Cursor createItemQuery();

	abstract protected ListAdapter createListAdapter(Cursor cursor);
	
	// custom helper methods
	
	protected final ListConfig<T> getListConfig()
	{
		return mConfig;
	}
		
	/**
	 * Permanently delete the given list item.
	 */
	protected void deleteItem(long id) {
        getContentResolver().delete(getListConfig().getListContentUri(), 
        		BaseColumns._ID + "=?", new String[] { String.valueOf(id) });
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
		return new Intent(Intent.ACTION_INSERT, getListConfig().getContentUri());
	}
	
	/**
	 * Return the intent generated when a list item is clicked.
	 * 
	 * @param url
	 *            type of data selected
	 */
	protected Intent getClickIntent(Uri uri) {
		return new Intent(Intent.ACTION_EDIT, uri);
	}

	
}
