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

package org.dodgybits.shuffle.android.list.config;

import android.content.ContextWrapper;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;

public interface ExpandableListConfig<G,C> {


    /**
     * @return id of layout for this view
     */
    abstract int getContentViewResId();


    /**
     * Content type of top level list items.
     */
    abstract Uri getGroupContentUri();


    /**
     * Content type of child items.
     */
    abstract Uri getChildContentUri();

    abstract int getCurrentViewMenuId();

    abstract String getGroupName(ContextWrapper context);
    abstract String getChildName(ContextWrapper context);
    
    /**
     * @return the name of the database column holding the key from the child to the parent
     */
    abstract String getGroupIdColumnName();
    /**
     * Generate a model object for the group item at the current cursor position.
     */
    abstract G readGroup(Cursor cursor, Resources res);

    /**
     * Generate a model object for the child item at the current cursor position.
     */
    abstract C readChild(Cursor cursor, Resources res);

    
}
