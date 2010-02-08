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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.SparseIntArray;

import org.dodgybits.shuffle.android.core.util.StringUtils;
import org.dodgybits.shuffle.android.persistence.provider.Shuffle;

import java.util.*;

/**
 * Static methods for converting to/from Android representations of our
 * model to our own model classes.
 */
public class BindingUtils {
    private BindingUtils() {
		// deny
	}

    public static Project fetchProjectById(android.content.Context androidContext, Long projectId) {
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

    public static Context fetchContextById(android.content.Context androidContext, Long contextId) {
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
		writeLong(values, Shuffle.Tasks.MODIFIED_DATE, System.currentTimeMillis());
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
		Long id1 = readLong(cursor, ID_INDEX);
        cursor.moveToPosition(pos2);
        int positionValue2 = cursor.getInt(DISPLAY_ORDER_INDEX);
		Long id2 = readLong(cursor, ID_INDEX);

        Uri uri = ContentUris.withAppendedId(Shuffle.Tasks.CONTENT_URI, id1);
        ContentValues values = new ContentValues();
        writeInteger(values, Shuffle.Tasks.DISPLAY_ORDER, positionValue2);
        androidContext.getContentResolver().update(uri, values, null, null);

        uri = ContentUris.withAppendedId(Shuffle.Tasks.CONTENT_URI, id2);
        values = new ContentValues();
        writeInteger(values, Shuffle.Tasks.DISPLAY_ORDER, positionValue1);
        androidContext.getContentResolver().update(uri, values, null, null);
	}

	
	private static final int TASK_COUNT_INDEX = 1;
	
	public static SparseIntArray readCountArray(Cursor cursor) {
		
		SparseIntArray countMap = new SparseIntArray();
		while (cursor.moveToNext()) {
			countMap.put(cursor.getInt(ID_INDEX), cursor.getInt(TASK_COUNT_INDEX));
		}
		return countMap;
	}
	

}
