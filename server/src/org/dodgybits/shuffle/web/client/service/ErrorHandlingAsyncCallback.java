package org.dodgybits.shuffle.web.client.service;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class ErrorHandlingAsyncCallback<T> 
	implements AsyncCallback<T> {

	@Override
	public void onFailure(Throwable caught) {
		Window.alert(caught.getMessage());
	}

}
