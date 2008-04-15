package org.dodgybits.android.shuffle.model;

public class Project {
	public Integer id;
	public final String name;
	public final Integer defaultContextId;
	public final boolean archived;
	
	public Project(Integer id, String name, Integer defaultContextId, boolean archived) {
		this.id = id;
		this.name = name;
		this.defaultContextId = defaultContextId;
		this.archived = archived;
	}
	
	public Project(String name, Integer defaultContextId, boolean archived) {
		this(null, name, defaultContextId, archived);
	}
		
}
