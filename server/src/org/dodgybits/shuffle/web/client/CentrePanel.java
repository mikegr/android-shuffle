package org.dodgybits.shuffle.web.client;


import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.inject.Inject;

public class CentrePanel extends ResizeComposite {
  
  TaskList taskList;
  
  @Inject
  public CentrePanel(TaskList taskList) {
  	this.taskList = taskList;
  	
    initWidget(taskList);
  }

}
