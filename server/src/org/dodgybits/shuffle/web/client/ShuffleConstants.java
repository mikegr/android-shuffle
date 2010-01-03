package org.dodgybits.shuffle.web.client;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;

@DefaultLocale("en") // not required since this is the default
public interface ShuffleConstants extends Constants {
    @DefaultStringValue("Sign In")
    String signIn();

    @DefaultStringValue("Sign Out")
	String signOut();
}
