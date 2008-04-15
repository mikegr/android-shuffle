package org.dodgybits.android.shuffle.util;

import java.util.Calendar;
import java.util.Date;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class ModelUtils {
	private static final String cTag = "ModelUtils";

	private static final int AT_HOME_INDEX = 0;
	private static final int AT_WORK_INDEX = 1;
	private static final int AT_COMPUTER_INDEX = 2;
	private static final int ERRANDS_INDEX = 3;
	private static final int COMMUNICATION_INDEX = 4;
	private static final int READ_INDEX = 5;
	
	public static final Context[] cPresetContexts = new Context[] {
		new Context("At home", 11, R.drawable.go_home), //0
		new Context("At work", 1, android.R.drawable.contact_work_location), //1
		new Context("Online", 7, R.drawable.applications_internet), //2
		new Context("Errands", 14, R.drawable.applications_development), //3
		new Context("Contact", 22, android.R.drawable.stat_sys_phone_call), //4
		new Context("Read", 4, R.drawable.format_justify_fill), //5
	};
			
    public static int deleteCompletedTasks(android.content.Context androidContext) {
		int deletedRows = androidContext.getContentResolver().delete(
				Shuffle.Tasks.CONTENT_URI, 
				Shuffle.Tasks.COMPLETE + " = 1", null);
		Log.d(cTag, "Deleted " + deletedRows + " completed tasks.");
		return deletedRows;
    }
    
    /**
     * Clean out the current data and populate the database
     * with a set of sample data.
     */
    public static void createSampleData(android.content.Context androidContext, Handler handler) {
    	cleanSlate(androidContext, null);
    	
    	Date now = new Date();
    	Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
    	cal.add(Calendar.DAY_OF_YEAR, -1);
    	Date yesterday = cal.getTime();
    	cal.add(Calendar.DAY_OF_YEAR, 3);
    	Date twoDays = cal.getTime();
    	cal.add(Calendar.DAY_OF_YEAR, 5);
    	Date oneWeek = cal.getTime();
    	cal.add(Calendar.WEEK_OF_YEAR, 1);
    	Date twoWeeks = cal.getTime();
    	int i = 1;
    	    	
    	Project sellBike = new Project("Sell old laptop", null, false);
    	insertProject(androidContext, sellBike);
    	insertTask(androidContext, new Task("Backup data", null, cPresetContexts[AT_COMPUTER_INDEX], sellBike, now, now, now, i++, false)); 
    	insertTask(androidContext, new Task("Reformat HD", null, cPresetContexts[AT_COMPUTER_INDEX], sellBike, now, now, twoDays, i++, false)); 
    	insertTask(androidContext, new Task("Determine good price", null, cPresetContexts[AT_COMPUTER_INDEX], sellBike, now, now, oneWeek, i++, false)); 
    	insertTask(androidContext, new Task("Put up ad", null, cPresetContexts[AT_COMPUTER_INDEX], sellBike, now, now, twoWeeks, i++, false)); 

    	i = 1;
    	Project cleanGarage = new Project("Clean out garage", null, false);
    	insertProject(androidContext, cleanGarage);
    	insertTask(androidContext, new Task("Sort out contents", "Split into keepers & junk", cPresetContexts[AT_HOME_INDEX], cleanGarage, now, now, yesterday, i++, false)); 
    	insertTask(androidContext, new Task("Advertise garage sale", null, cPresetContexts[AT_COMPUTER_INDEX], cleanGarage, now, now, oneWeek, i++, false)); 
    	insertTask(androidContext, new Task("Contact charities", "See what they want", cPresetContexts[COMMUNICATION_INDEX], cleanGarage, now, now, null, i++, false)); 
    	insertTask(androidContext, new Task("Take rest to tip", "Hire trailer?", cPresetContexts[ERRANDS_INDEX], cleanGarage, now, now, null, i++, false)); 

    	i = 1;
    	Project skiTrip = new Project("Organise ski trip", null, false);
    	insertProject(androidContext, skiTrip);
    	insertTask(androidContext, new Task("Send email to determine\nbest week", null, cPresetContexts[COMMUNICATION_INDEX], skiTrip, now, now, now, i++, false)); 
    	insertTask(androidContext, new Task("Look up deals", null, cPresetContexts[AT_COMPUTER_INDEX], skiTrip, now, now, twoDays, i++, false)); 
    	insertTask(androidContext, new Task("Book chalet", null, cPresetContexts[AT_COMPUTER_INDEX], skiTrip, now, now, null, i++, false)); 
    	insertTask(androidContext, new Task("Book flights", null, cPresetContexts[AT_COMPUTER_INDEX], skiTrip, now, now, null, i++, false)); 
    	insertTask(androidContext, new Task("Book hire car", null, cPresetContexts[AT_COMPUTER_INDEX], skiTrip, now, now, null, i++, false)); 
    	insertTask(androidContext, new Task("Get board waxed", null, cPresetContexts[ERRANDS_INDEX], skiTrip, now, now, twoWeeks, i++, false)); 

    	i = 1;
    	Project discussI8n = new Project("Discuss i8n", cPresetContexts[AT_WORK_INDEX].id, false);
    	insertProject(androidContext, discussI8n);
    	insertTask(androidContext, new Task("Read up on options", null, cPresetContexts[AT_COMPUTER_INDEX], discussI8n, now, now, twoDays, i++, false)); 
    	insertTask(androidContext, new Task("Kickoff meeting", null, cPresetContexts[COMMUNICATION_INDEX], discussI8n, now, now, oneWeek, i++, false)); 
    	insertTask(androidContext, new Task("Produce report", null, cPresetContexts[AT_WORK_INDEX], discussI8n, now, now, twoWeeks, i++, false)); 

    	// a few stand alone tasks
    	insertTask(androidContext, new Task("Organise music collection", null, cPresetContexts[AT_COMPUTER_INDEX], null, now, now, null, -1, false)); 
    	insertTask(androidContext, new Task("Make copy of door keys", null, cPresetContexts[ERRANDS_INDEX], null, now, now, yesterday, -1, false)); 
    	insertTask(androidContext, new Task("Read Falling Man", null, cPresetContexts[READ_INDEX], null, now, now, null, -1, false)); 
    	insertTask(androidContext, new Task("Buy Tufte books", null, cPresetContexts[ERRANDS_INDEX], null, now, now, oneWeek, -1, false)); 
    	
    	if (handler != null) handler.sendEmptyMessage(0);
    }
    
    /**
     * Delete any existing projects, contexts and tasks and
     * create the standard contexts.
     * 
     * @param androidContext
     */
    public static void cleanSlate(android.content.Context androidContext, Handler handler) {
    	int deletedRows = androidContext.getContentResolver().delete(Shuffle.Tasks.CONTENT_URI, null, null);
		Log.d(cTag, "Deleted " + deletedRows + " tasks.");
		Uri uri = ContentUris.withAppendedId(Shuffle.TaskContacts.CONTENT_URI, 0);			
    	deletedRows = androidContext.getContentResolver().delete(uri, null, null);
		Log.d(cTag, "Deleted " + deletedRows + " task contacts.");
		deletedRows = androidContext.getContentResolver().delete(Shuffle.Projects.CONTENT_URI, null, null);
		Log.d(cTag, "Deleted " + deletedRows + " projects.");
    	deletedRows = androidContext.getContentResolver().delete(Shuffle.Contexts.CONTENT_URI, null, null);
		Log.d(cTag, "Deleted " + deletedRows + " contexts.");
		for (Context context : cPresetContexts) {
			insertContext(androidContext, context);
		}
    	if (handler != null) handler.sendEmptyMessage(0);
    }
    
    private static boolean insertContext(android.content.Context androidContext, org.dodgybits.android.shuffle.model.Context context) {
        Uri uri = androidContext.getContentResolver().insert(Shuffle.Contexts.CONTENT_URI, null);
        int id = (int)ContentUris.parseId(uri);
        Log.d(cTag, "Created context id=" + id + " uri=" + uri);
        context.id = id;
        Cursor cursor = androidContext.getContentResolver().query(uri, 
        		Shuffle.Contexts.cFullProjection, null, null, null);
        cursor.next();
        BindingUtils.writeContext(cursor, context);
        boolean success = cursor.commitUpdates();
        cursor.close();
        return success;
    }
    
    private static boolean insertProject(android.content.Context androidContext, Project project) {
        Uri uri = androidContext.getContentResolver().insert(Shuffle.Projects.CONTENT_URI, null);
        int id = (int)ContentUris.parseId(uri);
        Log.d(cTag, "Created context id=" + id + " uri=" + uri);
        project.id = id;
        Cursor cursor = androidContext.getContentResolver().query(uri, 
        		Shuffle.Projects.cFullProjection, null, null, null);
        cursor.next();
        BindingUtils.writeProject(cursor, project);
        boolean success = cursor.commitUpdates();
        cursor.close();
        return success;
    }
    
    private static boolean insertTask(android.content.Context androidContext, Task task) {
        Uri uri = androidContext.getContentResolver().insert(Shuffle.Tasks.CONTENT_URI, null);
        int id = (int)ContentUris.parseId(uri);
        Log.d(cTag, "Created task id=" + id);
        task.id = id;
        Cursor cursor = androidContext.getContentResolver().query(uri, 
        		Shuffle.Tasks.cExpandedProjection, null, null, null);
        cursor.next();
        BindingUtils.writeTask(cursor, task);
        boolean success = cursor.commitUpdates();
        cursor.close();
        return success;
    }
    	
}
