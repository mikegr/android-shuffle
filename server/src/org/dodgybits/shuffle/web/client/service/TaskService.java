package org.dodgybits.shuffle.web.client.service;

import java.util.ArrayList;

import org.dodgybits.shuffle.web.client.model.TaskFilter;
import org.dodgybits.shuffle.web.client.model.TaskOrdering;
import org.dodgybits.shuffle.web.client.model.TaskValue;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("task")
public interface TaskService extends RemoteService {

    ArrayList<TaskValue> getMockTasks();
    ArrayList<TaskValue> getTasks(TaskFilter filter, TaskOrdering order);
    TaskValue saveTask(TaskValue taskValue);
	
}
