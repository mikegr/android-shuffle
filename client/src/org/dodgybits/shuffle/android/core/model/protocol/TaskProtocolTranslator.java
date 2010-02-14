package org.dodgybits.shuffle.android.core.model.protocol;

import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.dto.ShuffleProtos.Task.Builder;

public class TaskProtocolTranslator implements EntityProtocolTranslator<Task, org.dodgybits.shuffle.dto.ShuffleProtos.Task> {

    private final EntityDirectory<Context> mContextDirectory;
    private final EntityDirectory<Project> mProjectDirectory;
    
    public TaskProtocolTranslator(
            EntityDirectory<Context> contextDirectory,
            EntityDirectory<Project> projectDirectory) {
        mContextDirectory = contextDirectory;
        mProjectDirectory = projectDirectory;
    }

    public org.dodgybits.shuffle.dto.ShuffleProtos.Task toMessage(Task task) {
        Builder builder = org.dodgybits.shuffle.dto.ShuffleProtos.Task.newBuilder();
        builder
            .setId(task.getLocalId().getId())
            .setDescription(task.getDescription())
            .setCreated(ProtocolUtil.toDate(task.getCreatedDate()))
            .setModified(ProtocolUtil.toDate(task.getModifiedDate()))
            .setStartDate(ProtocolUtil.toDate(task.getStartDate()))
            .setDueDate(ProtocolUtil.toDate(task.getDueDate()))
            .setAllDay(task.isAllDay())
            .setOrder(task.getOrder())
            .setComplete(task.isComplete());
        
        final String details = task.getDetails();
        if (details != null) {
            builder.setDetails(details);
        }
        
        final Id contextId = task.getContextId();
        if (contextId.isInitialised()) {
            builder.setContextId(contextId.getId());
        }

        final Id projectId = task.getProjectId();
        if (projectId.isInitialised()) {
            builder.setProjectId(projectId.getId());
        }

        final String timezone = task.getTimezone();
        if (timezone != null) {
            builder.setTimezone(timezone);
        }
        
        final Id calEventId = task.getCalendarEventId();
        if (calEventId.isInitialised()) {
            builder.setCalEventId(calEventId.getId());
        }

        final Id tracksId = task.getTracksId();
        if (tracksId.isInitialised()) {
            builder.setTracksId(tracksId.getId());
        }
        
        return builder.build();
    }

    public Task fromMessage(
            org.dodgybits.shuffle.dto.ShuffleProtos.Task dto) {
        Task.Builder builder = Task.newBuilder();
        builder
            .setLocalId(Id.create(dto.getId()))
            .setDescription(dto.getDescription())
            .setDetails(dto.getDetails())
            .setCreatedDate(ProtocolUtil.fromDate(dto.getCreated()))
            .setModifiedDate(ProtocolUtil.fromDate(dto.getModified()))
            .setStartDate(ProtocolUtil.fromDate(dto.getStartDate()))
            .setDueDate(ProtocolUtil.fromDate(dto.getDueDate()))
            .setTimezone(dto.getTimezone())
            .setAllDay(dto.getAllDay())
            .setHasAlarm(false)
            .setOrder(dto.getOrder())
            .setComplete(dto.getComplete());
            
        if (dto.hasContextId()) {
            Id contextId = Id.create(dto.getContextId());
            Context context = mContextDirectory.findById(contextId);
            builder.setContextId(context.getLocalId());
        }

        if (dto.hasProjectId()) {
            Id projectId = Id.create(dto.getProjectId());
            Project project = mProjectDirectory.findById(projectId);
            builder.setProjectId(project.getLocalId());
        }
        
        if (dto.hasCalEventId()) {
            builder.setCalendarEventId(Id.create(dto.getCalEventId()));
        }

        if (dto.hasTracksId()) {
            builder.setTracksId(Id.create(dto.getTracksId()));
        }

        return builder.build();
    }    
    
}
