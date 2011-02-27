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

import android.os.Bundle;
import org.dodgybits.shuffle.android.core.model.Entity;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.view.AlertUtils;
import org.dodgybits.shuffle.android.list.config.DrilldownListConfig;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.util.SparseIntArray;

/**
 * A list whose items represent groups that lead to other list.
 * A poor man's ExpandableListActivity. :-)
 */
public abstract class AbstractDrilldownListActivity<G extends Entity> extends AbstractListActivity<G> {
	private static final String cTag = "AbstractDrilldownListActivity";

	protected SparseIntArray mTaskCountArray;
	
	protected int getChildCount(Id groupId) {
		return mTaskCountArray.get((int)groupId.getId());
	}
	
    protected final DrilldownListConfig<G> getDrilldownListConfig()
	{
		return (DrilldownListConfig<G>)getListConfig();
	}

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mButtonBar.getAddItemButton().setText(getDrilldownListConfig().getItemName(this));
    }


    @Override
    protected void onResume() {
        super.onResume();

        refreshChildCount();
    }

    /**
     * Mark selected item for delete.
     * Provide warning for items that have children.
     */
	@Override
    protected void toggleDeleted(final G entity) {
        final Id groupId = entity.getLocalId();
    	int childCount = getChildCount(groupId);
		if (childCount > 0 && !entity.isDeleted()) {
    		OnClickListener buttonListener = new OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				if (which == DialogInterface.BUTTON1) {
    			    	Log.i(cTag, "Deleting group id " + groupId);
    			    	AbstractDrilldownListActivity.super.toggleDeleted(entity);
    				} else {
    					Log.d(cTag, "Hit Cancel button. Do nothing.");
    				}
    			}
    		};
			AlertUtils.showDeleteGroupWarning(this, getDrilldownListConfig().getItemName(this), 
					getDrilldownListConfig().getChildName(this), childCount, buttonListener);
		} else {
			super.toggleDeleted(entity);
		}
    }

    abstract void refreshChildCount();

}
