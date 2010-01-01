package org.dodgybits.shuffle.web.client.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class KeyValue<T> implements Serializable {
	private String mKey;

	@SuppressWarnings("unused")
	private KeyValue() {
		// required for GWT serialization
	}
	
	public KeyValue(String key) {
		mKey = key;
	}

	public final String getValue() {
		return mKey;
	}
}
