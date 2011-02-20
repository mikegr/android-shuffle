package org.dodgybits.shuffle.android.core;

import com.google.inject.Module;
import org.dodgybits.shuffle.android.core.configuration.ShuffleModule;
import roboguice.application.RoboApplication;

import java.util.List;

public class ShuffleApplication extends RoboApplication {

	@Override
	protected void addApplicationModules(List<Module> modules) {
		modules.add(new ShuffleModule());
	}

}
