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

import org.dodgybits.android.shuffle.service.Locator;
import org.dodgybits.shuffle.dto.ShuffleProtos.Task.Builder;

public final class Task extends AbstractEntity implements TracksCompatible{
	public Long id;
	public final String description;
	public final String details;
	public final Context context;
	public final Project project;
	public final long created;
	public final long modified;
    public Long tracksId;
	public final long startDate;
	public final long dueDate;
	public final String timezone;
	public final Boolean allDay;
	public final Boolean hasAlarms;
	public final Long calEventId;
	// 0-indexed order within a project. 
	public final Integer order;
	public final Boolean complete;
	
	public Task(Long id, String description, String details,
                Context context, Project project, long created, long modified,
                long startDate, long dueDate, String timezone, Boolean allDay, Boolean hasAlarms,
                Long calEventId, Integer order, Boolean complete,
                Long tracksId) {
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
		this.calEventId = calEventId;
		this.order = order;
		this.complete = complete;
        this.tracksId = tracksId;

    }
	
	public Task(String description, String details,
			Context context, Project project, long created, long modified,
			long startDate, long dueDate, String timezone, Boolean allDay, 
			Boolean hasAlarms, Long calEventId, Integer order, Boolean complete,
			Long tracksId, Long tracksModified) {
		this(
				null, description, details, 
				context, project, created, modified, 
				startDate, dueDate, timezone, allDay, 
				hasAlarms, calEventId, order, complete, 
				tracksId);
	}

    @Override
    public Long getTracksId() {
        return tracksId;
    }

    @Override
    public long getModified() {
        return modified;
    }

    @Override
    public String getLocalName() {
        return description;
    }

    @Override
    public void setTracksId(Long id) {
        tracksId = id;
    }

    public org.dodgybits.shuffle.dto.ShuffleProtos.Task toDto() {
		Builder builder = org.dodgybits.shuffle.dto.ShuffleProtos.Task.newBuilder();
		builder
			.setId(id)
			.setDescription(description)
			.setCreated(toDate(created))
			.setModified(toDate(modified))
			.setStartDate(toDate(startDate))
			.setDueDate(toDate(dueDate))
			.setAllDay(allDay)
			.setOrder(order)
			.setComplete(complete);
		if (details != null) {
			builder.setDetails(details);
		}
		if (context != null) {
			builder.setContextId(context.id);
		}
		if (project != null) {
			builder.setProjectId(project.id);
		}
		if (timezone != null) {
			builder.setTimezone(timezone);
		}
		if (calEventId != null) {
			builder.setCalEventId(calEventId);
		}
        if (tracksId != null) {
            builder.setTracksId(tracksId);
        }
		return builder.build();
	}

	public static Task buildFromDto(
			org.dodgybits.shuffle.dto.ShuffleProtos.Task dto,
			Locator<Context> contextLocator,
			Locator<Project> projectLocator) {
		Long id = dto.getId();
		String description = dto.getDescription();
		String details = dto.getDetails();

		Context context = null;
		if (dto.hasContextId()) {
			context = contextLocator.findById(dto.getContextId());
		}

		Project project = null;
		if (dto.hasProjectId()) {
			project = projectLocator.findById(dto.getProjectId());
		}

		long created = fromDate(dto.getCreated());
		long modified = fromDate(dto.getModified());
		long startDate = fromDate(dto.getStartDate());
		long dueDate = fromDate(dto.getDueDate());
		String timezone = dto.getTimezone();
		Boolean allDay = dto.getAllDay();
		Boolean hasAlarms = false;

		Long calEventId = null;
		if (dto.hasCalEventId()) {
			calEventId = dto.getCalEventId();
		}

		Integer order = dto.getOrder();
		Boolean complete = dto.getComplete();

		Long tracksId = null;
		if (dto.hasTracksId()) {
		    tracksId = dto.getTracksId();
		}
		
		return new Task(
				id, description, details,
				context, project, created, modified,
				startDate, dueDate, timezone, allDay,
				hasAlarms, calEventId, order, complete,
				tracksId);
	}

}
