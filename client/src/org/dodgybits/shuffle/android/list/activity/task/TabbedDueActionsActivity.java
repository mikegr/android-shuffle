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
import org.dodgybits.shuffle.android.core.model.TaskQuery;
import org.dodgybits.shuffle.android.core.model.TaskQuery.PredefinedQuery;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.list.config.AbstractTaskListConfig;
import org.dodgybits.shuffle.android.list.config.ListConfig;
import org.dodgybits.shuffle.android.list.config.TaskListConfig;

import com.google.inject.Inject;

import roboguice.inject.InjectView;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TabbedDueActionsActivity extends AbstractTaskListActivity {
	private static final String cTag = "TabbedDueActionsActivity";

	@InjectView(android.R.id.tabhost) TabHost mTabHost;
	private PredefinedQuery mMode = PredefinedQuery.dueToday;
	
	public static final String DUE_MODE = "mode";
	
    @Inject private TaskPersister mTaskPersister;
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mTabHost.setup();
        mTabHost.addTab(createTabSpec(
        			R.string.day_button_title, 
        			PredefinedQuery.dueToday.name(),
        			android.R.drawable.ic_menu_day));
        mTabHost.addTab(createTabSpec(
        			R.string.week_button_title, 
                    PredefinedQuery.dueNextWeek.name(),
        			android.R.drawable.ic_menu_week));
        mTabHost.addTab(createTabSpec(
        			R.string.month_button_title, 
                    PredefinedQuery.dueNextMonth.name(),
        			android.R.drawable.ic_menu_month));
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			public void onTabChanged(String tabId) {
				Log.d(cTag, "Switched to tab: " + tabId);
				if (tabId == null) tabId = PredefinedQuery.dueToday.name();
				mMode = PredefinedQuery.valueOf(tabId);
				updateCursor();
			}
        	
        });
        
        mMode = PredefinedQuery.dueToday;
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(DUE_MODE)) {
            mMode = PredefinedQuery.valueOf(extras.getString(DUE_MODE));
        }
    }
    
    @Override
    protected void onResume() {
        Log.d(cTag, "onResume+");
        super.onResume();
        
        // ugh!! If I take the following out, the tab contents does not display
        int nextTab = mMode.ordinal() % 3;
        int currentTab = mMode.ordinal() - 1;
    	mTabHost.setCurrentTab(nextTab);
    	mTabHost.setCurrentTab(currentTab);
    }
    
    @Override
    protected ListConfig<Task> createListConfig()
	{
		return new AbstractTaskListConfig(createTaskQuery(), mTaskPersister) {

			@Override
			public int getContentViewResId() {
				return R.layout.tabbed_due_tasks;
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
	
	private TaskListConfig getTaskListConfig() {
	    return (TaskListConfig)getListConfig();
	}
    	
	private TaskQuery createTaskQuery() {
        return TaskQuery.newBuilder().setPredefined(mMode).build();
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
    	
    	getTaskListConfig().setTaskQuery(createTaskQuery());
    	Cursor cursor = getListConfig().createQuery(this);
    	adapter.changeCursor(cursor);
    	setTitle(getListConfig().createTitle(this));
	}    
	
	private String getSelectedPeriod() {
		String result = null;
		switch (mMode) {
		case dueToday:
			result = getString(R.string.day_button_title).toLowerCase();
			break;
		case dueNextWeek:
			result = getString(R.string.week_button_title).toLowerCase();
			break;
		case dueNextMonth:
			result = getString(R.string.month_button_title).toLowerCase();
			break;
		}
		return result;
	}
	

}
