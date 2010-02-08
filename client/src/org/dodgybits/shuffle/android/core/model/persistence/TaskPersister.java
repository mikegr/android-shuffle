package org.dodgybits.shuffle.android.core.model.persistence;

import java.util.TimeZone;

import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.Task.Builder;
import static org.dodgybits.shuffle.android.persistence.provider.Shuffle.Tasks.*;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.text.format.Time;

public class TaskPersister extends AbstractEntityPersister implements EntityPersister<Task> {

    private static final int ID_INDEX = 0;
    private static final int DESCRIPTION_INDEX = ID_INDEX + 1;
    private static final int DETAILS_INDEX = DESCRIPTION_INDEX + 1;
    private static final int PROJECT_INDEX = DETAILS_INDEX + 1;
    private static final int CONTEXT_INDEX = PROJECT_INDEX + 1;
    private static final int CREATED_INDEX = CONTEXT_INDEX + 1;
    private static final int MODIFIED_INDEX = CREATED_INDEX + 1;
    private static final int START_INDEX = MODIFIED_INDEX + 1;
    private static final int DUE_INDEX = START_INDEX + 1;
    private static final int TIMEZONE_INDEX = DUE_INDEX + 1;
    private static final int CAL_EVENT_INDEX = TIMEZONE_INDEX + 1;
    private static final int DISPLAY_ORDER_INDEX = CAL_EVENT_INDEX + 1;
    private static final int COMPLETE_INDEX = DISPLAY_ORDER_INDEX + 1;
    private static final int ALL_DAY_INDEX = COMPLETE_INDEX + 1;
    private static final int HAS_ALARM_INDEX = ALL_DAY_INDEX + 1;
    private static final int TASK_TRACK_INDEX = HAS_ALARM_INDEX  + 1;
    
    @Override
    public Task read(Cursor cursor) {
        Builder builder = Task.newBuilder();
        builder
            .setLocalId(readId(cursor, ID_INDEX))
            .setDescription(readString(cursor, DESCRIPTION_INDEX))
            .setDetails(readString(cursor, DETAILS_INDEX))
            .setProjectId(readId(cursor, PROJECT_INDEX))
            .setContextId(readId(cursor, CONTEXT_INDEX))
            .setCreatedDate(readLong(cursor, CREATED_INDEX))
            .setModifiedDate(readLong(cursor, MODIFIED_INDEX))
            .setStartDate(readLong(cursor, START_INDEX))
            .setDueDate(readLong(cursor, DUE_INDEX))
            .setTimezone(readString(cursor, TIMEZONE_INDEX))
            .setCalendarEventId(readId(cursor, CAL_EVENT_INDEX))
            .setOrder(cursor.getInt(DISPLAY_ORDER_INDEX))
            .setComplete(readBoolean(cursor, COMPLETE_INDEX))
            .setAllDay(readBoolean(cursor, ALL_DAY_INDEX))
            .setHasAlarm(readBoolean(cursor, HAS_ALARM_INDEX))
            .setTracksId(readId(cursor, TASK_TRACK_INDEX));

        return builder.build();
    }
    
    @Override
    public void write(ContentValues values, Task task) {
        // never write id since it's auto generated
        writeString(values, DESCRIPTION, task.getDescription());
        writeString(values, DETAILS, task.getDetails());
        writeId(values, PROJECT_ID, task.getProjectId());
        writeId(values, CONTEXT_ID, task.getContextId());
        values.put(CREATED_DATE, task.getCreatedDate());
        values.put(MODIFIED_DATE, task.getModifiedDate());
        values.put(START_DATE, task.getStartDate());
        values.put(DUE_DATE, task.getDueDate());
        
        String timezone = task.getTimezone();
        if (TextUtils.isEmpty(timezone))
        {
            if (task.isAllDay()) {
                timezone = Time.TIMEZONE_UTC;
            } else {
                timezone = TimeZone.getDefault().getID();
            }
        }
        values.put(TIMEZONE, timezone);

        writeId(values, CAL_EVENT_ID, task.getCalendarEventId());
        values.put(DISPLAY_ORDER, task.getOrder());
        writeBoolean(values, COMPLETE, task.isComplete());
        writeBoolean(values, ALL_DAY, task.isAllDay());
        writeBoolean(values, HAS_ALARM, task.hasAlarms());
        writeId(values, TRACKS_ID, task.getTracksId());
    }

    
    
}
