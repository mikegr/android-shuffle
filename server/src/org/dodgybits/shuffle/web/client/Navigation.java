package org.dodgybits.shuffle.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Navigation extends Composite {

  interface Binder extends UiBinder<Widget, Navigation> { }
  interface Style extends CssResource {
    String item();
  }
  
  private static final Binder binder = GWT.create(Binder.class);
  
  @UiField Style style;
  @UiField Anchor inboxLink;
  @UiField Anchor dueActionsLink;
  @UiField Anchor nextActionsLink;
  @UiField Anchor projectsLink;
  @UiField Anchor contextsLink;
  
  public Navigation() {
    initWidget(binder.createAndBindUi(this));
  }
  
  @UiHandler("inboxLink")
  void onInboxClicked(ClickEvent event) {
    // TODO dispatch event or some such thing
    Window.alert("Loading inbox");
  	
  }

  @UiHandler("projectsLink")
  void onProjectsClicked(ClickEvent event) {
    // TODO dispatch event or some such thing
    Window.alert("Loading projects");
  	
  }

}
