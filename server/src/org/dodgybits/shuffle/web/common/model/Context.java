package org.dodgybits.shuffle.web.common.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Context implements Serializable {
	private Id<Context> id;
	private String name;
	
	@SuppressWarnings("unused")
	private Context() {
		// required for GWT serialization
	}
	
	public Context(Id<Context> id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public final Id<Context> getId() {
		return id;
	}

	public final String getName() {
		return name;
	}

}
