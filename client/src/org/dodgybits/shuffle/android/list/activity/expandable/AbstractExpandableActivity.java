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

package org.dodgybits.shuffle.android.list.activity.expandable;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.FlurryEnabledExpandableListActivity;
import org.dodgybits.shuffle.android.core.model.Entity;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.persistence.EntityCache;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.view.AlertUtils;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.list.config.ExpandableListConfig;
import org.dodgybits.shuffle.android.list.view.SwipeListItemListener;
import org.dodgybits.shuffle.android.list.view.SwipeListItemWrapper;
import org.dodgybits.shuffle.android.preference.model.Preferences;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;

import com.google.inject.Inject;

public abstract class AbstractExpandableActivity<G extends Entity> extends FlurryEnabledExpandableListActivity 
	implements SwipeListItemListener {
	
	private static final String cTag = "AbstractExpandableActivity";

	protected ExpandableListAdapter mAdapter;
	@Inject protected EntityCache<org.dodgybits.shuffle.android.core.model.Context> mContextCache;
	@Inject protected EntityCache<Project> mProjectCache;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(getListConfig().getContentViewResId());
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        
		// Inform the list we provide context menus for items
        getExpandableListView().setOnCreateContextMenuListener(this);
        
        Cursor groupCursor = createGroupQuery();
        // Set up our adapter
        mAdapter = createExpandableListAdapter(groupCursor); 
        setListAdapter(mAdapter);
        
		// register self as swipe listener
		SwipeListItemWrapper wrapper = (SwipeListItemWrapper) findViewById(R.id.swipe_wrapper);
		wrapper.setSwipeListItemListener(this);        
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		refreshChildCount();
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
    		MenuUtils.checkCommonItemsSelected(prevView, this, getListConfig().getCurrentViewMenuId());
    		return true;
		case KeyEvent.KEYCODE_M:
			// go to previous view
			int nextView = getListConfig().getCurrentViewMenuId() + 1;
			if (nextView > MenuUtils.CONTEXT_ID) {
				nextView = MenuUtils.INBOX_ID;
			}
			MenuUtils.checkCommonItemsSelected(nextView, this, getListConfig().getCurrentViewMenuId());
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

        MenuUtils.addExpandableInsertMenuItems(menu, getListConfig().getGroupName(this), 
        		getListConfig().getChildName(this), this);
        MenuUtils.addViewMenuItems(menu, getListConfig().getCurrentViewMenuId());
        MenuUtils.addPrefsHelpMenuItems(this, menu);
        MenuUtils.addSearchMenuItem(this, menu);
        
        return true;
    }
        
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final EntityPersister<G> groupPersister = getListConfig().getGroupPersister();
        final TaskPersister childPersister = getListConfig().getChildPersister();
        
        switch (item.getItemId()) {
	        case MenuUtils.INSERT_CHILD_ID:
	        	long packedPosition = getSelectedPosition();
                int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
	        	if (groupPosition > -1)
	        	{
		        	Cursor cursor = (Cursor) getExpandableListAdapter().getGroup(groupPosition);
		        	G group = groupPersister.read(cursor);
	        		insertItem(childPersister.getContentUri(), group);	        		
	        	}
	        	else
	        	{
	        		insertItem(childPersister.getContentUri());
	        	}
	            return true;
	        case MenuUtils.INSERT_GROUP_ID:
	            insertItem(groupPersister.getContentUri());
	            return true;
        }
        if (MenuUtils.checkCommonItemsSelected(item, this, getListConfig().getCurrentViewMenuId())) return true;
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
    	ExpandableListView.ExpandableListContextMenuInfo info;
        try {
             info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(cTag, "bad menuInfo", e);
            return;
        }
        long packedPosition = info.packedPosition;
        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
        boolean isChild = isChild(packedPosition); 
        Cursor cursor;
        if (isChild) {
        	cursor = (Cursor)(getExpandableListAdapter().getChild(groupPosition, childPosition));
        } else {
        	cursor = (Cursor)(getExpandableListAdapter().getGroup(groupPosition));
        }
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }

        // Setup the menu header
        menu.setHeaderTitle(cursor.getString(1));

        if (isChild)
        {
        	long childId = getExpandableListAdapter().getChildId(groupPosition, childPosition);
            Uri selectedUri = ContentUris.withAppendedId(getListConfig().getChildPersister().getContentUri(), childId);
            MenuUtils.addSelectedAlternativeMenuItems(menu, selectedUri, false);
        	MenuUtils.addCompleteMenuItem(menu);
        }
        else
        {
        	long groupId = getExpandableListAdapter().getGroupId(groupPosition);
            Uri selectedUri = ContentUris.withAppendedId(getListConfig().getGroupPersister().getContentUri(), groupId);
            MenuUtils.addSelectedAlternativeMenuItems(menu, selectedUri, false);
            MenuUtils.addInsertMenuItems(menu, getListConfig().getChildName(this), true, this);
        }
		// ... and ends with the delete command.
		MenuUtils.addDeleteMenuItem(menu);
    }
        
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	ExpandableListView.ExpandableListContextMenuInfo info;
        try {
             info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(cTag, "bad menuInfo", e);
            return false;
        }

        switch (item.getItemId()) {
	        case MenuUtils.COMPLETE_ID:
	            toggleComplete(info.packedPosition, info.id);
	            return true;

	        case MenuUtils.DELETE_ID: 
                // Delete the item that the context menu is for
    			deleteItem(info.packedPosition);
                return true;
	            
	        case MenuUtils.INSERT_ID:
                int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
                final Uri childContentUri = getListConfig().getChildPersister().getContentUri();
	        	if (groupPosition > -1)
	        	{
		        	Cursor cursor = (Cursor) getExpandableListAdapter().getGroup(groupPosition);
		        	G group = getListConfig().getGroupPersister().read(cursor);
	        		insertItem(childContentUri, group);	        		
	        	}
	        	else
	        	{
	        		insertItem(childContentUri);
	        	}
	            return true;
        }
        return false;
    }	 
    
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
   		Uri url = ContentUris.withAppendedId(getListConfig().getChildPersister().getContentUri(), id);
		// Launch activity to view/edit the currently selected item
		startActivity(getClickIntent(url));
		return true;
	}
    	

    public class MyExpandableListAdapter extends SimpleCursorTreeAdapter {

        public MyExpandableListAdapter(Context context, Cursor cursor, int groupLayout,
                int childLayout, String[] groupFrom, int[] groupTo, String[] childrenFrom,
                int[] childrenTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childrenFrom,
                    childrenTo);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
        	long groupId = groupCursor.getLong(getGroupIdColumnIndex());
        	Log.d(cTag, "getChildrenCursor for groupId " + groupId);
    		return createChildQuery(groupId);
        }

    }

	public void onListItemSwiped(int position) {
		long packedPosition = getExpandableListView().getExpandableListPosition(position);
		if (isChild(packedPosition)) {
	        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
	        int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
			long id = getExpandableListAdapter().getChildId(groupPosition, childPosition);
			toggleComplete(packedPosition, id);
		}
	}
    
    protected final void toggleComplete(long packedPosition, long id) {
        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
    	Cursor c = (Cursor) getExpandableListAdapter().getChild(groupPosition, childPosition);
    	getListConfig().getChildPersister().toggleTaskComplete(c);
    }
    

    protected Boolean isChildSelected() {
    	long packed = this.getSelectedPosition();
    	return isChild(packed);
    }
    
    protected Boolean isChild(long packedPosition) {
    	int type = ExpandableListView.getPackedPositionType(packedPosition);
    	Boolean isChild = null;
    	switch (type) {
    	case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
    		isChild = Boolean.TRUE;
    		break;
    	case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
    		isChild = Boolean.FALSE;
    	}
    	return isChild;
    }
    
    protected Uri getSelectedContentUri() {
    	Uri selectedUri = null;
    	Boolean childSelected = isChildSelected(); 
    	if (childSelected != null) {
    		selectedUri = childSelected ? getListConfig().getChildPersister().getContentUri() : getListConfig().getGroupPersister().getContentUri();
    	}
    	return selectedUri;
    }



	/**
	 * Return the intent generated when a list item is clicked.
	 * 
	 * @param uri type of data selected
	 */
	protected Intent getClickIntent(Uri uri) {
		return new Intent(Intent.ACTION_EDIT, uri);
	}
	
    /**
     * Permanently delete the selected item.
     */
    protected final void deleteItem() {
    	deleteItem(getSelectedPosition());
    }
    
    protected final void deleteItem(final long packedPosition) {
    	final int type = ExpandableListView.getPackedPositionType(packedPosition);
    	final int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
    	final int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
        final EntityPersister<G> groupPersister = getListConfig().getGroupPersister();
        final TaskPersister childPersister = getListConfig().getChildPersister();
        
    	switch (type) {
	    	case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
	        	Log.d(cTag, "Deleting child at position " + groupPosition + "," + childPosition);
				final long childId = getExpandableListAdapter().getChildId(groupPosition, childPosition);
		    	Log.i(cTag, "Deleting child id " + childId);
		    	childPersister.moveToTrash(Id.create(childId));
		        showItemsDeletedToast(false);
		        refreshChildCount();
		        getExpandableListView().invalidate();
	    		break;
	    		
	    	case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
	        	Log.d(cTag, "Deleting parent at position " + groupPosition);
	        	// first check if there's any children...
	    		int childCount = getExpandableListAdapter().getChildrenCount(groupPosition);
	    		if (childCount > 0) {
		    		OnClickListener buttonListener = new OnClickListener() {
		    			public void onClick(DialogInterface dialog, int which) {
		    				if (which == DialogInterface.BUTTON1) {
		    					final long groupId = getExpandableListAdapter().getGroupId(groupPosition);
		    			    	Log.i(cTag, "Deleting group id " + groupId);
		    			    	groupPersister.moveToTrash(Id.create(groupId));
		    			        showItemsDeletedToast(true);
		    				} else {
		    					Log.d(cTag, "Hit Cancel button. Do nothing.");
		    				}
		    			}
		    		};
	    			AlertUtils.showDeleteGroupWarning(this, getListConfig().getGroupName(this), 
	    					getListConfig().getChildName(this), childCount, buttonListener);    		
	    		} else {
			    	Log.i(cTag, "Deleting childless group at position " + groupPosition);
					final long groupId = getExpandableListAdapter().getGroupId(groupPosition);
			    	Log.i(cTag, "Deleting group id " + groupId);
                    groupPersister.moveToTrash(Id.create(groupId));
			        showItemsDeletedToast(true);
	    		}
	        	break;
    	}
    }

    private final void showItemsDeletedToast(boolean isGroup) {
    	String name = isGroup ? getListConfig().getGroupName(this)
    			: getListConfig().getChildName(this);
    	String text = getResources().getString(
    			R.string.itemDeletedToast, name );
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();        
    	
    }
    
    private final void insertItem(Uri uri, G group) {
    	Intent intent =  new Intent(Intent.ACTION_INSERT, uri);
    	Bundle extras = intent.getExtras();
    	if (extras == null) extras = new Bundle();
    	updateInsertExtras(extras, group);
    	intent.putExtras(extras);
        startActivity(intent);
    }
    
    private final void insertItem(Uri uri) {
        // Launch activity to insert a new item
    	Intent intent =  new Intent(Intent.ACTION_INSERT, uri);
        startActivity(intent);
    }

	abstract protected void updateInsertExtras(Bundle extras, G group);

	abstract void refreshChildCount();

    abstract ExpandableListAdapter createExpandableListAdapter(Cursor cursor);
	
    /**
     * @return a cursor selecting the child items to display for a selected top level group item.
     */
    abstract Cursor createChildQuery(long groupId);
    
    /**
     * @return a cursor selecting the top levels items to display in the list.
     */
    abstract Cursor createGroupQuery();
    
    /**
     * @return index of group id column in group cursor
     */	
    abstract int getGroupIdColumnIndex();
    
    /**
     * @return index of child id column in group cursor
     */	
    abstract int getChildIdColumnIndex();
    
	// custom helper methods
	
	abstract protected ExpandableListConfig<G> getListConfig();
    
}
