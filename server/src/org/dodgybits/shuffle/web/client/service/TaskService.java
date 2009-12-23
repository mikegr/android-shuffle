package org.dodgybits.shuffle.web.client.service;

import java.util.ArrayList;

import org.dodgybits.shuffle.web.common.model.Task;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("task")
public interface TaskService extends RemoteService {
	ArrayList<Task> getTasks();
}
