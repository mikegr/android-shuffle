package org.dodgybits.shuffle.android.persistence.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class ContextProvider extends AbstractCollectionProvider {
	public static final String cContextTableName = "context";


	private static final String AUTHORITY = Shuffle.PACKAGE+".contextprovider";


	static final int CONTEXT_TASKS = 103;

	public ContextProvider() {
		super(AUTHORITY,"contexts",cContextTableName,Contexts.NAME, Contexts._ID, Contexts.CONTENT_URI,0,Contexts._ID,Contexts.NAME, Contexts.COLOUR,
				Contexts.ICON,Contexts.TRACKS_ID, Contexts.MODIFIED_DATE);
		uriMatcher.addURI(AUTHORITY, "contextTasks", CONTEXT_TASKS);
		restrictionBuilders.put(CONTEXT_TASKS, new CustomElementFilterRestrictionBuilder("context c, task t", "t.contextId = c._id", "c._id"));
		setDefaultSortOrder(Contexts.DEFAULT_SORT_ORDER);
	}


	/**
	 * Contexts table
	 */
	public static final class Contexts implements BaseColumns {
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/contexts");
		public static final Uri cContextTasksContentURI = Uri
				.parse("content://" + AUTHORITY + "/contextTasks");

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "name DESC";

		public static final String NAME = "name";
		public static final String COLOUR = "colour";
		public static final String ICON = "iconName";
		public static final String TRACKS_ID = "tracks_id";
		public static final String MODIFIED_DATE = "modified";

		/**
		 * Projection for all the columns of a context.
		 */
		public static final String[] cFullProjection = new String[] { _ID,
				NAME, COLOUR, ICON, TRACKS_ID, MODIFIED_DATE };

		public static final String TASK_COUNT = "count";
		/**
		 * Projection for fetching the task count for each context.
		 */
		public static final String[] cFullTaskProjection = new String[] { _ID,
				TASK_COUNT, };
	}



	
	


}
