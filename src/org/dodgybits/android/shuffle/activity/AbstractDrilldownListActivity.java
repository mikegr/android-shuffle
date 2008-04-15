package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.util.AlertUtils;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.util.Log;
import android.util.SparseIntArray;

/**
 * A list whose items represent groups that lead to other list.
 * A poor man's ExpandableListActivity. :-)
 */
public abstract class AbstractDrilldownListActivity<T> extends AbstractListActivity<T> {
	private static final String cTag = "AbstractDrilldownListActivity";


	protected SparseIntArray mTaskCountArray;
	
	abstract Uri getChildContentUri();
    
	abstract String getChildName();
	
	abstract void deleteChildren(int groupId);

	protected int getSelectedItemChildCount() {
		int groupId = (int)getSelectedItemId();
		return mTaskCountArray.get(groupId);
	}
	
    /**
     * Permanently delete the selected item.
     */
    protected void deleteItem() {
    	int childCount = getSelectedItemChildCount();
		if (childCount > 0) {
			final int groupId = (int)getSelectedItemId();
    		OnClickListener buttonListener = new OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				if (which == DialogInterface.BUTTON2) {
    			    	Log.i(cTag, "Deleting group id " + groupId);
    			    	AbstractDrilldownListActivity.super.deleteItem();
    			    	Log.i(cTag, "Deleting all child for group id " + groupId);
    			    	deleteChildren(groupId);
    				} else {
    					Log.d(cTag, "Hit Cancel button. Do nothing.");
    				}
    			}
    		};
			AlertUtils.showDeleteGroupWarning(this, getItemName(), getChildName(), childCount, buttonListener);
		} else {
			super.deleteItem();
		}
    }

    @Override
	protected boolean supportsViewAction() {
		return false;
	}

}
