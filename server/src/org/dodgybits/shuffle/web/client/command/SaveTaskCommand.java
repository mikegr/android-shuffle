package org.dodgybits.shuffle.web.client.command;

import org.dodgybits.shuffle.web.client.model.TaskValue;
import org.dodgybits.shuffle.web.client.service.TaskServiceAsync;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class SaveTaskCommand implements Command {

    private TaskServiceAsync mService;
    private AsyncCallback<TaskValue> mCallback;
    private TaskValue mTaskValue;
    
    public SaveTaskCommand(
            TaskValue taskValue,
            TaskServiceAsync service,
            AsyncCallback<TaskValue> callback) {
        mTaskValue = taskValue;
        mService = service;
        mCallback = callback;
    }
    
    @Override
    public void execute() {
        mService.saveTask(mTaskValue, mCallback);
    }

    public static class Factory {
        private TaskServiceAsync service;
        
        @Inject
        public Factory(TaskServiceAsync service) {
            this.service = service;
        }
        
        public SaveTaskCommand create(TaskValue taskValue, AsyncCallback<TaskValue> callback) {
            return new SaveTaskCommand(taskValue, service, callback);
        }
    }
    
}
