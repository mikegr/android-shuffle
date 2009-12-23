package org.dodgybits.shuffle.web.common.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Project implements Serializable {
	private Id<Project> id;
	private String name;
	private Id<Context> defaultContextId;
	
	@SuppressWarnings("unused")
	private Project() {
		// required for GWT serialization
	}
	
	public Project(Id<Project> id, String name, Id<Context> defaultContextId) {
		this.id = id;
		this.name = name;
		this.defaultContextId = defaultContextId;
	}

	public final Id<Project> getId() {
		return id;
	}

	public final String getName() {
		return name;
	}

	public final Id<Context> getDefaultContextId() {
		return defaultContextId;
	}
	
}
