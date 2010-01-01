package org.dodgybits.shuffle.web.client;

import java.util.ArrayList;
import java.util.Date;

import org.dodgybits.shuffle.web.client.command.GetTasksCommand;
import org.dodgybits.shuffle.web.client.command.SaveTaskCommand;
import org.dodgybits.shuffle.web.client.formatter.ActionDateFormatter;
import org.dodgybits.shuffle.web.client.model.TaskValue;
import org.dodgybits.shuffle.web.client.service.ErrorHandlingAsyncCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.inject.Inject;

/**
 * A composite that displays a list of actions.
 */
public class TaskList extends ResizeComposite implements ClickHandler {

    interface Binder extends UiBinder<Widget, TaskList> {
    }

    interface SelectionStyle extends CssResource {
        String selectedRow();
    }

    private static final Binder sBinder = GWT.create(Binder.class);

    private ActionDateFormatter mFormatter;
    private GetTasksCommand.Factory mGetTasksFactory;
    private SaveTaskCommand.Factory mSaveTaskFactory;

    private ArrayList<TaskValue> taskValues;

    @UiField
    FlowPanel header;
    @UiField
    FlowPanel footer;
    @UiField
    FlexTable table;
    @UiField
    SelectionStyle selectionStyle;

    @Inject
    public TaskList(GetTasksCommand.Factory factory,
            SaveTaskCommand.Factory saveTaskFactory,
            ActionDateFormatter formatter) {
        mGetTasksFactory = factory;
        mSaveTaskFactory = saveTaskFactory;
        mFormatter = formatter;

        initWidget(sBinder.createAndBindUi(this));
        initTable();
    }

    private void initTable() {
        // Initialize the table.
        table.getColumnFormatter().setWidth(1, "10em");
        table.addClickHandler(this);

        fetchActions();
    }

    private void fetchActions() {
        GetTasksCommand command = mGetTasksFactory
                .create(new ErrorHandlingAsyncCallback<ArrayList<TaskValue>>() {
                    @Override
                    public void onSuccess(ArrayList<TaskValue> result) {
                        taskValues = result;
                        displayActions();
                    }
                });
        command.execute();
    }
    
    private void saveAction(final int taskValueIndex) {
        TaskValue task = taskValues.get(taskValueIndex);
        SaveTaskCommand command = mSaveTaskFactory
            .create(task,
                    new ErrorHandlingAsyncCallback<TaskValue>() {
            @Override
            public void onSuccess(TaskValue result) {
                taskValues.set(taskValueIndex, result);
            }
        });
        command.execute();
    }

    private void displayActions() {
        int numActions = taskValues.size();
        for (int i = 0; i < numActions; i++) {
            TaskValue taskValue = taskValues.get(i);
            displayAction(taskValue, i);
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        Cell clickedCell = table.getCellForEvent(event); 
        int rowIndex = clickedCell.getRowIndex(); 
        saveAction(rowIndex);
    }
    
    
    private void displayAction(TaskValue taskValue, int row) {
        String description = "<div class='actionTitle'>"
                + escapeHtml(taskValue.getTitle())
                + "<span class='actionDetails'> - "
                + escapeHtml(taskValue.getDetails()) + "</span></div>";
        table.setHTML(row, 0, description);

        table.setText(row, 1, mFormatter.getShortDueDate(taskValue));
        table.getCellFormatter().setStyleName(
                row,
                1,
                isInPast(taskValue.getDueDate()) ? "actionDueInPass"
                        : "actionDueInFuture");
    }

    private static String escapeHtml(String maybeHtml) {
        final Element div = DOM.createDiv();
        DOM.setInnerText(div, maybeHtml);
        return DOM.getInnerHTML(div);
    }

    private static boolean isInPast(Date date) {
        return date != null && date.getTime() < System.currentTimeMillis();
    }


}
