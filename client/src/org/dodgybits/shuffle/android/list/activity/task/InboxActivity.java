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
import org.dodgybits.android.shuffle.activity.config.AbstractTaskListConfig;
import org.dodgybits.android.shuffle.activity.config.ListConfig;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.persistence.provider.Shuffle;
import org.dodgybits.shuffle.android.preference.model.Preferences;

import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class InboxActivity extends AbstractTaskListActivity {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		mOtherButton.setText(R.string.clean_inbox_button_title);
		Drawable cleanIcon = getResources().getDrawable(R.drawable.edit_clear);
		cleanIcon.setBounds(0, 0, 24, 24);
		mOtherButton.setCompoundDrawables(cleanIcon, null, null, null);
		mOtherButton.setVisibility(View.VISIBLE);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuUtils.addCleanInboxMenuItem(menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MenuUtils.CLEAN_INBOX_ID:
        	doCleanup();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected ListConfig<Task> createListConfig()
	{
		return new AbstractTaskListConfig() {

			public Uri getListContentUri() {
				// Tasks with no projects or created since last clean
				return Shuffle.Tasks.cInboxTasksContentURI;
			}

		    public int getCurrentViewMenuId() {
		    	return MenuUtils.INBOX_ID;
		    }
		    
		    public String createTitle(ContextWrapper context)
		    {
		    	return context.getString(R.string.title_inbox);
		    }
			
		};
	}
	    
	@Override
	protected void onOtherButtonClicked() {
		doCleanup();
	}
	
	private void doCleanup() {
    	Preferences.cleanUpInbox(this);
        Toast.makeText(this, R.string.clean_inbox_message, Toast.LENGTH_SHORT).show();
    	// need to restart the activity since the query has changed
    	// mCursor.requery() not enough
    	startActivity(new Intent(this, InboxActivity.class));
		finish();
	}
}
