package org.dodgybits.shuffle.web.client.command;

import java.util.ArrayList;

import org.dodgybits.shuffle.web.client.service.TaskServiceAsync;
import org.dodgybits.shuffle.web.common.model.Task;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class GetTasksCommand implements Command {

	private TaskServiceAsync service;
	private AsyncCallback<ArrayList<Task>> callback;
	
	public GetTasksCommand(
			TaskServiceAsync service,
			AsyncCallback<ArrayList<Task>> callback) {
		this.service = service;
		this.callback = callback;
	}
	
	@Override
	public void execute() {
		service.getTasks(callback);
	}

	public static class Factory {
		private TaskServiceAsync service;
		
		@Inject
		public Factory(TaskServiceAsync service) {
			this.service = service;
		}
		
		public GetTasksCommand create(AsyncCallback<ArrayList<Task>> callback) {
			return new GetTasksCommand(service, callback);
		}
	}
	
}
