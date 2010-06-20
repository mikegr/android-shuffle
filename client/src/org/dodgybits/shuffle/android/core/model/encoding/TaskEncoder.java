package org.dodgybits.shuffle.android.core.model.encoding;

import static android.provider.BaseColumns._ID;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.ALL_DAY;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.CAL_EVENT_ID;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.COMPLETE;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.CONTEXT_ID;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.CREATED_DATE;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.DESCRIPTION;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.DETAILS;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.DISPLAY_ORDER;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.DUE_DATE;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.HAS_ALARM;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.MODIFIED_DATE;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.PROJECT_ID;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.START_DATE;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.TIMEZONE;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.TRACKS_ID;

import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.Task.Builder;

import android.os.Bundle;

import com.google.inject.Singleton;

@Singleton
public class TaskEncoder extends AbstractEntityEncoder implements
        EntityEncoder<Task> {
    
    @Override
    public void save(Bundle icicle, Task task) {
        putId(icicle, _ID, task.getLocalId());
        putId(icicle, TRACKS_ID, task.getTracksId());
        icicle.putLong(MODIFIED_DATE, task.getModifiedDate());

        putString(icicle, DESCRIPTION, task.getDescription());
        putString(icicle, DETAILS, task.getDetails());
        putId(icicle, CONTEXT_ID, task.getContextId());
        putId(icicle, PROJECT_ID, task.getProjectId());
        icicle.putLong(CREATED_DATE, task.getCreatedDate());
        icicle.putLong(START_DATE, task.getStartDate());
        icicle.putLong(DUE_DATE, task.getDueDate());
        putString(icicle, TIMEZONE, task.getTimezone());
        putId(icicle, CAL_EVENT_ID, task.getCalendarEventId());
        icicle.putBoolean(ALL_DAY, task.isAllDay());
        icicle.putBoolean(HAS_ALARM, task.hasAlarms());
        icicle.putInt(DISPLAY_ORDER, task.getOrder());
        icicle.putBoolean(COMPLETE, task.isComplete());
    }

    @Override
    public Task restore(Bundle icicle) {
        if (icicle == null) return null;

        Builder builder = Task.newBuilder();
        builder.setLocalId(getId(icicle, _ID));
        builder.setModifiedDate(icicle.getLong(MODIFIED_DATE, 0L));
        builder.setTracksId(getId(icicle, TRACKS_ID));

        builder.setDescription(getString(icicle, DESCRIPTION));
        builder.setDetails(getString(icicle, DETAILS));
        builder.setContextId(getId(icicle, CONTEXT_ID));
        builder.setProjectId(getId(icicle, PROJECT_ID));
        builder.setCreatedDate(icicle.getLong(CREATED_DATE, 0L));
        builder.setStartDate(icicle.getLong(START_DATE, 0L));
        builder.setDueDate(icicle.getLong(DUE_DATE, 0L));
        builder.setTimezone(getString(icicle, TIMEZONE));
        builder.setCalendarEventId(getId(icicle, CAL_EVENT_ID));
        builder.setAllDay(icicle.getBoolean(ALL_DAY));
        builder.setHasAlarm(icicle.getBoolean(HAS_ALARM));
        builder.setOrder(icicle.getInt(DISPLAY_ORDER));
        builder.setComplete(icicle.getBoolean(COMPLETE));

        return builder.build();
    }

}
