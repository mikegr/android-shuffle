package org.dodgybits.shuffle.web.client;

import java.util.ArrayList;
import java.util.Date;

import org.dodgybits.shuffle.web.client.command.GetTasksCommand;
import org.dodgybits.shuffle.web.client.formatter.ActionDateFormatter;
import org.dodgybits.shuffle.web.client.service.ErrorHandlingAsyncCallback;
import org.dodgybits.shuffle.web.common.model.Task;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * A composite that displays a list of actions.
 */
public class TaskList extends ResizeComposite {

  interface Binder extends UiBinder<Widget, TaskList> { }
  interface SelectionStyle extends CssResource {
    String selectedRow();
  }

  private static final Binder binder = GWT.create(Binder.class);

  private ActionDateFormatter formatter;
  private GetTasksCommand.Factory factory;

  private ArrayList<Task> tasks;
  
  @UiField FlowPanel header;
  @UiField FlowPanel footer;
  @UiField FlexTable table;
  @UiField SelectionStyle selectionStyle;
  
  @Inject
  public TaskList(
  		GetTasksCommand.Factory factory, 
  		ActionDateFormatter formatter) {
  	this.factory = factory;
  	this.formatter = formatter;
  	
    initWidget(binder.createAndBindUi(this));
    initTable();
  }
  
  private void initTable() {
    // Initialize the table.
    table.getColumnFormatter().setWidth(1, "10em");
    
    fetchActions();
  }
  
  private void fetchActions() {
  	GetTasksCommand command = factory.create(
  			new ErrorHandlingAsyncCallback<ArrayList<Task>>() {
			@Override
			public void onSuccess(ArrayList<Task> result) {
				tasks = result;
				displayActions();
			}
		});
  	command.execute();
  }
  
  private void displayActions() {
    int numActions = tasks.size();
    for(int i = 0; i < numActions; i++) {
    	Task task = tasks.get(i);
    	displayAction(task, i);
    }
  }
  
  private void displayAction(Task task, int row) {
  	String description = "<div class='actionTitle'>" + 
			escapeHtml(task.getTitle()) +
			"<span class='actionDetails'> - " + 
			escapeHtml(task.getDetails()) +
			"</span></div>";
	  	table.setHTML(row, 0, description);
  	
  	table.setText(row, 1, formatter.getShortDueDate(task));
  	table.getCellFormatter().setStyleName(row, 1, 
  			isInPast(task.getDueDate()) 
  			? "actionDueInPass" 
  			: "actionDueInFuture" );
  }
  
  private static String escapeHtml(String maybeHtml) {
    final Element div = DOM.createDiv();
    DOM.setInnerText(div, maybeHtml);
    return DOM.getInnerHTML(div);
  }
  
  private static boolean isInPast(Date date) {
  	return date != null && 
  		date.getTime() < System.currentTimeMillis();
  }
  

}
