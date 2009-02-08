package org.dodgybits.android.shuffle.provider;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Preferences;
import org.dodgybits.android.shuffle.provider.Shuffle.Contexts;
import org.dodgybits.android.shuffle.provider.Shuffle.Projects;
import org.dodgybits.android.shuffle.provider.Shuffle.Tasks;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Provides access to database for all task related data.
 */
public class ShuffleProvider extends ContentProvider {
    private static final String cTag = "ShuffleProvider";

    public static final String cDatabaseName = "shuffle.db";
    private static final int cDatabaseVersion = 9;
    
    static final String cTaskTableName = "task";
    static final String cProjectTableName = "project";
    static final String cContextTableName = "context";
    /* load task along with its optional project and context all in one query */
    static final String cTaskJoinTableNames = "task left outer join project on task.projectId = project._id " +
    		                                       "left outer join context on task.contextId = context._id";
    
    private static Map<String, String> sTaskListProjectMap;
    private static Map<String, String> sContextListProjectMap;
    private static Map<String, String> sProjectListProjectMap;
	
    private static final int TASKS = 1;
    private static final int TASK_ID = 2;
    private static final int TOP_TASKS = 3;
    private static final int INBOX_TASKS = 4;
    private static final int DUE_TASKS = 5;
    
    private static final int CONTEXTS = 101;
    private static final int CONTEXT_ID = 102;
    private static final int CONTEXT_TASKS = 103;
    
    private static final int PROJECTS = 201;
    private static final int PROJECT_ID = 202;
    private static final int PROJECT_TASKS = 203;
    
    private static final UriMatcher cUriMatcher;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, cDatabaseName, null, cDatabaseVersion);
        }
    	
        @Override
        public void onCreate(SQLiteDatabase db) {
        	Log.i(cTag, "Creating shuffle DB");
            db.execSQL("CREATE TABLE " + cContextTableName + " ("
            		+ "_id INTEGER PRIMARY KEY,"
                    + "name TEXT," 
                    + "colour INTEGER," 
                    + "iconId INTEGER" + ");");

            db.execSQL("CREATE TABLE " + cProjectTableName + " (" 
            		+ "_id INTEGER PRIMARY KEY,"
                    + "name TEXT," 
                    + "archived INTEGER,"
                    + "defaultContextId INTEGER"
                    + ");");
        	
            db.execSQL("CREATE TABLE " + cTaskTableName + " (" 
            		+ "_id INTEGER PRIMARY KEY,"
                    + "description TEXT," 
                    + "details TEXT," 
                    + "contextId INTEGER,"
                    + "projectId INTEGER,"
                    + "created INTEGER,"
                    + "modified INTEGER,"
                    + "due INTEGER,"
                    + "displayOrder INTEGER,"
                    + "complete INTEGER"
                    + ");");
            
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(cTag, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + cContextTableName);
            db.execSQL("DROP TABLE IF EXISTS " + cProjectTableName);
            db.execSQL("DROP TABLE IF EXISTS " + cTaskTableName);
            onCreate(db);
        }
    }
    
    private DatabaseHelper mOpenHelper;
    
    @Override
    public boolean onCreate() {
    	Log.i(cTag, "+onCreate");
    	mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        switch (cUriMatcher.match(uri)) {
        case TASKS:
            qb.setTables(cTaskJoinTableNames);
            qb.setProjectionMap(sTaskListProjectMap);
            break;
        case TASK_ID:
            qb.setTables(cTaskJoinTableNames);
            qb.setProjectionMap(sTaskListProjectMap);
            qb.appendWhere(cTaskTableName + "._id=" + uri.getPathSegments().get(1));
            break;
        case INBOX_TASKS:
            qb.setTables(cTaskJoinTableNames);
            qb.setProjectionMap(sTaskListProjectMap);
    		long lastCleanMS = Preferences.getLastInboxClean(getContext());
            qb.appendWhere("(projectId is null) or (created > " + lastCleanMS + ")");
            break;
        case DUE_TASKS:
            qb.setTables(cTaskJoinTableNames);
            qb.setProjectionMap(sTaskListProjectMap);
            
    		int mode = Integer.parseInt(uri.getPathSegments().get(1));
    		long startMS = 0L;
    		long endMS = getEndDate(mode);
            qb.appendWhere("complete = 0");
    		qb.appendWhere(" AND (due > " + startMS + ")");
    		qb.appendWhere(" AND (due < " + endMS + ")");
            break;
        case TOP_TASKS:
            qb.setTables(cTaskJoinTableNames);
            qb.setProjectionMap(sTaskListProjectMap);
            
            qb.appendWhere("(complete = 0) and (");
            qb.appendWhere("  projectId not null and");
            qb.appendWhere("  displayOrder = (select min(t2.displayOrder) from task t2 where task.projectId = t2.projectId and t2.complete = 0))");
            break;
        case CONTEXTS:
            qb.setTables(cContextTableName);
            qb.setProjectionMap(sContextListProjectMap);
            break;
        case CONTEXT_ID:
            qb.setTables(cContextTableName);
            qb.appendWhere("_id=" + uri.getPathSegments().get(1));
            break;
        case CONTEXT_TASKS:
        	return db.rawQuery("select c._id, count(*) count from context c, task t where t.contextId = c._id group by c._id" , null);
        case PROJECTS:
            qb.setTables(cProjectTableName);
            qb.setProjectionMap(sProjectListProjectMap);
            break;
        case PROJECT_ID:
            qb.setTables(cProjectTableName);
            qb.appendWhere("_id=" + uri.getPathSegments().get(1));
            break;
        case PROJECT_TASKS:
        	return db.rawQuery("select p._id, count(*) from project p, task t where t.projectId = p._id group by p._id" , null);
        default:
            throw new IllegalArgumentException("Unknown URL " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sort)) {
            switch (cUriMatcher.match(uri)) {
            case TASKS:
            case TASK_ID:
            case INBOX_TASKS:
            	orderBy = Shuffle.Tasks.DEFAULT_SORT_ORDER;
            	break;
            case TOP_TASKS:
            	orderBy = sTaskListProjectMap.get(Shuffle.Tasks.PROJECT_NAME) + " ASC";
            	break;
            case DUE_TASKS:
            	orderBy = Shuffle.Tasks.DUE_DATE + " ASC";
            	break;
            case CONTEXTS:
            case CONTEXT_ID:
            	orderBy = Shuffle.Contexts.DEFAULT_SORT_ORDER;
            	break;
            case PROJECTS:
            case PROJECT_ID:
            	orderBy = Shuffle.Projects.DEFAULT_SORT_ORDER;
            	break;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
            }
            	
    	} else {
            orderBy = sort;
        }
        
        if (Log.isLoggable(cTag, Log.DEBUG)) {
        	Log.d(cTag, "Executing " + selection + " with args " + Arrays.toString(selectionArgs) +
        			" ORDER BY " + orderBy);
        }
        
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
    
    private long getEndDate(int mode) {
    	long endMS = 0L;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);    		
		switch (mode) {
		case Shuffle.Tasks.DAY_MODE:
			cal.roll(Calendar.DAY_OF_YEAR, 1);
			endMS = cal.getTimeInMillis();
			break;
		case Shuffle.Tasks.WEEK_MODE:
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			cal.roll(Calendar.DAY_OF_YEAR, 7);
			endMS = cal.getTimeInMillis();
			break;
		case Shuffle.Tasks.MONTH_MODE:
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.roll(Calendar.MONTH, 1);
			endMS = cal.getTimeInMillis();
			break;
		}
		if (Log.isLoggable(cTag, Log.INFO)) {
			Log.i(cTag, "Due date ends " + new Date(endMS) + "(" + endMS + ")");
		}
		return endMS;
    }

    @Override
    public String getType(Uri uri) {
        switch (cUriMatcher.match(uri)) {
        case TASKS:
        case INBOX_TASKS:
        case DUE_TASKS:
            return Shuffle.Tasks.CONTENT_TYPE;
        case TASK_ID:
            return Shuffle.Tasks.CONTENT_ITEM_TYPE;
        case CONTEXTS:
            return Shuffle.Contexts.CONTENT_TYPE;
        case CONTEXT_ID:
            return Shuffle.Contexts.CONTENT_ITEM_TYPE;
        case PROJECTS:
            return Shuffle.Projects.CONTENT_TYPE;
        case PROJECT_ID:
            return Shuffle.Projects.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown Uri " + uri);
        }
    }

    @Override
    public Uri insert(Uri url, ContentValues initialValues) {
        long rowID;
        ContentValues values;
        Resources r;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (cUriMatcher.match(url)) {
        case TASKS:
        case DUE_TASKS:
        case TOP_TASKS:
        case INBOX_TASKS:
            Long now = Long.valueOf(System.currentTimeMillis());
            r = getContext().getResources();

            // Make sure that the fields are all set
            if (! values.containsKey(Shuffle.Tasks.CREATED_DATE)) {
                values.put(Shuffle.Tasks.CREATED_DATE, now);
            }
            if (! values.containsKey(Shuffle.Tasks.MODIFIED_DATE)) {
                values.put(Shuffle.Tasks.MODIFIED_DATE, now);
            }
            if (! values.containsKey(Shuffle.Tasks.DESCRIPTION)) {
                values.put(Shuffle.Tasks.DESCRIPTION, r.getString(R.string.initial_text));
            }
            if (! values.containsKey(Shuffle.Tasks.DETAILS)) {
                values.put(Shuffle.Tasks.DETAILS, "");
            }
            if (! values.containsKey(Shuffle.Tasks.DISPLAY_ORDER)) {
            	values.put(Shuffle.Tasks.DISPLAY_ORDER, 0);
            }
            if (! values.containsKey(Shuffle.Tasks.COMPLETE)) {
            	values.put(Shuffle.Tasks.COMPLETE, 0);
            }

            rowID = db.insert(cTaskTableName, sTaskListProjectMap.get(Shuffle.Tasks.DESCRIPTION), values);
            if (rowID > 0) {
                Uri uri = ContentUris.withAppendedId(Shuffle.Tasks.CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(uri, null);
                return uri;
            }
        	break;
        	        	
        case PROJECTS:
            r = android.content.res.Resources.getSystem();
            if (values.containsKey(Shuffle.Projects.NAME) == false) {
                values.put(Shuffle.Projects.NAME, r.getString(android.R.string.untitled));
            }
            
            rowID = db.insert(cProjectTableName, sProjectListProjectMap.get(Shuffle.Projects.NAME), values);
            if (rowID > 0) {
                Uri uri = ContentUris.withAppendedId(Shuffle.Projects.CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(uri, null);
                return uri;
            }
        	break;
        	
        case CONTEXTS:
            r = Resources.getSystem();
            if (values.containsKey(Shuffle.Contexts.NAME) == false) {
                values.put(Shuffle.Contexts.NAME, r.getString(android.R.string.untitled));
            }
            
            rowID = db.insert(cContextTableName, sContextListProjectMap.get(Shuffle.Contexts.NAME), values);
            if (rowID > 0) {
                Uri uri = ContentUris.withAppendedId(Shuffle.Contexts.CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(uri, null);
                return uri;
            }
        	break;
        	    	
    	default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }


        throw new SQLException("Failed to insert row into " + url);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    	
        int count;
        switch (cUriMatcher.match(uri)) {
        case TASKS:
        case DUE_TASKS:
        case TOP_TASKS:
        case INBOX_TASKS:
            count = db.delete(cTaskTableName, where, whereArgs);
            break;
        case TASK_ID:
            String taskId = uri.getPathSegments().get(1);
            count = db
                    .delete(cTaskTableName, Tasks._ID + "="
                            + taskId
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case CONTEXTS:
            count = db.delete(cContextTableName, where, whereArgs);
            break;
        case CONTEXT_ID:
            String contextId = uri.getPathSegments().get(1);
            count = db
                    .delete(cContextTableName, Contexts._ID + "="
                            + contextId
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case PROJECTS:
            count = db.delete(cProjectTableName, where, whereArgs);
            break;
        case PROJECT_ID:
            String projectId = uri.getPathSegments().get(1);
            count = db
                    .delete(cProjectTableName, Projects._ID + "="
                            + projectId
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        String segment;
        switch (cUriMatcher.match(uri)) {
        case TASKS:
        case DUE_TASKS:
        case TOP_TASKS:
       	case INBOX_TASKS:
            count = db.update(cTaskTableName, values, where, whereArgs);
            break;
        case TASK_ID:
            segment = uri.getPathSegments().get(1);
            count = db
                    .update(cTaskTableName, values, "_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case CONTEXTS:
            count = db.update(cContextTableName, values, where, whereArgs);
            break;
        case CONTEXT_ID:
            segment = uri.getPathSegments().get(1);
            count = db
                    .update(cContextTableName, values, "_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case PROJECTS:
            count = db.update(cProjectTableName, values, where, whereArgs);
            break;
        case PROJECT_ID:
            segment = uri.getPathSegments().get(1);
            count = db
                    .update(cProjectTableName, values, "_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URL " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        cUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        cUriMatcher.addURI(Shuffle.PACKAGE, "tasks", TASKS);
        cUriMatcher.addURI(Shuffle.PACKAGE, "tasks/#", TASK_ID);
        cUriMatcher.addURI(Shuffle.PACKAGE, "inboxTasks", INBOX_TASKS);
        cUriMatcher.addURI(Shuffle.PACKAGE, "dueTasks/#", DUE_TASKS);
        cUriMatcher.addURI(Shuffle.PACKAGE, "topTasks", TOP_TASKS);
        cUriMatcher.addURI(Shuffle.PACKAGE, "contexts", CONTEXTS);
        cUriMatcher.addURI(Shuffle.PACKAGE, "contexts/#", CONTEXT_ID);
        cUriMatcher.addURI(Shuffle.PACKAGE, "contextTasks", CONTEXT_TASKS);
        cUriMatcher.addURI(Shuffle.PACKAGE, "projects", PROJECTS);
        cUriMatcher.addURI(Shuffle.PACKAGE, "projects/#", PROJECT_ID);
        cUriMatcher.addURI(Shuffle.PACKAGE, "projectTasks", PROJECT_TASKS);
        
        // ugh - these mapping lists smell bad. Are they even needed?
        sTaskListProjectMap = new HashMap<String, String>();
        sTaskListProjectMap.put(Shuffle.Tasks._ID, cTaskTableName + "._id");
        sTaskListProjectMap.put(Shuffle.Tasks.DESCRIPTION, cTaskTableName + ".description");
        sTaskListProjectMap.put(Shuffle.Tasks.DETAILS, cTaskTableName + ".details");
        sTaskListProjectMap.put(Shuffle.Tasks.CONTEXT_ID, cTaskTableName + ".contextId");
        sTaskListProjectMap.put(Shuffle.Tasks.PROJECT_ID, cTaskTableName + ".projectId");
        sTaskListProjectMap.put(Shuffle.Tasks.CREATED_DATE, cTaskTableName + ".created");
        sTaskListProjectMap.put(Shuffle.Tasks.MODIFIED_DATE, cTaskTableName + ".modified");
        sTaskListProjectMap.put(Shuffle.Tasks.DUE_DATE, cTaskTableName + ".due");
        sTaskListProjectMap.put(Shuffle.Tasks.DISPLAY_ORDER, cTaskTableName + ".displayOrder");
        sTaskListProjectMap.put(Shuffle.Tasks.COMPLETE, cTaskTableName + ".complete");
        sTaskListProjectMap.put(Shuffle.Tasks.PROJECT_NAME, cProjectTableName + ".name");
        sTaskListProjectMap.put(Shuffle.Tasks.PROJECT_DEFAULT_CONTEXT_ID, cProjectTableName + ".defaultContextId");
        sTaskListProjectMap.put(Shuffle.Tasks.PROJECT_ARCHIVED, cProjectTableName + ".archived");
        sTaskListProjectMap.put(Shuffle.Tasks.CONTEXT_NAME, cContextTableName + ".name");
        sTaskListProjectMap.put(Shuffle.Tasks.CONTEXT_COLOUR, cContextTableName + ".colour");
        sTaskListProjectMap.put(Shuffle.Tasks.CONTEXT_ICON, cContextTableName + ".iconId");

        sContextListProjectMap = new HashMap<String, String>();
        sContextListProjectMap.put(Shuffle.Contexts._ID, cContextTableName + "._id");
        sContextListProjectMap.put(Shuffle.Contexts.NAME, cContextTableName + ".name");
        sContextListProjectMap.put(Shuffle.Contexts.COLOUR, cContextTableName + ".colour");
        sContextListProjectMap.put(Shuffle.Contexts.ICON, cContextTableName + ".iconId");

        sProjectListProjectMap = new HashMap<String, String>();
        sProjectListProjectMap.put(Shuffle.Projects._ID, cProjectTableName + "._id");
        sProjectListProjectMap.put(Shuffle.Projects.NAME, cProjectTableName + ".name");
        sProjectListProjectMap.put(Shuffle.Projects.DEFAULT_CONTEXT_ID, cProjectTableName + ".defaultContextId");
        sProjectListProjectMap.put(Shuffle.Projects.ARCHIVED, cProjectTableName + ".archived");
    }
}
