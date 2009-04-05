package org.dodgybits.android.shuffle.model;

import android.text.TextUtils;

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

	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof Project) {
			result = TextUtils.equals(((Project)o).name, name );
		}
		return result;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

	
}
