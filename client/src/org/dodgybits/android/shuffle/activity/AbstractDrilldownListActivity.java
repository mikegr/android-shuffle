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
	
	protected int getChildCount(long groupId) {
		return mTaskCountArray.get((int)groupId);
	}
	
	protected final DrilldownListConfig<T> getDrilldownListConfig()
	{
		return (DrilldownListConfig<T>)getListConfig();
	}
	
	abstract void deleteChildren(long groupId);
	
    /**
     * Permanently delete the selected item.
     */
	@Override
    protected void deleteItem(final long groupId) {
    	int childCount = getChildCount(groupId);
		if (childCount > 0) {
    		OnClickListener buttonListener = new OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				if (which == DialogInterface.BUTTON1) {
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
			super.deleteItem(groupId);
		}
    }

}
