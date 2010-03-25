package org.dodgybits.shuffle.android.list.config;

import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.TaskQuery;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;

public interface TaskListConfig extends ListConfig<Task> {

    TaskPersister getTaskPersister();
    
    TaskQuery getTaskQuery();
    void setTaskQuery(TaskQuery query);
}
