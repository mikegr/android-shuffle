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

import org.dodgybits.shuffle.android.core.model.Entity;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;

import android.content.ContextWrapper;

public interface ExpandableListConfig<G extends Entity> {


    /**
     * @return id of layout for this view
     */
    int getContentViewResId();
    int getCurrentViewMenuId();

    String getGroupName(ContextWrapper context);
    String getChildName(ContextWrapper context);
    
    /**
     * @return the name of the database column holding the key from the child to the parent
     */
    String getGroupIdColumnName();
    
    EntityPersister<G> getGroupPersister();
    TaskPersister getChildPersister();

    
}
