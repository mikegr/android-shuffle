package org.dodgybits.shuffle.web.client.service;

import org.dodgybits.shuffle.web.client.model.LoginInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {
  public void login(String requestUri, AsyncCallback<LoginInfo> async);
}
