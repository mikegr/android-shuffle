package org.dodgybits.shuffle.android.persistence.provider;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

public class TaskProvider extends AbstractCollectionProvider {
	
	public static final String TASK_TABLE_NAME = "task";

	public static final String UPDATE_INTENT = "org.dodgybits.shuffle.android.TASK_UPDATE";

    private static final String URL_COLLECTION_NAME = "tasks";
	
	public TaskProvider() {
        super(
                AUTHORITY,           // authority
                URL_COLLECTION_NAME, // collectionNamePlural
                TASK_TABLE_NAME,     // tableName
                UPDATE_INTENT,       // update intent action
                Tasks.DESCRIPTION,   // primary key
                BaseColumns._ID,     // id field
                Tasks.CONTENT_URI,   // content URI
                BaseColumns._ID,     // fields...
                Tasks.DESCRIPTION,
                Tasks.DETAILS,
                Tasks.CONTEXT_ID,
                Tasks.PROJECT_ID,
                Tasks.CREATED_DATE,
                Tasks.START_DATE,
                Tasks.DUE_DATE,
                Tasks.TIMEZONE,
                Tasks.CAL_EVENT_ID,
                Tasks.DISPLAY_ORDER,
                Tasks.COMPLETE,
                Tasks.ALL_DAY,
                Tasks.HAS_ALARM,
                ShuffleTable.TRACKS_ID, 
                ShuffleTable.MODIFIED_DATE,
                ShuffleTable.DELETED,
                ShuffleTable.ACTIVE
                );
		
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
		public static final String DEFAULT_SORT_ORDER = "due ASC, created ASC";

		public static final String DESCRIPTION = "description";
		public static final String DETAILS = "details";
		public static final String CONTEXT_ID = "contextId";
		public static final String PROJECT_ID = "projectId";
		public static final String CREATED_DATE = "created";
		public static final String START_DATE = "start";
		public static final String DUE_DATE = "due";
		public static final String TIMEZONE = "timezone";
		public static final String CAL_EVENT_ID = "calEventId";
		public static final String DISPLAY_ORDER = "displayOrder";
		public static final String COMPLETE = "complete";
		public static final String ALL_DAY = "allDay";
		public static final String HAS_ALARM = "hasAlarm";

		/**
		 * Projection for all the columns of a task.
		 */
		public static final String[] FULL_PROJECTION = new String[] { _ID,
				DESCRIPTION, DETAILS, PROJECT_ID, CONTEXT_ID, CREATED_DATE,
				MODIFIED_DATE, START_DATE, DUE_DATE, TIMEZONE, CAL_EVENT_ID,
				DISPLAY_ORDER, COMPLETE, ALL_DAY, HAS_ALARM, TRACKS_ID, DELETED, ACTIVE };


	}



	private class TaskInserter extends ElementInserterImpl {

		public TaskInserter() {
			super(Tasks.DESCRIPTION);
		}
		

		@Override
		protected void addDefaultValues(ContentValues values) {
		    super.addDefaultValues(values);
		    
		    // Make sure that the fields are all set
		    
			Long now = System.currentTimeMillis();
			if (!values.containsKey(Tasks.CREATED_DATE)) {
				values.put(Tasks.CREATED_DATE, now);
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
