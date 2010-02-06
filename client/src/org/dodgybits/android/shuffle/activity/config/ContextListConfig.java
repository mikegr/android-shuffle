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

package org.dodgybits.android.shuffle.activity.config;

import android.content.ContextWrapper;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.persistence.provider.Shuffle;

public class ContextListConfig implements DrilldownListConfig<Context> {

	public String createTitle(ContextWrapper context) {
    	return context.getString(R.string.title_context);
	}

	public Uri getContentUri() {
		return Shuffle.Contexts.CONTENT_URI;
	}

	public int getContentViewResId() {
		return R.layout.contexts;
	}

	public int getCurrentViewMenuId() {
    	return MenuUtils.CONTEXT_ID;
	}

	public String getItemName(ContextWrapper context) {
		return context.getString(R.string.context_name);
	}

	public Uri getListContentUri() {
		return Shuffle.Contexts.CONTENT_URI;
	}

	public boolean isTaskList() {
		return false;
	}

	public Context readItem(Cursor cursor, Resources res) {
        return BindingUtils.readContext(cursor, res);
	}

	public boolean supportsViewAction() {
		return false;
	}
	
	public Uri getChildContentUri() {
		return Shuffle.Tasks.CONTENT_URI;
	}
	
	public String getChildName(ContextWrapper context) {
		return context.getString(R.string.task_name);
	}		

}
