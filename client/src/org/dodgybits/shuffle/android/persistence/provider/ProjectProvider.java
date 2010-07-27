package org.dodgybits.shuffle.android.persistence.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class ProjectProvider extends AbstractCollectionProvider {
	   
	public static final String PROJECT_TABLE_NAME = "project";

    public static final String UPDATE_INTENT = "org.dodgybits.shuffle.android.PROJECT_UPDATE";

	private static final String AUTHORITY = Shuffle.PACKAGE+".projectprovider";

	static final int PROJECT_TASKS = 203;
	static final int ACTIVE_PROJECTS = 204;

	private static final String URL_COLLECTION_NAME = "projects";
	

	public ProjectProvider() {
		super(
		        AUTHORITY,
		        URL_COLLECTION_NAME, 
		        PROJECT_TABLE_NAME,
                UPDATE_INTENT,
		        Projects.NAME,
		        Projects._ID,
		        Projects.CONTENT_URI,
		        Projects._ID,Projects.NAME,
				Projects.DEFAULT_CONTEXT_ID, 
				Projects.TRACKS_ID, Projects.MODIFIED_DATE,
				Projects.PARALLEL, Projects.ARCHIVED, Projects.HIDDEN);
		
		makeSearchable(Projects._ID, Projects.NAME, Projects.NAME, Projects.NAME);
		uriMatcher.addURI(AUTHORITY, "projectTasks", PROJECT_TASKS);
		String idField = "p._id";
		String tables = "project p, task t";
		String restrictions = "t.projectId = p._id and t.hidden = 0";

		restrictionBuilders.put(PROJECT_TASKS, 
		        new CustomElementFilterRestrictionBuilder(
		                tables, restrictions, idField));
		groupByBuilders.put(PROJECT_TASKS, 
		        new StandardGroupByBuilder("p._id"));
		setDefaultSortOrder(Projects.DEFAULT_SORT_ORDER);

		uriMatcher.addURI(AUTHORITY, "activeProjects", ACTIVE_PROJECTS);
		
		restrictionBuilders.put(ACTIVE_PROJECTS, new CustomElementFilterRestrictionBuilder("project p", "p.hidden = 0", idField));
		
	}
	
    /**
     * Projects table
     */
    public static final class Projects implements BaseColumns {
        public static final String ARCHIVED = "archived";
        
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + URL_COLLECTION_NAME);
        public static final Uri PROJECT_TASKS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/projectTasks");
        public static final Uri ACTIVE_PROJECTS = Uri.parse("content://" + AUTHORITY + "/activeProjects");
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
        public static final String HIDDEN = "hidden";
        /**
         * Projection for all the columns of a project.
         */
        public static final String[] FULL_PROJECTION = new String[] {
                _ID,
                NAME,
                DEFAULT_CONTEXT_ID,
                TRACKS_ID,
                MODIFIED_DATE,
                PARALLEL,
                ARCHIVED,
                HIDDEN
        };
        /**
         * Projection for fetching the task count for each project.
         */
        public static final String[] FULL_TASK_PROJECTION = new String[] {
            _ID,
            TASK_COUNT,
        };
    }
    
}
