package org.dodgybits.shuffle.android.core.model.encoding;

import static android.provider.BaseColumns._ID;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.ALL_DAY;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.CAL_EVENT_ID;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.COMPLETE;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.CONTEXT_ID;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.CREATED_DATE;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.DESCRIPTION;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.DETAILS;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.DISPLAY_ORDER;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.DUE_DATE;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.HAS_ALARM;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.MODIFIED_DATE;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.PROJECT_ID;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.START_DATE;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.TIMEZONE;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.TRACKS_ID;

import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.Task.Builder;

import android.os.Bundle;

public class TaskEncoder extends AbstractEntityEncoder implements
        EntityEncoder<Task> {
    
    @Override
    public Bundle save(Task task) {
        Bundle icicle = new Bundle();
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

        return icicle;
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
