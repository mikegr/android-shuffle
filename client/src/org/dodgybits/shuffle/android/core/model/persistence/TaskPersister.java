package org.dodgybits.shuffle.android.core.model.persistence;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.util.SparseIntArray;
import com.google.inject.Inject;
import org.dodgybits.shuffle.android.core.activity.flurry.Analytics;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.Task.Builder;
import org.dodgybits.shuffle.android.core.model.persistence.selector.Flag;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.core.util.StringUtils;
import org.dodgybits.shuffle.android.persistence.provider.AbstractCollectionProvider;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import roboguice.inject.ContentResolverProvider;
import roboguice.inject.ContextScoped;
import roboguice.util.Ln;

import java.util.*;

import static org.dodgybits.shuffle.android.core.util.Constants.*;
import static org.dodgybits.shuffle.android.persistence.provider.AbstractCollectionProvider.ShuffleTable.ACTIVE;
import static org.dodgybits.shuffle.android.persistence.provider.AbstractCollectionProvider.ShuffleTable.DELETED;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.*;

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
    private static final int DELETED_INDEX = TASK_TRACK_INDEX +1;
    private static final int ACTIVE_INDEX = DELETED_INDEX +1;

    @Inject
    public TaskPersister(ContentResolverProvider provider, Analytics analytics) {
        super(provider.get(), analytics);
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
            .setTracksId(readId(cursor, TASK_TRACK_INDEX))
            .setDeleted(readBoolean(cursor, DELETED_INDEX))
            .setActive(readBoolean(cursor, ACTIVE_INDEX));

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
        writeBoolean(values, DELETED, task.isDeleted());
        writeBoolean(values, ACTIVE, task.isActive());

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
        return TaskProvider.Tasks.FULL_PROJECTION;
    }

    @Override
    public int emptyTrash() {
        // find tasks that are deleted or who's context or project is deleted
        TaskSelector selector = TaskSelector.newBuilder().setDeleted(Flag.yes).build();

        Cursor cursor = mResolver.query(getContentUri(),
                new String[] {BaseColumns._ID},
                selector.getSelection(null),
                selector.getSelectionArgs(),
                selector.getSortOrder());
        List<String> ids = new ArrayList<String>();
        while (cursor.moveToNext()) {
            ids.add(cursor.getString(ID_INDEX));
        }
        cursor.close();

        int rowsDeleted = 0;
        if (ids.size() > 0) {
            Ln.i("About to delete tasks %s", ids);
            String queryString = "_id IN (" + StringUtils.join(ids, ",") + ")";
            rowsDeleted = mResolver.delete(getContentUri(), queryString, null);
            Map<String, String> params = new HashMap<String, String>(mFlurryParams);
            params.put(cFlurryCountParam, String.valueOf(rowsDeleted));
            mAnalytics.onEvent(cFlurryDeleteEntityEvent, params);
        }

        return rowsDeleted;
    }

    public int deleteCompletedTasks() {
        int deletedRows = updateDeletedFlag(TaskProvider.Tasks.COMPLETE + " = 1", null, true);
        Log.d(cTag, "Deleting " + deletedRows + " completed tasks.");
        
        Map<String, String> params = new HashMap<String,String>(mFlurryParams);
        params.put(cFlurryCountParam, String.valueOf(deletedRows));
        mAnalytics.onEvent(cFlurryDeleteEntityEvent, params);
        
        return deletedRows;
    }


    public void updateCompleteFlag(Id id, boolean isComplete) {
        ContentValues values = new ContentValues();
        writeBoolean(values, COMPLETE, isComplete);
        values.put(MODIFIED_DATE, System.currentTimeMillis());
        mResolver.update(getUri(id), values, null, null);
        if (isComplete) {
            mAnalytics.onEvent(cFlurryCompleteTaskEvent);
        }
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
        values.clear();
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
