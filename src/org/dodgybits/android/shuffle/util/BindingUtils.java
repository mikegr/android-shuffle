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

package org.dodgybits.android.shuffle.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TimeZone;

import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.model.Context.Icon;
import org.dodgybits.android.shuffle.provider.Shuffle;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.SparseIntArray;

/**
 * Static methods for converting to/from Android representations of our
 * model to our own model classes.
 */
public class BindingUtils {

    private BindingUtils() {
		// deny
	}
	
	public static Task restoreTask(Bundle icicle, Resources res) {
		if (icicle == null) return null;
		Integer id = getInteger(icicle, Shuffle.Tasks._ID);
		String description = icicle.getString(Shuffle.Tasks.DESCRIPTION);
		String details = icicle.getString(Shuffle.Tasks.DETAILS);
		Context context = restoreContext(icicle.getBundle(Shuffle.Tasks.CONTEXT_ID), res);
		Project project = restoreProject(icicle.getBundle(Shuffle.Tasks.PROJECT_ID));
		long created = icicle.getLong(Shuffle.Tasks.CREATED_DATE, 0L);
		long modified = icicle.getLong(Shuffle.Tasks.MODIFIED_DATE, 0L);
		long startDate = icicle.getLong(Shuffle.Tasks.START_DATE, 0L);
		long dueDate = icicle.getLong(Shuffle.Tasks.DUE_DATE, 0L);
		String timezone = icicle.getString(Shuffle.Tasks.TIMEZONE);
		Boolean allDay = icicle.getBoolean(Shuffle.Tasks.ALL_DAY);
		Boolean hasAlarms = icicle.getBoolean(Shuffle.Tasks.HAS_ALARM);
		int order = icicle.getInt(Shuffle.Tasks.DISPLAY_ORDER);
		Boolean complete = icicle.getBoolean(Shuffle.Tasks.COMPLETE);
		return new Task(
				id, description, details, 
				context, project, created, modified,
				startDate, dueDate, timezone, allDay, hasAlarms,
				order, complete);
	}
		
	public static Context restoreContext(Bundle icicle, Resources res) {
		if (icicle == null) return null;
		Integer id = getInteger(icicle, Shuffle.Contexts._ID);
		String name = icicle.getString(Shuffle.Contexts.NAME);
		Integer colour = getInteger(icicle, Shuffle.Contexts.COLOUR);
		String iconName = icicle.getString(Shuffle.Contexts.ICON);
		Icon icon = Icon.createIcon(iconName, res);
		return new Context(id, name, colour, icon);
	}
	
	public static Project restoreProject(Bundle icicle) {
		if (icicle == null) return null;
		Integer id = getInteger(icicle, Shuffle.Projects._ID);
		String name = icicle.getString(Shuffle.Projects.NAME);
		Integer defaultContextId = getInteger(icicle, Shuffle.Projects.DEFAULT_CONTEXT_ID);
		Boolean archived = icicle.getBoolean(Shuffle.Projects.ARCHIVED);
		return new Project(id, name, defaultContextId, archived);
	}
		
	public static Bundle saveTask(Bundle icicle, Task task) {
		putInteger(icicle, Shuffle.Tasks._ID, task.id);
		icicle.putString(Shuffle.Tasks.DESCRIPTION, task.description);
		icicle.putString(Shuffle.Tasks.DETAILS, task.details);
		if (task.context != null) {
			icicle.putBundle(Shuffle.Tasks.CONTEXT_ID, saveContext(new Bundle(), task.context));
		}
		if (task.project != null) {
			icicle.putBundle(Shuffle.Tasks.PROJECT_ID, saveProject(new Bundle(), task.project));
		}
		icicle.putLong(Shuffle.Tasks.CREATED_DATE, task.created);
		icicle.putLong(Shuffle.Tasks.MODIFIED_DATE, task.modified);
		icicle.putLong(Shuffle.Tasks.START_DATE, task.startDate);
		icicle.putLong(Shuffle.Tasks.DUE_DATE, task.dueDate);
		icicle.putString(Shuffle.Tasks.TIMEZONE, task.timezone);
		icicle.putBoolean(Shuffle.Tasks.ALL_DAY, task.allDay);
		icicle.putBoolean(Shuffle.Tasks.HAS_ALARM, task.hasAlarms);
		putInteger(icicle, Shuffle.Tasks.DISPLAY_ORDER, task.order);
		icicle.putBoolean(Shuffle.Tasks.COMPLETE, task.complete);
		return icicle;
	}
	
	private static Integer getInteger(Bundle icicle, String key) {
		return icicle.containsKey(key) ? icicle.getInt(key) : null;
	}
	
	private static void putInteger(Bundle icicle, String key, Integer value) {
		if (value != null) icicle.putInt(key, value);
	}
	
	public static Bundle saveContext(Bundle icicle, Context context) {
		putInteger(icicle, Shuffle.Contexts._ID, context.id);
		icicle.putString(Shuffle.Contexts.NAME, context.name);
		putInteger(icicle, Shuffle.Contexts.COLOUR, context.colourIndex);
		icicle.putString(Shuffle.Contexts.ICON, context.icon.iconName);
		return icicle;
	}
	
	public static Bundle saveProject(Bundle icicle, Project project) {
		putInteger(icicle, Shuffle.Projects._ID, project.id);
		icicle.putString(Shuffle.Projects.NAME, project.name);
		putInteger(icicle, Shuffle.Projects.DEFAULT_CONTEXT_ID, project.defaultContextId);
		icicle.putBoolean(Shuffle.Projects.ARCHIVED, project.archived);
		return icicle;
	}
	
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
    private static final int DISPLAY_ORDER_INDEX = TIMEZONE_INDEX + 1;
    private static final int COMPLETE_INDEX = DISPLAY_ORDER_INDEX + 1;
    private static final int ALL_DAY_INDEX = COMPLETE_INDEX + 1;
    private static final int HAS_ALARM_INDEX = ALL_DAY_INDEX + 1;

    private static final int PROJECT_NAME_INDEX = HAS_ALARM_INDEX + 1;
    private static final int PROJECT_DEFAULT_CONTEXT_ID_INDEX = PROJECT_NAME_INDEX + 1;
    private static final int PROJECT_ARCHIVED_INDEX = PROJECT_DEFAULT_CONTEXT_ID_INDEX + 1;
    
    private static final int CONTEXT_NAME_INDEX = PROJECT_ARCHIVED_INDEX + 1;
    private static final int CONTEXT_COLOUR_INDEX = CONTEXT_NAME_INDEX + 1;
    private static final int CONTEXT_ICON_INDEX = CONTEXT_COLOUR_INDEX + 1;
    
	public static Task readTask(Cursor cursor, Resources res) {
		Integer id = readInteger(cursor, ID_INDEX);
        String description = readString(cursor, DESCRIPTION_INDEX);
		String details = readString(cursor, DETAILS_INDEX);
		Integer projectId = readInteger(cursor, PROJECT_INDEX);
		Project project = readJoinedProject(cursor, projectId);
		Integer contextId = readInteger(cursor, CONTEXT_INDEX);
		Context context = readJoinedContext(cursor, res, contextId);
		
		long created = cursor.getLong(CREATED_INDEX);
		long modified = cursor.getLong(MODIFIED_INDEX);
		long startDate = cursor.getLong(START_INDEX);
		long dueDate = cursor.getLong(DUE_INDEX);
		String timezone = readString(cursor, TIMEZONE_INDEX);
		Integer displayOrder = readInteger(cursor, DISPLAY_ORDER_INDEX);
		Boolean complete = readBoolean(cursor, COMPLETE_INDEX);
		Boolean allDay = readBoolean(cursor, ALL_DAY_INDEX);
		Boolean hasAlarm = readBoolean(cursor, HAS_ALARM_INDEX);
		return new Task(
				id, description, details, 
				context, project, created, modified, 
				startDate, dueDate, timezone, allDay, hasAlarm,
				displayOrder, complete);
	}
	
	private static Project readJoinedProject(Cursor cursor, Integer projectId) {
		if (projectId == null) return null;
		String name = readString(cursor, PROJECT_NAME_INDEX);
		if (TextUtils.isEmpty(name)) return null;
		Integer defaultContextId = readInteger(cursor, PROJECT_DEFAULT_CONTEXT_ID_INDEX);
		Boolean archived = readBoolean(cursor, PROJECT_ARCHIVED_INDEX);
		return new Project(projectId, name, defaultContextId, archived);
		
	}

	private static Context readJoinedContext(Cursor cursor, Resources res, Integer contextId) {
		if (contextId == null) return null;
		String name = readString(cursor, CONTEXT_NAME_INDEX);
		if (TextUtils.isEmpty(name)) return null;
		int colour = cursor.getInt(CONTEXT_COLOUR_INDEX);
		String iconName = readString(cursor, CONTEXT_ICON_INDEX);
		Icon icon = Icon.createIcon(iconName, res);
		return new Context(contextId, name, colour, icon);
	}

	public static Project fetchProjectById(android.content.Context androidContext, Integer projectId) {
		Project project = null;
		if (projectId != null) {
			Uri uri = ContentUris.withAppendedId(Shuffle.Projects.CONTENT_URI, projectId);			
			Cursor projectCursor = androidContext.getContentResolver().query(uri, 
					Shuffle.Projects.cFullProjection, null, null, null);
			if (projectCursor.moveToFirst()) {
				project = readProject(projectCursor);
			}
			projectCursor.close();
		}
		return project;
	}
	
	public static Context fetchContextById(android.content.Context androidContext, Integer contextId) {
		Context context = null;
		if (contextId != null) {
			Uri uri = ContentUris.withAppendedId(Shuffle.Contexts.CONTENT_URI, contextId);			
			Cursor contextCursor = androidContext.getContentResolver().query(
					uri, Shuffle.Contexts.cFullProjection, null, null, null);
			if (contextCursor.moveToFirst()) {
				context = readContext(contextCursor, androidContext.getResources());
			}
			contextCursor.close();
		}
		return context;
	}
	
	/**
	 * Toggle whether the task at the given cursor position is complete.
	 * The cursor is committed and re-queried after the update.
	 * 
	 * @param cursor cursor positioned at task to update
	 * @return new value of task completeness
	 */
	public static boolean toggleTaskComplete(android.content.Context androidContext, Cursor cursor, Uri listUri, long taskId  ) {
		Boolean newValue = !readBoolean(cursor, COMPLETE_INDEX);
        ContentValues values = new ContentValues();
		writeBoolean(values, Shuffle.Tasks.COMPLETE, newValue);
        androidContext.getContentResolver().update(listUri, values, 
        		Shuffle.Tasks._ID + "=?", new String[] { String.valueOf(taskId) });
		return newValue;
	}
	
	/**
	 * Swap the display order of two tasks at the given cursor positions. 
	 * The cursor is committed and re-queried after the update.
	 */
	public static void swapTaskPositions(android.content.Context androidContext, Cursor cursor, int pos1, int pos2) {
        cursor.moveToPosition(pos1);
        int positionValue1 = cursor.getInt(DISPLAY_ORDER_INDEX);
		Integer id1 = readInteger(cursor, ID_INDEX);
        cursor.moveToPosition(pos2);
        int positionValue2 = cursor.getInt(DISPLAY_ORDER_INDEX);
		Integer id2 = readInteger(cursor, ID_INDEX);
        
        Uri uri = ContentUris.withAppendedId(Shuffle.Tasks.CONTENT_URI, id1);
        ContentValues values = new ContentValues();
        writeInteger(values, Shuffle.Tasks.DISPLAY_ORDER, positionValue2);
        androidContext.getContentResolver().update(uri, values, null, null);

        uri = ContentUris.withAppendedId(Shuffle.Tasks.CONTENT_URI, id2);
        values = new ContentValues();
        writeInteger(values, Shuffle.Tasks.DISPLAY_ORDER, positionValue1);
        androidContext.getContentResolver().update(uri, values, null, null);
	}
	
	public static void writeTask(ContentValues values, Task task) {
		// never write id since it's auto generated
		writeString(values, Shuffle.Tasks.DESCRIPTION, task.description);
		writeString(values, Shuffle.Tasks.DETAILS, task.details);
		if (task.project != null) {
			writeInteger(values, Shuffle.Tasks.PROJECT_ID, task.project.id);
		} else {
			writeInteger(values, Shuffle.Tasks.PROJECT_ID, null);
		}
		if (task.context != null) {
			writeInteger(values, Shuffle.Tasks.CONTEXT_ID, task.context.id);
		} else {
			writeInteger(values, Shuffle.Tasks.CONTEXT_ID, null);
		}
		
		values.put(Shuffle.Tasks.CREATED_DATE, task.created);
		values.put(Shuffle.Tasks.MODIFIED_DATE, task.modified);
		values.put(Shuffle.Tasks.START_DATE, task.startDate);
		values.put(Shuffle.Tasks.DUE_DATE, task.dueDate);
		
		String timezone = task.timezone;
		if (TextUtils.isEmpty(timezone))
		{
			if (task.allDay) {
				timezone = Time.TIMEZONE_UTC;
			} else {
				timezone = TimeZone.getDefault().getID();
			}
		}
		values.put(Shuffle.Tasks.TIMEZONE, timezone);
		
		writeBoolean(values, Shuffle.Tasks.ALL_DAY, task.allDay);
		writeBoolean(values, Shuffle.Tasks.HAS_ALARM, task.hasAlarms);
		writeInteger(values, Shuffle.Tasks.DISPLAY_ORDER, task.order);
		writeBoolean(values, Shuffle.Tasks.COMPLETE, task.complete);
	}
	
	private static final int NAME_INDEX = 1;
	private static final int COLOUR_INDEX = 2;
	private static final int ICON_INDEX = 3;
	
	public static Context readContext(Cursor cursor, Resources res) {
		Integer id = readInteger(cursor, ID_INDEX);
		String name = readString(cursor, NAME_INDEX);
		int colour = cursor.getInt(COLOUR_INDEX);
		String iconName = readString(cursor, ICON_INDEX);
		Icon icon = Icon.createIcon(iconName, res);
		return new Context(id, name, colour, icon);
	}
	
	public static void writeContext(ContentValues values, Context context) {
		// never write id since it's auto generated
		writeString(values, Shuffle.Contexts.NAME, context.name);
		writeInteger(values, Shuffle.Contexts.COLOUR, context.colourIndex);
		writeString(values, Shuffle.Contexts.ICON, context.icon.iconName);
	}
	
	private static final int DEFAULT_CONTEXT_INDEX = 2;
	private static final int ARCHIVED_INDEX = 3;
	
	public static Project readProject(Cursor cursor) {
		Integer id = readInteger(cursor, ID_INDEX);
		String name = readString(cursor, NAME_INDEX);
		Integer defaultContextId = readInteger(cursor, DEFAULT_CONTEXT_INDEX);
		Boolean archived = readBoolean(cursor, ARCHIVED_INDEX);
		return new Project(id, name, defaultContextId, archived);
	}
	
	public static void writeProject(ContentValues values, Project project) {
		// never write id since it's auto generated
		writeString(values, Shuffle.Projects.NAME, project.name);
		writeInteger(values, Shuffle.Projects.DEFAULT_CONTEXT_ID, project.defaultContextId);
		writeBoolean(values, Shuffle.Projects.ARCHIVED, project.archived);
	}
	
	private static final int TASK_COUNT_INDEX = 1;
	
	public static SparseIntArray readCountArray(Cursor cursor) {
		
		SparseIntArray countMap = new SparseIntArray();
		while (cursor.moveToNext()) {
			countMap.put(cursor.getInt(ID_INDEX), cursor.getInt(TASK_COUNT_INDEX));
		}
		return countMap;
	}
	
	private static Integer readInteger(Cursor cursor, int index) {
		return (cursor.isNull(index) ? null : cursor.getInt(index));
	}
	
	private static Boolean readBoolean(Cursor cursor, int index) {
		return (cursor.getInt(index) == 1);
	}
	
	private static String readString(Cursor cursor, int index) {
		return (cursor.isNull(index) ? null : cursor.getString(index));
	}
	
	private static void writeInteger(ContentValues values, String key, Integer value) {
		if (value == null) {
			values.putNull(key);
		} else {
			values.put(key, value);
		}
	}
	
	private static void writeBoolean(ContentValues values, String key, boolean value) {
		values.put(key, value ? 1 : 0);
	}
	
	private static void writeString(ContentValues values, String key, String value) {
		if (value == null) {
			values.putNull(key);
		} else {
			values.put(key, value);
		}
		
	}
	
	public static String toIdListString(Collection<Long> ids) {
		StringBuilder response = new StringBuilder();
		Iterator<Long> i = ids.iterator();
		while (i.hasNext()) {
			response.append(i.next());
			if (i.hasNext()) response.append(",");
		}
		return response.toString();
	}

	public static Collection<Long> toIdCollection(String idList) {
		if (TextUtils.isEmpty(idList)) return new ArrayList<Long>();
		String[] idArray = idList.split(",");
		Collection<Long> ids = new ArrayList<Long>(idArray.length);
		for (String id: idArray) {
			ids.add(Long.parseLong(id));
		}
		return ids;
	}
	

}
