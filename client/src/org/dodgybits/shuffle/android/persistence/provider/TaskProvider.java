package org.dodgybits.shuffle.android.persistence.provider;

import android.content.ContentValues;
import android.net.Uri;

public class TaskProvider extends AbstractCollectionProvider {
	
	public static final String TASK_TABLE_NAME = "task";

	public static final String UPDATE_INTENT = "org.dodgybits.shuffle.android.TASK_UPDATE";

    private static final String URL_COLLECTION_NAME = "tasks";
	
	public TaskProvider() {
		super(AUTHORITY,
		        URL_COLLECTION_NAME,
		        TASK_TABLE_NAME,
		        UPDATE_INTENT,
		        Tasks.DESCRIPTION,Tasks._ID,
		        Tasks.CONTENT_URI,
		        Tasks.DESCRIPTION,
		        Tasks.DETAILS,Tasks.CONTEXT_ID,Tasks.PROJECT_ID,Tasks.CREATED_DATE,
				Tasks.MODIFIED_DATE,Tasks.START_DATE,Tasks.DUE_DATE,Tasks.TIMEZONE,
				Tasks.CAL_EVENT_ID,Tasks.DISPLAY_ORDER,Tasks.COMPLETE,
				Tasks.ALL_DAY,Tasks.HAS_ALARM,Tasks.TRACKS_ID, Tasks._ID);
		
		makeSearchable(Tasks._ID, 
		        Tasks.DESCRIPTION, Tasks.DETAILS,
		        Tasks.DESCRIPTION, Tasks.DETAILS);
		elementInserters.put(COLLECTION_MATCH_ID, new TaskInserter());
		setDefaultSortOrder(Tasks.DEFAULT_SORT_ORDER);
	}



	public static final String AUTHORITY = Shuffle.PACKAGE + ".taskprovider";
	/**
	 * Tasks table
	 */
	public static final class Tasks implements ShuffleTable {

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY+"/tasks");

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "start ASC, created ASC";

		public static final String DESCRIPTION = "description";
		public static final String DETAILS = "details";
		public static final String CONTEXT_ID = "contextId";
		public static final String PROJECT_ID = "projectId";
		public static final String CREATED_DATE = "created";
		public static final String MODIFIED_DATE = "modified";
		public static final String START_DATE = "start";
		public static final String DUE_DATE = "due";
		public static final String TIMEZONE = "timezone";
		public static final String CAL_EVENT_ID = "calEventId";
		public static final String DISPLAY_ORDER = "displayOrder";
		public static final String COMPLETE = "complete";
		public static final String ALL_DAY = "allDay";
		public static final String HAS_ALARM = "hasAlarm";
		public static final String TRACKS_ID = "tracks_id";

		/**
		 * Projection for all the columns of a task.
		 */
		public static final String[] cFullProjection = new String[] { _ID,
				DESCRIPTION, DETAILS, PROJECT_ID, CONTEXT_ID, CREATED_DATE,
				MODIFIED_DATE, START_DATE, DUE_DATE, TIMEZONE, CAL_EVENT_ID,
				DISPLAY_ORDER, COMPLETE, ALL_DAY, HAS_ALARM, TRACKS_ID };


	}



	private class TaskInserter extends ElementInserterImpl {

		public TaskInserter() {
			super(Tasks.DESCRIPTION);
		}
		

		@Override
		protected void addDefaultValues(ContentValues values) {
			Long now = System.currentTimeMillis();

			// Make sure that the fields are all set
			if (!values.containsKey(Tasks.CREATED_DATE)) {
				values.put(Tasks.CREATED_DATE, now);
			}
			if (!values.containsKey(Tasks.MODIFIED_DATE)) {
				values.put(Tasks.MODIFIED_DATE, now);
			}
			if (!values.containsKey(Tasks.DESCRIPTION)) {
				values.put(Tasks.DESCRIPTION, "");
			}
			if (!values.containsKey(Tasks.DETAILS)) {
				values.put(Tasks.DETAILS, "");
			}
			if (!values.containsKey(Tasks.DISPLAY_ORDER)) {
				values.put(Tasks.DISPLAY_ORDER, 0);
			}
			if (!values.containsKey(Tasks.COMPLETE)) {
				values.put(Tasks.COMPLETE, 0);
			}
		}
	}
}
