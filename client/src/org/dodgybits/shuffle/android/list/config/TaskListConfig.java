package org.dodgybits.shuffle.android.list.config;

import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;

public interface TaskListConfig extends ListConfig<Task> {

    TaskPersister getTaskPersister();
    
    TaskSelector getTaskSelector();
    void setTaskSelector(TaskSelector query);

    boolean showTaskContext();
    boolean showTaskProject();


}
