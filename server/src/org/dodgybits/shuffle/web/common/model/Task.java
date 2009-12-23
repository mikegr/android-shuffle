package org.dodgybits.shuffle.web.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings("serial")
public class Task implements Serializable {
	private Id<Task> id;
	private String title;
	private String details;
	private Id<Project> projectId;
	private ArrayList<Id<Context>> contextIds;
	private Date dueDate;
	
	@SuppressWarnings("unused")
	private Task() {
		// required for GWT serialization
	}
	
	public Task(
			Id<Task> id, 
			String title, String details,
			Id<Project> projectId, ArrayList<Id<Context>> contextIds, 
			Date dueDate) {
		super();
		this.id = id;
		this.title = title;
		this.details = details;
		this.projectId = projectId;
		this.contextIds = contextIds;
		this.dueDate = dueDate;
	}

	public final Id<Task> getId() {
		return id;
	}

	public final String getTitle() {
		return title;
	}

	public final String getDetails() {
		return details;
	}

	public final Id<Project> getProjectId() {
		return projectId;
	}

	public final ArrayList<Id<Context>> getContextIds() {
		return contextIds;
	}

	public final Date getDueDate() {
		return dueDate;
	}
	
}
