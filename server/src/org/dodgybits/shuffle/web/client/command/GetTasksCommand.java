package org.dodgybits.shuffle.web.client.command;

import java.util.ArrayList;

import org.dodgybits.shuffle.web.client.model.TaskValue;
import org.dodgybits.shuffle.web.client.service.TaskServiceAsync;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class GetTasksCommand implements Command {

	private TaskServiceAsync mService;
	private AsyncCallback<ArrayList<TaskValue>> mCallback;
	
	public GetTasksCommand(
			TaskServiceAsync service,
			AsyncCallback<ArrayList<TaskValue>> callback) {
		mService = service;
		mCallback = callback;
	}
	
	@Override
	public void execute() {
//		service.getMockTasks(callback);
	    mService.getTasks(null, null, mCallback);
	}

	public static class Factory {
		private TaskServiceAsync service;
		
		@Inject
		public Factory(TaskServiceAsync service) {
			this.service = service;
		}
		
		public GetTasksCommand create(AsyncCallback<ArrayList<TaskValue>> callback) {
			return new GetTasksCommand(service, callback);
		}
	}
	
}
