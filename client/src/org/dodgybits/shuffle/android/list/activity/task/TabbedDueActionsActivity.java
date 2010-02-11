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

package org.dodgybits.shuffle.android.list.activity.task;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.list.config.AbstractTaskListConfig;
import org.dodgybits.shuffle.android.list.config.ListConfig;
import org.dodgybits.shuffle.android.persistence.provider.Shuffle;

import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TabbedDueActionsActivity extends AbstractTaskListActivity {
	private static final String cTag = "TabbedDueActionsActivity";

	private TabHost mTabHost;
	private int mMode = Shuffle.Tasks.DAY_MODE;
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabHost.addTab(createTabSpec(
        			R.string.day_button_title, 
        			String.valueOf(Shuffle.Tasks.DAY_MODE),
        			android.R.drawable.ic_menu_day));
        mTabHost.addTab(createTabSpec(
        			R.string.week_button_title, 
        			String.valueOf(Shuffle.Tasks.WEEK_MODE),
        			android.R.drawable.ic_menu_week));
        mTabHost.addTab(createTabSpec(
        			R.string.month_button_title, 
        			String.valueOf(Shuffle.Tasks.MONTH_MODE),
        			android.R.drawable.ic_menu_month));
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			public void onTabChanged(String tabId) {
				Log.d(cTag, "Switched to tab: " + tabId);
				if (tabId == null) tabId = String.valueOf(Shuffle.Tasks.DAY_MODE);
				mMode = Integer.parseInt(tabId); 
				updateCursor();
			}
        	
        });
    }
    
    @Override
    protected void onResume() {
        Log.d(cTag, "onResume+");
        super.onResume();
        
        // ugh!! If I take the following out, the first tab contents does not display
    	mTabHost.setCurrentTab(1);
    	mTabHost.setCurrentTab(0);
    }
    
	@Override
	protected ListConfig<Task> createListConfig()
	{
		return new AbstractTaskListConfig() {

			@Override
			public int getContentViewResId() {
				return R.layout.tabbed_due_tasks;
			}
			
			public Uri getListContentUri() {
				return Shuffle.Tasks.cDueTasksContentURI.buildUpon().appendPath(
						String.valueOf(mMode)).build();
			}

		    public int getCurrentViewMenuId() {
				return MenuUtils.CALENDAR_ID;
		    }
		    
		    public String createTitle(ContextWrapper context)
		    {
				return context.getString(R.string.title_calendar, getSelectedPeriod());
		    }
			
		};
	}
    	
    private TabSpec createTabSpec(int tabTitleRes, String tagId, int iconId) {
        TabSpec tabSpec = mTabHost.newTabSpec(tagId);
        tabSpec.setContent(R.id.task_list);
        String tabName = getString(tabTitleRes);
        tabSpec.setIndicator(tabName); //, this.getResources().getDrawable(iconId));
        return tabSpec;
    }
    
	private void updateCursor() {
    	SimpleCursorAdapter adapter = (SimpleCursorAdapter)getListAdapter();
    	Cursor oldCursor = adapter.getCursor();
    	if (oldCursor != null) {
    		// changeCursor always closes the cursor, 
    		// so need to stop managing the old one first
    		stopManagingCursor(oldCursor);
    	}
    	
    	Cursor cursor = createItemQuery();
    	adapter.changeCursor(cursor);
    	setTitle(getListConfig().createTitle(this));
	}    
	
	private String getSelectedPeriod() {
		String result = null;
		switch (mMode) {
		case Shuffle.Tasks.DAY_MODE:
			result = getString(R.string.day_button_title).toLowerCase();
			break;
		case Shuffle.Tasks.WEEK_MODE:
			result = getString(R.string.week_button_title).toLowerCase();
			break;
		case Shuffle.Tasks.MONTH_MODE:
			result = getString(R.string.month_button_title).toLowerCase();
			break;
		}
		return result;
	}
	

}
