package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.activity.config.DrilldownListConfig;
import org.dodgybits.android.shuffle.util.AlertUtils;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.util.SparseIntArray;

/**
 * A list whose items represent groups that lead to other list.
 * A poor man's ExpandableListActivity. :-)
 */
public abstract class AbstractDrilldownListActivity<T> extends AbstractListActivity<T> {
	private static final String cTag = "AbstractDrilldownListActivity";

	protected SparseIntArray mTaskCountArray;
	
	protected int getSelectedItemChildCount() {
		int groupId = (int)getSelectedItemId();
		return mTaskCountArray.get(groupId);
	}
	
	protected final DrilldownListConfig<T> getDrilldownListConfig()
	{
		return (DrilldownListConfig<T>)getListConfig();
	}
	
	abstract void deleteChildren(int groupId);
	
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
    			    	AbstractDrilldownListActivity.super.deleteItem(groupId);
    			    	Log.i(cTag, "Deleting all child for group id " + groupId);
    			    	deleteChildren(groupId);
    				} else {
    					Log.d(cTag, "Hit Cancel button. Do nothing.");
    				}
    			}
    		};
			AlertUtils.showDeleteGroupWarning(this, getDrilldownListConfig().getItemName(this), 
					getDrilldownListConfig().getChildName(this), childCount, buttonListener);
		} else {
			super.deleteItem(getSelectedItemId());
		}
    }

}
