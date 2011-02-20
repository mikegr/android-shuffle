package org.dodgybits.shuffle.android.core.configuration;

import android.content.ContextWrapper;
import com.google.inject.Provides;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.encoding.ContextEncoder;
import org.dodgybits.shuffle.android.core.model.encoding.EntityEncoder;
import org.dodgybits.shuffle.android.core.model.encoding.ProjectEncoder;
import org.dodgybits.shuffle.android.core.model.encoding.TaskEncoder;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import org.dodgybits.shuffle.android.core.model.persistence.DefaultEntityCache;
import org.dodgybits.shuffle.android.core.model.persistence.EntityCache;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.model.persistence.ProjectPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;

import org.dodgybits.shuffle.android.core.model.persistence.selector.Flag;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.list.annotation.*;
import org.dodgybits.shuffle.android.list.config.*;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;
import roboguice.config.AbstractAndroidModule;

import com.google.inject.TypeLiteral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.dodgybits.shuffle.android.core.model.persistence.selector.Flag.no;

public class ShuffleModule extends AbstractAndroidModule {

    @Override
	protected void configure() {
        addCaches();
        addPersisters();
        addEncoders();
        addListPreferenceSettings();
        addListConfig();
	}

    private void addCaches() {
        bind(new TypeLiteral<EntityCache<Context>>() {}).to(new TypeLiteral<DefaultEntityCache<Context>>() {});
        bind(new TypeLiteral<EntityCache<Project>>() {}).to(new TypeLiteral<DefaultEntityCache<Project>>() {});
    }

    private void addPersisters() {
        bind(new TypeLiteral<EntityPersister<Context>>() {}).to(ContextPersister.class);
        bind(new TypeLiteral<EntityPersister<Project>>() {}).to(ProjectPersister.class);
        bind(new TypeLiteral<EntityPersister<Task>>() {}).to(TaskPersister.class);
    }

    private void addEncoders() {
        bind(new TypeLiteral<EntityEncoder<Context>>() {}).to(ContextEncoder.class);
        bind(new TypeLiteral<EntityEncoder<Project>>() {}).to(ProjectEncoder.class);
        bind(new TypeLiteral<EntityEncoder<Task>>() {}).to(TaskEncoder.class);
    }

    private void addListPreferenceSettings() {
        bind(ListPreferenceSettings.class).annotatedWith(Inbox.class).toInstance(
                new ListPreferenceSettings("inbox"));

        bind(ListPreferenceSettings.class).annotatedWith(TopTasks.class).toInstance(
                new ListPreferenceSettings("next_tasks").setDefaultCompleted(Flag.no));

        ListPreferenceSettings projectSettings = new ListPreferenceSettings("project");
        bind(ListPreferenceSettings.class).annotatedWith(ProjectTasks.class).toInstance(projectSettings);
        bind(ListPreferenceSettings.class).annotatedWith(Projects.class).toInstance(projectSettings);
        bind(ListPreferenceSettings.class).annotatedWith(ExpandableProjects.class).toInstance(projectSettings);

        ListPreferenceSettings contextSettings = new ListPreferenceSettings("context");
        bind(ListPreferenceSettings.class).annotatedWith(ContextTasks.class).toInstance(contextSettings);
        bind(ListPreferenceSettings.class).annotatedWith(Contexts.class).toInstance(contextSettings);
        bind(ListPreferenceSettings.class).annotatedWith(ExpandableContexts.class).toInstance(contextSettings);

        bind(ListPreferenceSettings.class).annotatedWith(DueTasks.class).toInstance(
            new ListPreferenceSettings("due_tasks").setDefaultCompleted(Flag.no));

        bind(ListPreferenceSettings.class).annotatedWith(Tickler.class).toInstance(
            new ListPreferenceSettings("tickler").setDefaultCompleted(Flag.no));

    }

    private void addListConfig() {
        bind(DueActionsListConfig.class).annotatedWith(DueTasks.class).to(DueActionsListConfig.class);
        bind(ContextTasksListConfig.class).annotatedWith(ContextTasks.class).to(ContextTasksListConfig.class);
        bind(ProjectTasksListConfig.class).annotatedWith(ProjectTasks.class).to(ProjectTasksListConfig.class);
        bind(ListConfig.class).annotatedWith(Projects.class).to(ProjectListConfig.class);
        bind(ListConfig.class).annotatedWith(Contexts.class).to(ContextListConfig.class);
    }


    @Provides @Inbox
    TaskListConfig providesInboxTaskListConfig(TaskPersister taskPersister, @Inbox ListPreferenceSettings settings) {
		return new AbstractTaskListConfig(
                StandardTaskQueries.getQuery(StandardTaskQueries.cInbox),
                taskPersister, settings) {

		    public int getCurrentViewMenuId() {
		    	return MenuUtils.INBOX_ID;
		    }

		    public String createTitle(ContextWrapper context)
		    {
		    	return context.getString(R.string.title_inbox);
		    }

		};
    }

    @Provides @TopTasks
    TaskListConfig providesTopTasksTaskListConfig(TaskPersister taskPersister, @TopTasks ListPreferenceSettings settings) {
        return new AbstractTaskListConfig(
                StandardTaskQueries.getQuery(StandardTaskQueries.cNextTasks),
                taskPersister, settings) {

		    public int getCurrentViewMenuId() {
		    	return MenuUtils.TOP_TASKS_ID;
		    }

		    public String createTitle(ContextWrapper context)
		    {
		    	return context.getString(R.string.title_next_tasks);
		    }

		};
    }

    @Provides @Tickler
    TaskListConfig providesTicklerTaskListConfig(TaskPersister taskPersister, @Tickler ListPreferenceSettings settings) {
        return new AbstractTaskListConfig(
                StandardTaskQueries.getQuery(StandardTaskQueries.cTickler),
                taskPersister, settings) {

		    public int getCurrentViewMenuId() {
		    	return MenuUtils.INBOX_ID;
		    }

		    public String createTitle(ContextWrapper context)
		    {
		    	return context.getString(R.string.title_tickler);
		    }

		};
    }



}
