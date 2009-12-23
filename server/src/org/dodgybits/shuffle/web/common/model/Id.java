package org.dodgybits.shuffle.web.common.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Id<T> implements Serializable {
	private long id;

	@SuppressWarnings("unused")
	private Id() {
		// required for GWT serialization
	}
	
	public Id(long id) {
		this.id = id;
	}

	public final long getId() {
		return id;
	}
}
