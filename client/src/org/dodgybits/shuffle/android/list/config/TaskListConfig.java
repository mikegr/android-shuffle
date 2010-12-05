package org.dodgybits.shuffle.android.list.config;

import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;

public interface TaskListConfig extends ListConfig<Task> {

    TaskPersister getTaskPersister();
    
    TaskSelector getTaskQuery();
    void setTaskQuery(TaskSelector query);
}
