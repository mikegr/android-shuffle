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
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.list.annotation.Inbox;
import org.dodgybits.shuffle.android.list.config.AbstractTaskListConfig;
import org.dodgybits.shuffle.android.list.config.ListConfig;
import org.dodgybits.shuffle.android.list.config.StandardTaskQueries;

import com.google.inject.Inject;

import android.content.ContextWrapper;
import android.os.Bundle;
import org.dodgybits.shuffle.android.list.config.TaskListConfig;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;

public class TopTasksActivity extends AbstractTaskListActivity {

    @Inject private TaskPersister mTaskPersister;

    @Inject @Inbox
    private TaskListConfig mTaskListConfig;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

	protected ListConfig<Task> createListConfig()
	{
        return mTaskListConfig;
	}

}
