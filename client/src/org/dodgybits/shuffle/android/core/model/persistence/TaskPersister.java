package org.dodgybits.shuffle.android.core.model.persistence;

import static org.dodgybits.shuffle.android.core.util.Constants.cFlurryCompleteTaskEvent;
import static org.dodgybits.shuffle.android.core.util.Constants.cFlurryCountParam;
import static org.dodgybits.shuffle.android.core.util.Constants.cFlurryDeleteEntityEvent;
import static org.dodgybits.shuffle.android.core.util.Constants.cFlurryReorderTasksEvent;
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

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.Task.Builder;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;

import roboguice.inject.ContentResolverProvider;
import roboguice.inject.ContextScoped;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.util.SparseIntArray;

import com.google.inject.Inject;

@ContextScoped
public class TaskPersister extends AbstractEntityPersister<Task> {
    private static final String cTag = "TaskPersister";

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
    
    @Inject
    public TaskPersister(ContentResolverProvider provider) {
        super(provider.get());
    }
    
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
    protected void writeContentValues(ContentValues values, Task task) {
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

    @Override
    protected String getEntityName() {
        return "task";
    }
    
    @Override
    public Uri getContentUri() {
        return TaskProvider.Tasks.CONTENT_URI;
    }
    
    @Override
    public String[] getFullProjection() {
        return TaskProvider.Tasks.cFullProjection;
    }
    
    public int deleteCompletedTasks() {
        int deletedRows = mResolver.delete(
                getContentUri(), 
                TaskProvider.Tasks.COMPLETE + " = 1",
                null);
        Log.d(cTag, "Deleted " + deletedRows + " completed tasks.");
        
        Map<String, String> params = new HashMap<String,String>(mFlurryParams);
        params.put(cFlurryCountParam, String.valueOf(deletedRows));
        mAnalytics.onEvent(cFlurryDeleteEntityEvent, params);
        
        return deletedRows;
    }

    /**
     * Toggle whether the task at the given cursor position is complete.
     * The cursor is committed and re-queried after the update.
     *
     * @param cursor cursor positioned at task to update
     * @return new value of task completeness
     */
    public boolean toggleTaskComplete(Cursor cursor) {
        Id taskId = readId(cursor, ID_INDEX);
        boolean isComplete = !readBoolean(cursor, COMPLETE_INDEX);
        ContentValues values = new ContentValues();
        writeBoolean(values, COMPLETE, isComplete);
        values.put(MODIFIED_DATE, System.currentTimeMillis());
        mResolver.update(getContentUri(), values,
                TaskProvider.Tasks._ID + "=?", new String[] { String.valueOf(taskId) });
        if (isComplete) {
            mAnalytics.onEvent(cFlurryCompleteTaskEvent);
        }
        return isComplete;
    }


    /**
     * Swap the display order of two tasks at the given cursor positions.
     * The cursor is committed and re-queried after the update.
     */
    public void swapTaskPositions(Cursor cursor, int pos1, int pos2) {
        cursor.moveToPosition(pos1);
        Id id1 = readId(cursor, ID_INDEX);
        int positionValue1 = cursor.getInt(DISPLAY_ORDER_INDEX);
        cursor.moveToPosition(pos2);
        Id id2 = readId(cursor, ID_INDEX);
        int positionValue2 = cursor.getInt(DISPLAY_ORDER_INDEX);

        Uri uri = ContentUris.withAppendedId(getContentUri(), id1.getId());
        ContentValues values = new ContentValues();
        values.put(DISPLAY_ORDER, positionValue2);
        mResolver.update(uri, values, null, null);

        uri = ContentUris.withAppendedId(getContentUri(), id2.getId());
        values = new ContentValues();
        values.put(DISPLAY_ORDER, positionValue1);
        mResolver.update(uri, values, null, null);
        mAnalytics.onEvent(cFlurryReorderTasksEvent);
    }

    
    private static final int TASK_COUNT_INDEX = 1;
    
    public SparseIntArray readCountArray(Cursor cursor) {
        
        SparseIntArray countMap = new SparseIntArray();
        while (cursor.moveToNext()) {
            Integer id = cursor.getInt(ID_INDEX);
            Integer count = cursor.getInt(TASK_COUNT_INDEX);
            countMap.put(id, count);
        }
        return countMap;
    }    
}
