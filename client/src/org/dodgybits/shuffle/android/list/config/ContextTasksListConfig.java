package org.dodgybits.shuffle.android.list.config;

import android.content.ContextWrapper;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.list.annotation.ContextTasks;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.dodgybits.shuffle.android.core.model.persistence.selector.Flag.no;

public class ContextTasksListConfig extends AbstractTaskListConfig {
    private Id mContextId;
    private Context mContext;

    @Inject
    public ContextTasksListConfig(TaskPersister persister, @ContextTasks ListPreferenceSettings settings) {
        super(null, persister, settings);
    }


    @Override
    public int getCurrentViewMenuId() {
        return 0;
    }

    @Override
    public String createTitle(ContextWrapper context)
    {
        return context.getString(R.string.title_context_tasks, mContext.getName());
    }

    @Override
    public boolean showTaskContext() {
        return false;
    }

    public void setContextId(Id contextId) {
        mContextId = contextId;
        setTaskSelector(createTaskQuery());
    }

    public void setContext(Context context) {
        mContext = context;
    }

    private TaskSelector createTaskQuery() {
        List<Id> ids = Arrays.asList(new Id[]{mContextId});
        TaskSelector query = TaskSelector.newBuilder()
            .setContexts(ids)
            .setSortOrder(TaskProvider.Tasks.CREATED_DATE + " ASC")
            .build();
        return query;
    }

}
