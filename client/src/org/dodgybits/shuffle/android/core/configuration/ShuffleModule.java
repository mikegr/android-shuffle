package org.dodgybits.shuffle.android.core.configuration;

import android.content.ContextWrapper;
import com.google.inject.Provides;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
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
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.list.annotation.DueTasks;
import org.dodgybits.shuffle.android.list.annotation.Inbox;
import org.dodgybits.shuffle.android.list.annotation.TopTasks;
import org.dodgybits.shuffle.android.list.config.AbstractTaskListConfig;
import org.dodgybits.shuffle.android.list.config.DueActionsListConfig;
import org.dodgybits.shuffle.android.list.config.StandardTaskQueries;
import org.dodgybits.shuffle.android.list.config.TaskListConfig;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;
import roboguice.config.AbstractAndroidModule;

import com.google.inject.TypeLiteral;

public class ShuffleModule extends AbstractAndroidModule {

    @Override
	protected void configure() {
	      bind(new TypeLiteral<EntityCache<Context>>() {}).to(new TypeLiteral<DefaultEntityCache<Context>>() {});
	      bind(new TypeLiteral<EntityCache<Project>>() {}).to(new TypeLiteral<DefaultEntityCache<Project>>() {});

	      bind(new TypeLiteral<EntityPersister<Context>>() {}).to(ContextPersister.class);
	      bind(new TypeLiteral<EntityPersister<Project>>() {}).to(ProjectPersister.class);
	      bind(new TypeLiteral<EntityPersister<Task>>() {}).to(TaskPersister.class);

	      bind(new TypeLiteral<EntityEncoder<Context>>() {}).to(ContextEncoder.class);
	      bind(new TypeLiteral<EntityEncoder<Project>>() {}).to(ProjectEncoder.class);
	      bind(new TypeLiteral<EntityEncoder<Task>>() {}).to(TaskEncoder.class);

        bind(DueActionsListConfig.class).annotatedWith(DueTasks.class).to(DueActionsListConfig.class);
	}

    @Provides @Inbox
    TaskListConfig providesInboxTaskListConfig(TaskPersister taskPersister) {
        ListPreferenceSettings settings = new ListPreferenceSettings("inbox");
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
    TaskListConfig providesTopTasksTaskListConfig(TaskPersister taskPersister) {
        ListPreferenceSettings settings = new ListPreferenceSettings("next_tasks").setDefaultCompleted(Flag.no);

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


}
