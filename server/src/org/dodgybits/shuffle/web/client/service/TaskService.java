package org.dodgybits.shuffle.web.client.service;

import java.util.ArrayList;

import org.dodgybits.shuffle.web.client.model.TaskFilter;
import org.dodgybits.shuffle.web.client.model.TaskOrdering;
import org.dodgybits.shuffle.web.client.model.TaskValue;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("task")
public interface TaskService extends RemoteService {

    ArrayList<TaskValue> getMockTasks() throws NotLoggedInException;
    ArrayList<TaskValue> getTasks(TaskFilter filter, TaskOrdering order) throws NotLoggedInException;
    TaskValue saveTask(TaskValue taskValue) throws NotLoggedInException;
	
}
