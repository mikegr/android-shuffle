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

import java.util.Date;

public class Task {
	public Integer id;
	public final String description;
	public final String details;
	public final Context context;
	public final Project project;
	public final Date created;
	public final Date modified;
	public final Date dueDate;
	// 0-indexed order within a project. 
	public final Integer order;
	public final Boolean complete;
	
	public Task(Integer id, String description, String details,
			Context context, Project project, Date created, Date modified, Date dueDate,
			Integer order, Boolean complete) {
		this.id = id;
		this.description = description;
		this.details = details;
		this.context = context;
		this.project = project;
		this.created = created;
		this.modified = modified;
		this.dueDate = dueDate;
		this.order = order;
		this.complete = complete;
	}
	
	public Task(String description, String details,
			Context context, Project project, Date created, 
			Date modified, Date dueDate,
			Integer order, Boolean complete) {
		this(
				null, description, details, context, project, 
				created, modified, dueDate, order, complete);
	}
	
}
