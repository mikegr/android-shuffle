package org.dodgybits.shuffle.web.client.service;

import java.util.ArrayList;

import org.dodgybits.shuffle.web.client.model.TaskFilter;
import org.dodgybits.shuffle.web.client.model.TaskOrdering;
import org.dodgybits.shuffle.web.client.model.TaskValue;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TaskServiceAsync {

    void getMockTasks(AsyncCallback<ArrayList<TaskValue>> callback);
    void getTasks(TaskFilter filter, TaskOrdering order, AsyncCallback<ArrayList<TaskValue>> callback);
    void saveTask(TaskValue taskValue, AsyncCallback<TaskValue> callback);
	
}
