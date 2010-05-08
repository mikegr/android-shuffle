package org.dodgybits.shuffle.android.persistence.provider;

import java.util.HashMap;
import java.util.Map;

import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

public class ProjectProvider extends AbstractCollectionProvider {
	   
	public static final String PROJECT_TABLE_NAME = "project";




	/**
     * Projects table
     */
    public static final class Projects implements BaseColumns {
		public static final String ARCHIVED = "archived";
		
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/"+URL_COLLECTION_NAME);
		public static final Uri cProjectTasksContentURI = Uri.parse("content://" + AUTHORITY + "/projectTasks");
		public static final String DEFAULT_CONTEXT_ID = "defaultContextId";
		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "name DESC";
		public static final String MODIFIED_DATE = "modified";
		public static final String NAME = "name";
		
		public static final String PARALLEL = "parallel";
		public static final String TASK_COUNT = "count";
		public static final String TRACKS_ID = "tracks_id";
        /**
         * Projection for all the columns of a project.
         */
        public static final String[] cFullProjection = new String[] {
                _ID,
                NAME,
                DEFAULT_CONTEXT_ID,
                TRACKS_ID,
                MODIFIED_DATE,
                PARALLEL,
                ARCHIVED
        };
        /**
         * Projection for fetching the task count for each project.
         */
        public static final String[] cFullTaskProjection = new String[] {
            _ID,
            TASK_COUNT,
        };
    }
	
	private static final String AUTHORITY = Shuffle.PACKAGE+".projectprovider";

	static final int PROJECT_TASKS = 203;

	private static final String URL_COLLECTION_NAME = "projects";
	

	public ProjectProvider() {
		super(AUTHORITY,URL_COLLECTION_NAME, PROJECT_TABLE_NAME,Projects.NAME,Projects._ID,Projects.CONTENT_URI,0, Projects._ID,Projects.NAME,
				Projects.DEFAULT_CONTEXT_ID, Projects.TRACKS_ID, Projects.MODIFIED_DATE,Projects.PARALLEL, Projects.ARCHIVED);
		makeSearchable(Projects._ID, Projects.NAME, Projects.NAME, Projects.NAME);
		uriMatcher.addURI(AUTHORITY, "projectTasks", PROJECT_TASKS);
		String idField = "p._id";
		String tables = "project p, task t";
		String restrictions = "t.projectId = p._id";

		restrictionBuilders.put(PROJECT_TASKS, new CustomElementFilterRestrictionBuilder(tables, restrictions, idField));
		setDefaultSortOrder(Projects.DEFAULT_SORT_ORDER);

	}

}
