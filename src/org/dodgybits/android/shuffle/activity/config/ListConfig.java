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

public interface ListConfig<T> {

    public String createTitle(ContextWrapper context);

	public String getItemName(ContextWrapper context);

	/**
	 * @return id of layout for this view
	 */
	public int getContentViewResId();

	/**
	 * Content type of list itself.
	 */
	public Uri getListContentUri();

	/**
	 * Content type of list items.
	 */
	public Uri getContentUri();

	/**
	 * Generate a model object for the item at the current cursor position.
	 */
	public T readItem(Cursor cursor, Resources res);

	public int getCurrentViewMenuId();
	
	public boolean supportsViewAction();
	
	public boolean isTaskList();
		
}
