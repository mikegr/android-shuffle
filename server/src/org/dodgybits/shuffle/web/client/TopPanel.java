/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.dodgybits.shuffle.web.client;

import org.dodgybits.shuffle.web.client.model.LoginInfo;
import org.dodgybits.shuffle.web.client.service.LoginServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * The top panel, which contains the 'welcome' message and various links.
 */
public class TopPanel extends Composite {

  interface Binder extends UiBinder<Widget, TopPanel> { }
  private static final Binder binder = GWT.create(Binder.class);

  @Inject ShuffleConstants constants;
  
  @UiField SpanElement emailSpan;
  @UiField Anchor loginLink;
  @UiField Anchor settingsLink;
  
  @Inject
  public TopPanel(LoginServiceAsync loginService, final LoginInfo loginInfo) {
    initWidget(binder.createAndBindUi(this));
    
    loginService.login(getModuleUrl(), new AsyncCallback<LoginInfo>() {
        public void onFailure(Throwable error) {
        }

        public void onSuccess(LoginInfo result) {
          loginInfo.populate(result);
          if(loginInfo.isLoggedIn()) {
              loginLink.setText(constants.signOut());
              loginLink.setHref(loginInfo.getLogoutUrl());
              emailSpan.setInnerText(loginInfo.getEmailAddress());
          } else {
              loginLink.setText(constants.signIn());
              loginLink.setHref(loginInfo.getLoginUrl());
              emailSpan.setInnerText("");
          }
        }
      });
  }
  
  private String getModuleUrl() {
      String url = Location.getPath();
      String gwtCodeSvrParam = Location.getParameter("gwt.codesvr");
      if (!"".equals(gwtCodeSvrParam)) {
        url += "?gwt.codesvr=" + gwtCodeSvrParam;
      }     
      
      return url;
  }

}
