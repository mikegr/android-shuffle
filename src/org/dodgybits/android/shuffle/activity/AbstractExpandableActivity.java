package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.util.AlertUtils;
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
import android.view.KeyEvent;
import android.view.Menu;
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
        setDefaultKeyMode(SHORTCUT_DEFAULT_KEYS);
        Cursor groupCursor = createGroupQuery();
        // Set up our adapter
        mAdapter = createExpandableListAdapter(groupCursor); 
        setListAdapter(mAdapter);
        animateList();
	}
	
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
        
        @Override
        public long getGroupId(int groupPosition) {
        	Log.d(cTag, "getGroupId " + groupPosition);
        	Cursor groupCursor = (Cursor) getGroup(groupPosition);
        	return groupCursor.getLong(getGroupIdColumnIndex());
        }
        
        @Override
        public long getChildId(int groupPosition, int childPosition) {
        	Log.d(cTag, "getChildId " + groupPosition + "," + childPosition);
        	Cursor childCursor = (Cursor) getChild(groupPosition, childPosition);
        	return childCursor.getLong(getChildIdColumnIndex());
        }

    }


    /**
     * @return Number of items in the list.
     */
    protected final int getItemCount() {
    	return getExpandableListAdapter().getGroupCount();
    }

    protected Boolean isChildSelected() {
    	long packed = this.getSelectedPosition();
    	int type = ExpandableListView.getPackedPositionType(packed);
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
    
    /**
     * Overriding, since parent version is buggy.
     * @see http://code.google.com/p/android/issues/detail?id=308
     */
    @Override 
    public long getSelectedId() {
    	long packedPosition = getSelectedPosition();
    	int type = ExpandableListView.getPackedPositionType(packedPosition);
    	int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
    	int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
    	long result = -1;
    	switch (type) {
    	case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
        	Log.d(cTag, "Getting child id for position " + groupPosition + "," + childPosition);
    		result =  5;//mAdapter.getChildId(groupPosition, childPosition);
    		break;
    	case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
    		Log.d(cTag, "Getting group id for position " + groupPosition);
    		result = mAdapter.getGroupId(groupPosition);
        	break;
    	}
    	return result;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final boolean haveItems = getItemCount() > 0;

        // If there are any items in the list (which implies that one of
        // them is selected), then we need to generate the actions that
        // can be performed on the current selection.  This will be a combination
        // of our own specific actions along with any extensions that can be
        // found.
        
    	Uri selectedContentUri = getSelectedContentUri();
    	// while bug 308 exists, don't add menu items for child selections
    	Boolean childSelected = isChildSelected();
        if (haveItems && childSelected != null && !childSelected && (selectedContentUri != null)) {
        	long selectedId = getSelectedId();
            Uri selectedUri = ContentUris.withAppendedId(selectedContentUri, selectedId);
            MenuUtils.addSelectedAlternativeMenuItems(menu, selectedUri, this, false);
            // ... and ends with the delete command.
            MenuUtils.addDeleteMenuItem(menu);
        } else {
            menu.removeGroup(Menu.SELECTED_ALTERNATIVE);
        }

        // Make sure the delete action is disabled if there are no items.
        menu.setItemShown(MenuUtils.DELETE_ID, haveItems);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(Menu.Item item) {
        switch (item.getId()) {
	        case MenuUtils.DELETE_ID:
	            deleteItem();
	            return true;
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

    /**
     * Permanently delete the selected item.
     */
    protected final void deleteItem() {
    	final long packedPosition = getSelectedPosition();
    	final int type = ExpandableListView.getPackedPositionType(packedPosition);
    	final int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
    	final int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
    	switch (type) {
    	case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
        	Log.d(cTag, "Deleting child at position " + groupPosition + "," + childPosition);
    		// TODO do delete
        	// Can't delete due to Android issue 308
//        	Cursor childCursor = (Cursor) getExpandableListAdapter().getChild(groupPosition, childPosition);
//        	childCursor.deleteRow();
    		break;
    	case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
        	Log.d(cTag, "Deleting parent at position " + groupPosition);
	    	// Following loop is an attempt to alleviate Android issue 553
	    	int groupCount = getExpandableListAdapter().getGroupCount();
	    	for (int i = groupCount - 1; i >= 0 ; i--) {
	    		Log.d(cTag, "Collapsing group pos " + i);
	    		getExpandableListView().collapseGroup(i);
	    	}
        	// first check if there's any children...
    		int childCount = getExpandableListAdapter().getChildrenCount(groupPosition);
    		if (childCount > 0) {
	    		OnClickListener buttonListener = new OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    				if (which == DialogInterface.BUTTON2) {
	    					final long groupId = getSelectedId();
	    			    	Log.i(cTag, "Deleting group id " + groupId);
	    		    		final Cursor groupCursor = (Cursor) getExpandableListAdapter().getGroup(groupPosition);
	    		    		groupCursor.deleteRow();
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
	    		Cursor groupCursor = (Cursor) getExpandableListAdapter().getGroup(groupPosition);
	    		groupCursor.deleteRow();
    		}
        	break;
    	}
    }

    
    private final void insertItem(Uri uri) {
        // Launch activity to insert a new item
    	Intent intent =  new Intent(Intent.INSERT_ACTION, uri);
        startActivity(intent);
    }

}
