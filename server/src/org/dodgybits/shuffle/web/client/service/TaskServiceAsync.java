package org.dodgybits.shuffle.web.client.service;

import java.util.ArrayList;

import org.dodgybits.shuffle.web.common.model.Task;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TaskServiceAsync {
	void getTasks(AsyncCallback<ArrayList<Task>> callback);
	
}
