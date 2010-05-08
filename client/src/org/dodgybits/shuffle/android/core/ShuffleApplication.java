package org.dodgybits.shuffle.android.core;

import java.util.List;

import org.dodgybits.shuffle.android.core.configuration.ShuffleModule;

import roboguice.application.GuiceApplication;

import com.google.inject.Module;

public class ShuffleApplication extends GuiceApplication {

	@Override
	protected void addApplicationModules(List<Module> modules) {
		modules.add(new ShuffleModule());
	}

}
