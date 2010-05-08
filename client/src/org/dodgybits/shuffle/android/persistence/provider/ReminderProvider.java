package org.dodgybits.shuffle.android.persistence.provider;

import android.net.Uri;

public class ReminderProvider extends AbstractCollectionProvider {
	private static final String AUTHORITY = Shuffle.PACKAGE+".reminderprovider";

	/**
	 * Reminders table
	 */
	public static final class Reminders implements ShuffleTable {
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/reminders");


		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "minutes DESC";

		/**
		 * The task the reminder belongs to
		 * <P>
		 * Type: INTEGER (foreign key to the task table)
		 * </P>
		 */
		public static final String TASK_ID = "taskId";

		/**
		 * The minutes prior to the event that the alarm should ring. -1
		 * specifies that we should use the default value for the system.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String MINUTES = "minutes";

		public static final int MINUTES_DEFAULT = -1;

		/**
		 * The alarm method.
		 */
		public static final String METHOD = "method";

		public static final int METHOD_DEFAULT = 0;
		public static final int METHOD_ALERT = 1;

		/**
		 * Projection for all the columns of a context.
		 */
		public static final String[] cFullProjection = new String[] { _ID,
				MINUTES, METHOD, };

		public static final int MINUTES_INDEX = 1;
		public static final int METHOD_INDEX = 2;
	}

	public static final String cReminderTableName = "Reminder";
	
	public ReminderProvider() {
		super(AUTHORITY,"reminders",cReminderTableName,Reminders.METHOD,Reminders._ID,Reminders.CONTENT_URI,0,Reminders._ID,Reminders.TASK_ID,Reminders.MINUTES, Reminders.METHOD );
		setDefaultSortOrder(Reminders.DEFAULT_SORT_ORDER);
	}



}
