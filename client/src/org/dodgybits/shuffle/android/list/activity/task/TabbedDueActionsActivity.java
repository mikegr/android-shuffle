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
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.Flag;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector.PredefinedQuery;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.list.annotation.DueTasks;
import org.dodgybits.shuffle.android.list.config.AbstractTaskListConfig;
import org.dodgybits.shuffle.android.list.config.DueActionsListConfig;
import org.dodgybits.shuffle.android.list.config.ListConfig;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;

import roboguice.inject.InjectView;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.google.inject.Inject;

public class TabbedDueActionsActivity extends AbstractTaskListActivity {
	private static final String cTag = "TabbedDueActionsActivity";

	@InjectView(android.R.id.tabhost) TabHost mTabHost;

    @Inject @DueTasks
    private DueActionsListConfig mListConfig;


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
				mListConfig.setMode(PredefinedQuery.valueOf(tabId));
				updateCursor();
			}
        	
        });
        
        mListConfig.setMode(PredefinedQuery.dueToday);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(DUE_MODE)) {
            mListConfig.setMode(PredefinedQuery.valueOf(extras.getString(DUE_MODE)));
        }
    }
    
    @Override
    protected void onResume() {
        Log.d(cTag, "onResume+");
        super.onResume();
        
        // ugh!! If I take the following out, the tab contents does not display
        int nextTab = mListConfig.getMode().ordinal() % 3;
        int currentTab = mListConfig.getMode().ordinal() - 1;
    	mTabHost.setCurrentTab(nextTab);
    	mTabHost.setCurrentTab(currentTab);
    }
    
    @Override
    protected ListConfig<Task> createListConfig()
	{
		return mListConfig;
	}

    private TabSpec createTabSpec(int tabTitleRes, String tagId, int iconId) {
        TabSpec tabSpec = mTabHost.newTabSpec(tagId);
        tabSpec.setContent(R.id.task_list);
        String tabName = getString(tabTitleRes);
        tabSpec.setIndicator(tabName); //, this.getResources().getDrawable(iconId));
        return tabSpec;
    }

}
