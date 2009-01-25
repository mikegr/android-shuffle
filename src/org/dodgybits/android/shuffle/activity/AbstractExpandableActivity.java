package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.util.AlertUtils;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.util.MenuUtils;

import android.app.ExpandableListActivity;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;

public abstract class AbstractExpandableActivity<G,C> extends ExpandableListActivity{
	private static final String cTag = "AbstractExpandableActivity";

	protected ExpandableListAdapter mAdapter;
    
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(getContentViewResId());
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        
		// Inform the list we provide context menus for items
        getExpandableListView().setOnCreateContextMenuListener(this);
        
        Cursor groupCursor = createGroupQuery();
        // Set up our adapter
        mAdapter = createExpandableListAdapter(groupCursor); 
        setListAdapter(mAdapter);
        animateList();
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		refreshChildCount();
	}
	
	abstract void refreshChildCount();
	
    protected void animateList() {
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(50);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(100);
        set.addAnimation(animation);

        LayoutAnimationController controller =
                new LayoutAnimationController(set, 0.5f);
        ExpandableListView listView = getExpandableListView();        
        listView.setLayoutAnimation(controller);
    }

	
    /**
     * @return id of layout for this view
     */
    abstract int getContentViewResId();


    /**
     * Content type of top level list items.
     */
    abstract Uri getGroupContentUri();

    /**
     * @return index of group id column in group cursor
     */	
    abstract int getGroupIdColumnIndex();

    /**
     * Content type of child items.
     */
    abstract Uri getChildContentUri();

    /**
     * @return index of child id column in group cursor
     */	
    abstract int getChildIdColumnIndex();

    
    /**
     * @return a cursor selecting the top levels items to display in the list.
     */
    abstract Cursor createGroupQuery();
    
    abstract ExpandableListAdapter createExpandableListAdapter(Cursor cursor);

    abstract int getCurrentViewMenuId();

    abstract String getGroupName();
    abstract String getChildName();
    
    /**
     * @return the name of the database column holding the key from the child to the parent
     */
    abstract String getGroupIdColumnName();
    /**
     * Generate a model object for the group item at the current cursor position.
     */
    abstract G readGroup(Cursor cursor);

    /**
     * Generate a model object for the child item at the current cursor position.
     */
    abstract C readChild(Cursor cursor);

    /**
     * @return a cursor selecting the child items to display for a selected top level group item.
     */
    abstract Cursor createChildQuery(long groupId);

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

    protected final void toggleComplete(long packedPosition, long id) {
        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
    	Cursor c = (Cursor) getExpandableListAdapter().getChild(groupPosition, childPosition);
        BindingUtils.toggleTaskComplete(this, c, getChildContentUri(), id);
    }
    

    /**
     * @return Number of items in the list.
     */
    protected final int getItemCount() {
    	return getExpandableListAdapter().getGroupCount();
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
    		selectedUri = childSelected ? getChildContentUri() : getGroupContentUri();
    	}
    	return selectedUri;
    }

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	switch (event.getKeyCode()) {
    	case KeyEvent.KEYCODE_N:
    		// go to previous view
    		int prevView = getCurrentViewMenuId() - 1;
    		if (prevView < MenuUtils.INBOX_ID) {
    			prevView = MenuUtils.CONTEXT_ID;
    		}
    		MenuUtils.checkCommonItemsSelected(prevView, this, getCurrentViewMenuId());
    		return true;
		case KeyEvent.KEYCODE_M:
			// go to previous view
			int nextView = getCurrentViewMenuId() + 1;
			if (nextView > MenuUtils.CONTEXT_ID) {
				nextView = MenuUtils.INBOX_ID;
			}
			MenuUtils.checkCommonItemsSelected(nextView, this, getCurrentViewMenuId());
			return true;
    	}
		return super.onKeyDown(keyCode, event);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuUtils.addExpandableInsertMenuItems(menu, getGroupName(), getChildName(), this);
        MenuUtils.addAlternativeMenuItems(menu, getChildContentUri(), this);
        MenuUtils.addAlternativeMenuItems(menu, getGroupContentUri(), this);
        MenuUtils.addViewMenuItems(menu, getCurrentViewMenuId());
        MenuUtils.addPrefsHelpMenuItems(menu);
        
        return true;
    }
        
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case MenuUtils.INSERT_CHILD_ID:
	            insertItem(getChildContentUri());
	            return true;
	        case MenuUtils.INSIDE_GROUP_ID:
	            insertItem(getGroupContentUri());
	            return true;
        }
        if (MenuUtils.checkCommonItemsSelected(item, this, getCurrentViewMenuId())) return true;
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
            Uri selectedUri = ContentUris.withAppendedId(getChildContentUri(), childId);
            MenuUtils.addSelectedAlternativeMenuItems(menu, selectedUri, this, false);
        	MenuUtils.addCompleteMenuItem(menu);
        }
        else
        {
        	long groupId = getExpandableListAdapter().getGroupId(groupPosition);
            Uri selectedUri = ContentUris.withAppendedId(getGroupContentUri(), groupId);
            MenuUtils.addSelectedAlternativeMenuItems(menu, selectedUri, this, false);
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

	        case MenuUtils.DELETE_ID: {
	                // Delete the item that the context menu is for
	    			deleteItem(info.packedPosition);
	                return true;
	            }
        }
        return false;
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
    	switch (type) {
	    	case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
	        	Log.d(cTag, "Deleting child at position " + groupPosition + "," + childPosition);
				final long childId = getSelectedId();
		    	Log.i(cTag, "Deleting child id " + childId);
				Uri childUri = ContentUris.withAppendedId(getChildContentUri(), childId);			
		        getContentResolver().delete(childUri, null, null);		    
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
		    					Uri uri = ContentUris.withAppendedId(getGroupContentUri(), groupId);			
		    			        getContentResolver().delete(uri, null, null);
		    			    	Log.i(cTag, "Deleting all child for group id " + groupId);
		    					getContentResolver().delete(getChildContentUri(), getGroupIdColumnName() + " = ?", new String[] {String.valueOf(groupId)});
		    				} else {
		    					Log.d(cTag, "Hit Cancel button. Do nothing.");
		    				}
		    			}
		    		};
	    			AlertUtils.showDeleteGroupWarning(this, getGroupName(), getChildName(), childCount, buttonListener);    		
	    		} else {
			    	Log.i(cTag, "Deleting childless group at position " + groupPosition);
					final long groupId = getSelectedId();
			    	Log.i(cTag, "Deleting group id " + groupId);
					Uri groupUri = ContentUris.withAppendedId(getGroupContentUri(), groupId);			
			        getContentResolver().delete(groupUri, null, null);		    	
	    		}
	        	break;
    	}
    }

    
    private final void insertItem(Uri uri) {
        // Launch activity to insert a new item
    	Intent intent =  new Intent(Intent.ACTION_INSERT, uri);
        startActivity(intent);
    }

}
