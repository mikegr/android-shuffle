package org.dodgybits.shuffle.android.list.config;

import android.content.ContextWrapper;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.Flag;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.list.annotation.ContextTasks;
import org.dodgybits.shuffle.android.list.annotation.ProjectTasks;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.dodgybits.shuffle.android.core.model.persistence.selector.Flag.no;

public class ProjectTasksListConfig extends AbstractTaskListConfig {
    private Id mProjectId;
    private Project mProject;

    @Inject
    public ProjectTasksListConfig(TaskPersister persister, @ProjectTasks ListPreferenceSettings settings) {
        super(null, persister, settings);
    }


    @Override
    public int getCurrentViewMenuId() {
        return 0;
    }

    @Override
    public String createTitle(ContextWrapper context)
    {
        return context.getString(R.string.title_project_tasks, mProject.getName());
    }

    @Override
    public boolean showTaskProject() {
        return false;
    }

    public void setProjectId(Id projectId) {
        mProjectId = projectId;
        setTaskSelector(createTaskQuery());
    }

    public void setProject(Project project) {
        mProject = project;
    }

    private TaskSelector createTaskQuery() {
        List<Id> ids = Arrays.asList(new Id[]{mProjectId});
        TaskSelector query = TaskSelector.newBuilder()
            .setProjects(ids)
            .setSortOrder(TaskProvider.Tasks.DISPLAY_ORDER + " ASC")
            .build();
        return query;
    }

}
