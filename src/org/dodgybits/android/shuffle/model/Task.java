/*
 * Copyright (C) 2009 Android Shuffle Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dodgybits.android.shuffle.model;


public final class Task {
	public Integer id;
	public final String description;
	public final String details;
	public final Context context;
	public final Project project;
	public final long created;
	public final long modified;
	public final long startDate;
	public final long dueDate;
	public final String timezone;
	public final Boolean allDay;
	public final Boolean hasAlarms;
	// 0-indexed order within a project. 
	public final Integer order;
	public final Boolean complete;
	
	public Task(Integer id, String description, String details,
			Context context, Project project, long created, long modified, 
			long startDate, long dueDate, String timezone, Boolean allDay, Boolean hasAlarms,
			Integer order, Boolean complete) {
		this.id = id;
		this.description = description;
		this.details = details;
		this.context = context;
		this.project = project;
		this.created = created;
		this.modified = modified;
		this.startDate = startDate;
		this.dueDate = dueDate;
		this.timezone = timezone;
		this.allDay = allDay;
		this.hasAlarms = hasAlarms;
		this.order = order;
		this.complete = complete;
	}
	
	public Task(String description, String details,
			Context context, Project project, long created, long modified,
			long startDate, long dueDate, String timezone, Boolean allDay, 
			Boolean hasAlarms, Integer order, Boolean complete) {
		this(
				null, description, details, 
				context, project, created, modified, 
				startDate, dueDate, timezone, allDay, 
				hasAlarms, order, complete);
	}
	
}
