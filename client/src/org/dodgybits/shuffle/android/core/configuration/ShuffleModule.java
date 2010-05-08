package org.dodgybits.shuffle.android.core.configuration;

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
	}

}
