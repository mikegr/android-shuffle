package org.dodgybits.android.shuffle.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;

import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;

/**
 * Static methods for converting to/from Android representations of our
 * model to our own model classes.
 */
public class BindingUtils {
    private static final String cTag = "BindingUtils";

    private BindingUtils() {
		// deny
	}
	
	public static Task restoreTask(Bundle icicle) {
		if (icicle == null) return null;
		Integer id = getInteger(icicle, Shuffle.Tasks._ID);
		String description = icicle.getString(Shuffle.Tasks.DESCRIPTION);
		String details = icicle.getString(Shuffle.Tasks.DETAILS);
		Context context = restoreContext(icicle.getBundle(Shuffle.Tasks.CONTEXT_ID));
		Project project = restoreProject(icicle.getBundle(Shuffle.Tasks.PROJECT_ID));
		Date created = restoreDate(icicle, Shuffle.Tasks.CREATED_DATE);
		Date modified = restoreDate(icicle, Shuffle.Tasks.MODIFIED_DATE);
		Date dueDate = restoreDate(icicle, Shuffle.Tasks.DUE_DATE);
		int order = icicle.getInt(Shuffle.Tasks.DISPLAY_ORDER);
		Boolean complete = icicle.getBoolean(Shuffle.Tasks.COMPLETE);
		return new Task(
				id, description, details, context, project, created, 
				modified, dueDate, order, complete);
	}
		
	private static Date restoreDate(Bundle icicle, String key) {
		Date date = null;
		if (icicle.containsKey(key)) {
			date = new Date(icicle.getLong(key));
		}
		return date;
	}
	
	public static Context restoreContext(Bundle icicle) {
		if (icicle == null) return null;
		Integer id = getInteger(icicle, Shuffle.Contexts._ID);
		String name = icicle.getString(Shuffle.Contexts.NAME);
		Integer colour = getInteger(icicle, Shuffle.Contexts.COLOUR);
		Integer iconResource = icicle.getInt(Shuffle.Contexts.ICON);
		return new Context(id, name, colour, iconResource);
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
		putDate(icicle, Shuffle.Tasks.CREATED_DATE, task.created);
		putDate(icicle, Shuffle.Tasks.MODIFIED_DATE, task.modified);
		putDate(icicle, Shuffle.Tasks.DUE_DATE, task.dueDate);
		putInteger(icicle, Shuffle.Tasks.DISPLAY_ORDER, task.order);
		icicle.putBoolean(Shuffle.Tasks.COMPLETE, task.complete);
		return icicle;
	}
	
	public static Collection<Long> restoreContactIds(Bundle icicle) {
		return toIdCollection(icicle.getString(Shuffle.TaskContacts.CONTACT_ID));
	}
	
	public static Bundle saveContactIds(Bundle icicle, Collection<Long> ids) {
		icicle.putString(Shuffle.TaskContacts.CONTACT_ID, toIdListString(ids));
		return icicle;
	}
	
	private static Integer getInteger(Bundle icicle, String key) {
		return icicle.containsKey(key) ? icicle.getInt(key) : null;
	}
	
	private static void putInteger(Bundle icicle, String key, Integer value) {
		if (value != null) icicle.putInt(key, value);
	}
	
	private static Bundle putDate(Bundle icicle, String key, Date date) {
		if (date != null) {
			icicle.putLong(key, date.getTime());
		}
		return icicle;
	}
	
	public static Bundle saveContext(Bundle icicle, Context context) {
		putInteger(icicle, Shuffle.Contexts._ID, context.id);
		icicle.putString(Shuffle.Contexts.NAME, context.name);
		putInteger(icicle, Shuffle.Contexts.COLOUR, context.colourIndex);
		putInteger(icicle, Shuffle.Contexts.ICON, context.iconResource);
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
    private static final int DESCRIPTION_INDEX = 1;
    private static final int DETAILS_INDEX = 2;
    private static final int PROJECT_INDEX = 3;
    private static final int CONTEXT_INDEX = 4;
    private static final int CREATED_INDEX = 5;
    private static final int MODIFIED_INDEX = 6;
    private static final int DUE_INDEX = 7;
    private static final int DISPLAY_ORDER_INDEX = 8;
    private static final int COMPLETE_INDEX = 9;

    private static final int PROJECT_NAME_INDEX = 10;
    private static final int PROJECT_DEFAULT_CONTEXT_ID_INDEX = 11;
    private static final int PROJECT_ARCHIVED_INDEX = 12;
    
    private static final int CONTEXT_NAME_INDEX = 13;
    private static final int CONTEXT_COLOUR_INDEX = 14;
    private static final int CONTEXT_ICON_INDEX = 15;
    
	public static Task readTask(Cursor cursor) {
		Integer id = readInteger(cursor, ID_INDEX);
        String description = readString(cursor, DESCRIPTION_INDEX);
		String details = readString(cursor, DETAILS_INDEX);
		Integer projectId = readInteger(cursor, PROJECT_INDEX);
		Project project = readJoinedProject(cursor, projectId);
		Integer contextId = readInteger(cursor, CONTEXT_INDEX);
		Context context = readJoinedContext(cursor, contextId);
		
		Date created = readDate(cursor, CREATED_INDEX);
		Date modified = readDate(cursor, MODIFIED_INDEX);
		Date dueDate = readDate(cursor, DUE_INDEX);
		Integer displayOrder = readInteger(cursor, DISPLAY_ORDER_INDEX);
		Boolean complete = readBoolean(cursor, COMPLETE_INDEX);
		return new Task(
				id, description, details, context, project, created, 
				modified, dueDate, displayOrder, complete);
	}
	
	private static Project readJoinedProject(Cursor cursor, Integer projectId) {
		if (projectId == null) return null;
		String name = readString(cursor, PROJECT_NAME_INDEX);
		Integer defaultContextId = readInteger(cursor, PROJECT_DEFAULT_CONTEXT_ID_INDEX);
		Boolean archived = readBoolean(cursor, PROJECT_ARCHIVED_INDEX);
		return new Project(projectId, name, defaultContextId, archived);
		
	}

	private static Context readJoinedContext(Cursor cursor, Integer contextId) {
		if (contextId == null) return null;
		String name = readString(cursor, CONTEXT_NAME_INDEX);
		int colour = cursor.getInt(CONTEXT_COLOUR_INDEX);
		Integer iconResourceId = readInteger(cursor, CONTEXT_ICON_INDEX);
		return new Context(contextId, name, colour, iconResourceId);
	}

	public static Project fetchProjectById(android.content.Context androidContext, Integer projectId) {
		Project project = null;
		if (projectId != null) {
			Uri uri = ContentUris.withAppendedId(Shuffle.Projects.CONTENT_URI, projectId);			
			Cursor projectCursor = androidContext.getContentResolver().query(uri, 
					Shuffle.Projects.cFullProjection, null, null, null);
			if (projectCursor.first()) {
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
			Cursor contextCursor = androidContext.getContentResolver().query(uri, Shuffle.Contexts.cFullProjection, null, null, null);
			if (contextCursor.first()) {
				context = readContext(contextCursor);
			}
			contextCursor.close();
		}
		return context;
	}
	
    @SuppressWarnings("unused")
	private static final int TASK_ID_INDEX = 1;
    private static final int CONTACT_ID_INDEX = 2;

	public static Collection<Long> fetchContactIds(android.content.Context androidContext, Integer taskId) {
		Log.d(cTag, "Fetching contacts for task " + taskId);
		Collection<Long> ids = new HashSet<Long>();
		if (taskId != null) {
			Uri uri = ContentUris.withAppendedId(Shuffle.TaskContacts.CONTENT_URI, taskId);			
			Cursor contactCursor = androidContext.getContentResolver().query(uri, 
					Shuffle.TaskContacts.cFullProjection, null, null, null);
			while (contactCursor.next()) {
				ids.add(contactCursor.getLong(CONTACT_ID_INDEX));
			}
			contactCursor.close();
		}
		Log.d(cTag, "Contacts found " + ids);
		return ids;
	}
	
	public static void updateContactIds(android.content.Context androidContext, Integer taskId, Collection<Long> ids) {
		// delete old ones first
		Log.d(cTag, "Updating contacts for task " + taskId);
		Uri uri = ContentUris.withAppendedId(Shuffle.TaskContacts.CONTENT_URI, taskId);			
		androidContext.getContentResolver().delete(uri, null, null);
		int count = ids.size();
		ContentValues[] values = new ContentValues[count];
		int i = 0;
		for (Long id : ids) {
			values[i] = new ContentValues();
			values[i].put(Shuffle.TaskContacts.TASK_ID, taskId);
			values[i].put(Shuffle.TaskContacts.CONTACT_ID, id);
			i++;
		}
		androidContext.getContentResolver().bulkInsert(uri, values);
	}
	
	public static List<String> fetchContactNames(android.content.Context androidContext, Collection<Long> ids) {
		List<String> names = new ArrayList<String>(ids.size());
		if (!ids.isEmpty()) {
			Uri uri = Contacts.People.CONTENT_URI;
			String idList = toIdListString(ids);
			Cursor contactCursor = androidContext.getContentResolver().query(uri, new String[] {Contacts.People._ID, Contacts.People.NAME}, 
			"people._id in (" + idList + ")", null, Contacts.People.NAME + " DESC");
			while (contactCursor.next()) {
				names.add(contactCursor.getString(1));
			}
			contactCursor.close();
		}
		return names;
	}
	
	
	/**
	 * Toggle whether the task at the given cursor position is complete.
	 * The cursor is committed and re-queried after the update.
	 * 
	 * @param cursor cursor positions at task to update
	 * @return new value of task completeness
	 */
	public static boolean toggleTaskComplete(Activity activity, Cursor cursor) {
		Boolean newValue = !readBoolean(cursor, COMPLETE_INDEX);
		writeBoolean(cursor, COMPLETE_INDEX, newValue);
		activity.managedCommitUpdates(cursor);
		return newValue;
	}
	
	/**
	 * Swap the display order of two tasks at the given cursor positions. 
	 * The cursor is committed and re-queried after the update.
	 */
	public static void swapTaskPositions(Cursor cursor, int pos1, int pos2) {
        cursor.moveTo(pos1);
        int positionValue1 = cursor.getInt(DISPLAY_ORDER_INDEX);
        cursor.moveTo(pos2);
        int positionValue2 = cursor.getInt(DISPLAY_ORDER_INDEX);
        cursor.updateInt(DISPLAY_ORDER_INDEX, positionValue1);
        cursor.moveTo(pos1);
        cursor.updateInt(DISPLAY_ORDER_INDEX, positionValue2);
		cursor.commitUpdates();
		cursor.requery();
	}
	
	public static void writeTask(Cursor cursor, Task task) {
		// never write id since it's auto generated
		writeString(cursor, DESCRIPTION_INDEX, task.description);
		writeString(cursor, DETAILS_INDEX, task.details);
		if (task.project != null) {
			writeInteger(cursor, PROJECT_INDEX, task.project.id);
		}
		if (task.context != null) {
			writeInteger(cursor, CONTEXT_INDEX, task.context.id);
		}
		writeDate(cursor, CREATED_INDEX, task.created);
		writeDate(cursor, MODIFIED_INDEX, task.modified);
		writeDate(cursor, DUE_INDEX, task.dueDate);
		writeInteger(cursor, DISPLAY_ORDER_INDEX, task.order);
		writeBoolean(cursor, COMPLETE_INDEX, task.complete);
	}
	
	private static final int NAME_INDEX = 1;
	private static final int COLOUR_INDEX = 2;
	private static final int ICON_INDEX = 3;
	
	public static Context readContext(Cursor cursor) {
		Integer id = readInteger(cursor, ID_INDEX);
		String name = readString(cursor, NAME_INDEX);
		int colour = cursor.getInt(COLOUR_INDEX);
		Integer iconResourceId = readInteger(cursor, ICON_INDEX);
		return new Context(id, name, colour, iconResourceId);
	}
	
	public static void writeContext(Cursor cursor, Context context) {
		// never write id since it's auto generated
		writeString(cursor, NAME_INDEX, context.name);
		cursor.updateInt(COLOUR_INDEX, context.colourIndex);
		writeInteger(cursor, ICON_INDEX, context.iconResource);
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
	
	public static void writeProject(Cursor cursor, Project project) {
		// never write id since it's auto generated
		writeString(cursor, NAME_INDEX, project.name);
		writeInteger(cursor, DEFAULT_CONTEXT_INDEX, project.defaultContextId);
		writeBoolean(cursor, ARCHIVED_INDEX, project.archived);
	}
	
	private static final int TASK_COUNT_INDEX = 1;
	
	public static SparseIntArray readCountArray(Cursor cursor) {
		
		SparseIntArray countMap = new SparseIntArray();
		while (cursor.next()) {
			countMap.put(cursor.getInt(ID_INDEX), cursor.getInt(TASK_COUNT_INDEX));
		}
		return countMap;
	}
	
	private static Integer readInteger(Cursor cursor, int index) {
		return (cursor.isNull(index) ? null : cursor.getInt(index));
	}
	
	private static Date readDate(Cursor cursor, int index) {
		return (cursor.isNull(index) ? null : new Date(cursor.getLong(index)));
	}

	private static Boolean readBoolean(Cursor cursor, int index) {
		return (cursor.getInt(index) == 1);
	}
	
	private static String readString(Cursor cursor, int index) {
		return (cursor.isNull(index) ? null : cursor.getString(index));
	}
	
	private static void writeDate(Cursor cursor, int index, Date date) {
		if (date == null) {
			cursor.updateToNull(index);
		} else {
			cursor.updateLong(index, date.getTime());
		}
	}
	
	private static void writeInteger(Cursor cursor, int index, Integer value) {
		if (value == null) {
			cursor.updateToNull(index);
		} else {
			cursor.updateInt(index, value);
		}
	}
	
	private static void writeBoolean(Cursor cursor, int index, boolean value) {
		cursor.updateInt(index, value ? 1 : 0);
	}
	
	private static void writeString(Cursor cursor, int index, String value) {
		if (value == null) {
			cursor.updateToNull(index);
		} else {
			cursor.updateString(index, value);
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
