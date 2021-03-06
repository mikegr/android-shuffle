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

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.list.activity.task.ContextTasksActivity;
import org.dodgybits.shuffle.android.list.config.ContextListConfig;
import org.dodgybits.shuffle.android.list.config.ListConfig;
import org.dodgybits.shuffle.android.list.view.ContextView;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import com.google.inject.Inject;

/**
 * Display list of contexts with task children.
 */
public class ContextsActivity extends AbstractDrilldownListActivity<Context> {
	@Inject ContextListConfig mListConfig;

    @Override
    protected void refreshChildCount() {
        TaskSelector selector = TaskSelector.newBuilder()
                .applyListPreferences(this, getListConfig().getListPreferenceSettings())
                .build();

        Cursor cursor = getContentResolver().query(
                ContextProvider.Contexts.CONTEXT_TASKS_CONTENT_URI,
                ContextProvider.Contexts.FULL_TASK_PROJECTION,
                selector.getSelection(this),
                selector.getSelectionArgs(),
                selector.getSortOrder());
        mTaskCountArray = getDrilldownListConfig().getChildPersister().readCountArray(cursor);
        cursor.close();
    }

	@Override
	protected ListConfig<Context> createListConfig() {
	    return mListConfig;
	}
	
	@Override
	protected ListAdapter createListAdapter(Cursor cursor) {
		ListAdapter adapter =
				new SimpleCursorAdapter(this,
						android.R.layout.simple_list_item_1, cursor,
						new String[] { ContextProvider.Contexts.NAME },
						new int[] { android.R.id.text1 }) {
			
			public View getView(int position, View convertView, ViewGroup parent) {
				Cursor cursor = (Cursor)getItem(position);
				Context context = getListConfig().getPersister().read(cursor);
				ContextView contextView;
				if (convertView instanceof ContextView) {
					contextView = (ContextView) convertView;
				} else {
					contextView = new ContextView(parent.getContext()) {
						protected int getViewResourceId() {
							return R.layout.list_context_view;
						}
						
					};
				}
				contextView.setTaskCountArray(mTaskCountArray);
				contextView.updateView(context);
				return contextView;
			}

		};
		return adapter;
	}
	
    /**
     * Return the intent generated when a list item is clicked.
     * 
     * @param uri type of data selected
     */ 
	@Override
    protected Intent getClickIntent(Uri uri) {
    	// if a context is clicked on, show tasks for that context.
    	Intent intent = new Intent(this, ContextTasksActivity.class);
    	intent.setData(uri);
    	return intent;
    }

}