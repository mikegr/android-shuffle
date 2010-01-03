package org.dodgybits.shuffle.web.client.gin;

import org.dodgybits.shuffle.web.client.formatter.ActionDateFormatter;
import org.dodgybits.shuffle.web.client.model.LoginInfo;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class ShuffleGinModule extends AbstractGinModule {

  protected void configure() {
  	bind(ActionDateFormatter.class).in(Singleton.class);

  	bind(LoginInfo.class).in(Singleton.class);

  	// event hub (TODO hide this from project classes) 
  	bind(HandlerManager.class).in(Singleton.class);
  }

}
