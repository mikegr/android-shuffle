package org.dodgybits.shuffle.android.persistence.provider;

import android.net.Uri;

public class ContextProvider extends AbstractCollectionProvider {
	public static final String CONTEXT_TABLE_NAME = "context";

    public static final String UPDATE_INTENT = "org.dodgybits.shuffle.android.CONTEXT_UPDATE";

	private static final String AUTHORITY = Shuffle.PACKAGE + ".contextprovider";

	static final int CONTEXT_TASKS = 103;
	static final int ACTIVE_CONTEXTS = 104;
	
    private static final String URL_COLLECTION_NAME = "contexts";


	public ContextProvider() {
		super(
		        AUTHORITY,
		        URL_COLLECTION_NAME,
		        CONTEXT_TABLE_NAME,
                UPDATE_INTENT,
		        Contexts.NAME, 
		        Contexts._ID, 
		        Contexts.CONTENT_URI,
		        Contexts._ID,Contexts.NAME, 
		        Contexts.COLOUR,
				Contexts.ICON,
				Contexts.TRACKS_ID, 
				Contexts.MODIFIED_DATE,
				Contexts.DELETED);
		
		uriMatcher.addURI(AUTHORITY, "contextTasks", CONTEXT_TASKS);
		restrictionBuilders.put(CONTEXT_TASKS, 
		        new CustomElementFilterRestrictionBuilder(
		                "context c, task t", "t.contextId = c._id and t.deleted = 0", "c._id"));
        groupByBuilders.put(CONTEXT_TASKS, 
                new StandardGroupByBuilder("c._id"));
		setDefaultSortOrder(Contexts.DEFAULT_SORT_ORDER);
		uriMatcher.addURI(AUTHORITY, "activeContexts", ACTIVE_CONTEXTS);
		
		restrictionBuilders.put(ACTIVE_CONTEXTS, new CustomElementFilterRestrictionBuilder("context c", "c.deleted = 0", "c._id"));
		
	}


	/**
	 * Contexts table
	 */
	public static final class Contexts implements ShuffleTable {
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/contexts");
		public static final Uri CONTEXT_TASKS_CONTENT_URI = Uri
				.parse("content://" + AUTHORITY + "/contextTasks");

		public static final Uri ACTIVE_CONTEXTS = Uri
		.parse("content://" + AUTHORITY + "/activeContexts");

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "name DESC";

		public static final String NAME = "name";
		public static final String COLOUR = "colour";
		public static final String ICON = "iconName";
		/**
		 * Projection for all the columns of a context.
		 */
		public static final String[] FULL_PROJECTION = new String[] { _ID,
				NAME, COLOUR, ICON, TRACKS_ID, MODIFIED_DATE, DELETED };

		public static final String TASK_COUNT = "count";
		/**
		 * Projection for fetching the task count for each context.
		 */
		public static final String[] FULL_TASK_PROJECTION = new String[] { _ID,
				TASK_COUNT, };
	}

}
