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

import java.util.Calendar;
import java.util.TimeZone;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.model.Context.Icon;
import org.dodgybits.android.shuffle.provider.Shuffle;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;

public class ModelUtils {
	private static final String cTag = "ModelUtils";

	private static final int AT_HOME_INDEX = 0;
	private static final int AT_WORK_INDEX = 1;
	private static final int AT_COMPUTER_INDEX = 2;
	private static final int ERRANDS_INDEX = 3;
	private static final int COMMUNICATION_INDEX = 4;
	private static final int READ_INDEX = 5;

	private static Context[] cPresetContexts = null;

	private static void initPresetContexts(Resources res) {
		if (cPresetContexts == null) {
			cPresetContexts = new Context[] {
					new Context(res.getText(R.string.context_athome).toString(), 5, createIcon("go_home", res)), // 0
					new Context(res.getText(R.string.context_atwork).toString(), 19, createIcon("system_file_manager", res)), // 1
					new Context(res.getText(R.string.context_online).toString(), 1, createIcon("applications_internet", res)), // 2
					new Context(res.getText(R.string.context_errands).toString(), 14, createIcon("applications_development", res)), // 3
					new Context(res.getText(R.string.context_contact).toString(), 22, createIcon("system_users", res)), // 4
					new Context(res.getText(R.string.context_read).toString(), 16, createIcon("format_justify_fill", res)), // 5
			};
		}
	}

	private static Icon createIcon(String iconName, Resources res) {
		return Icon.createIcon(iconName, res);
	}

	public static Context getSampleContext(Resources res) {
		initPresetContexts(res);
		return cPresetContexts[ERRANDS_INDEX];
	}
	
	public static int deleteCompletedTasks(
			android.content.Context androidContext) {
		int deletedRows = androidContext.getContentResolver().delete(
				Shuffle.Tasks.CONTENT_URI, Shuffle.Tasks.COMPLETE + " = 1",
				null);
		Log.d(cTag, "Deleted " + deletedRows + " completed tasks.");
		return deletedRows;
	}

	/**
	 * Clean out the current data and populate the database with a set of sample
	 * data.
	 */
	public static void createSampleData(android.content.Context androidContext,
			Handler handler) {
		cleanSlate(androidContext, null);
		initPresetContexts(androidContext.getResources());
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long now = cal.getTimeInMillis();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		long yesterday = cal.getTimeInMillis();
		cal.add(Calendar.DAY_OF_YEAR, 3);
		long twoDays = cal.getTimeInMillis();
		cal.add(Calendar.DAY_OF_YEAR, 5);
		long oneWeek = cal.getTimeInMillis();
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		long twoWeeks = cal.getTimeInMillis();
		
		Project sellBike = new Project("Sell old Powerbook", null, false);
		insertProject(androidContext, sellBike);
		insertTask(androidContext, 
				createTask("Backup data", null, 
						AT_COMPUTER_INDEX, sellBike, 
						now, now + DateUtils.HOUR_IN_MILLIS));
		insertTask(androidContext, 
				createTask("Reformat HD", "Install Leopard and updates", 
						AT_COMPUTER_INDEX, sellBike, 
						twoDays, twoDays + 2 * DateUtils.HOUR_IN_MILLIS));
		insertTask(androidContext, 
				createTask("Determine good price", "Take a look on ebay for similar systems", 
						AT_COMPUTER_INDEX, sellBike, 
						oneWeek, oneWeek + 2 * DateUtils.HOUR_IN_MILLIS));
		insertTask(androidContext, 
				createTask("Put up ad", AT_COMPUTER_INDEX, sellBike, twoWeeks));

		Project cleanGarage = new Project("Clean out garage", null, false);
		insertProject(androidContext, cleanGarage);
		insertTask(androidContext, 
				createTask("Sort out contents", "Split into keepers and junk", 
						AT_HOME_INDEX, cleanGarage, 
						yesterday, yesterday));
		insertTask(androidContext, 
				createTask("Advertise garage sale", "Local paper(s) and on craigslist", 
						AT_COMPUTER_INDEX, cleanGarage, 
						oneWeek, oneWeek + 2 * DateUtils.HOUR_IN_MILLIS));
		insertTask(androidContext, 
				createTask("Contact local charities", "See what they want or maybe just put in charity bins", 
						COMMUNICATION_INDEX, cleanGarage, 
						now, now));
		insertTask(androidContext, 
				createTask("Take rest to tip", "Hire trailer?", 
						ERRANDS_INDEX, cleanGarage, 
						now, now));

		Project skiTrip = new Project("Organise ski trip", null, false);
		insertProject(androidContext, skiTrip);
		insertTask(androidContext, 
				createTask("Send email to determine best week", null, 
						COMMUNICATION_INDEX, skiTrip, 
						now, now + 2 * DateUtils.HOUR_IN_MILLIS));
		insertTask(androidContext, 
				createTask("Look up package deals", 
						AT_COMPUTER_INDEX, skiTrip, 0L));
		insertTask(androidContext, 
				createTask("Book chalet", 
						AT_COMPUTER_INDEX, skiTrip, 0L)); 
		insertTask(androidContext, 
				createTask("Book flights", 
						AT_COMPUTER_INDEX, skiTrip, 0L));
		insertTask(androidContext, 
				createTask("Book hire car", 
						AT_COMPUTER_INDEX, skiTrip, 0L));
		insertTask(androidContext, 
				createTask("Get board waxed", 
						ERRANDS_INDEX, skiTrip, 0L));

		Project discussI8n = 
			new Project("Discuss internationalization",
				cPresetContexts[AT_WORK_INDEX].id, false);
		insertProject(androidContext, discussI8n);
		insertTask(androidContext, 
				createTask("Read up on options", null, 
						AT_COMPUTER_INDEX, discussI8n, 
						twoDays, twoDays + 2 * DateUtils.HOUR_IN_MILLIS));
		insertTask(androidContext, 
				createTask("Kickoff meeting", null, 
						COMMUNICATION_INDEX, discussI8n, 
						oneWeek, oneWeek + 2 * DateUtils.HOUR_IN_MILLIS));
		insertTask(androidContext, 
				createTask("Produce report", null, 
						AT_WORK_INDEX, discussI8n, 
						twoWeeks, twoWeeks + 2 * DateUtils.HOUR_IN_MILLIS));

		// a few stand alone tasks
		insertTask(androidContext, 
				createTask("Organise music collection", 
						AT_COMPUTER_INDEX, null, 0L));
		insertTask(androidContext, 
				createTask("Make copy of door keys", 
						ERRANDS_INDEX, null, yesterday));
		insertTask(androidContext, 
				createTask("Read Falling Man", 
						READ_INDEX, null, 0L));
		insertTask(androidContext, 
				createTask("Buy Tufte books", 
						ERRANDS_INDEX, null, oneWeek));
		if (handler != null)
			handler.sendEmptyMessage(0);
	}

	private static Task createTask(String description, int contextIndex, Project project, long start) {
		return createTask(description, null, contextIndex, project, start);
	}
	
	private static Task createTask(String description, String details, 
			int contextIndex, Project project, long start) {
		return createTask(description, null, contextIndex, project, start, start);

	}		

	private static int ORDER = 1;
	
	private static Task createTask(String description, String details, 
			int contextIndex, Project project, long start, long due) {
		Context context = contextIndex > -1 ? cPresetContexts[contextIndex] : null;
		long created = System.currentTimeMillis();
		long modified = created;
		String timezone = TimeZone.getDefault().getID();
		boolean allDay = false;
		boolean hasAlarms = false;
		boolean complete = false;
		return new Task(description, details, context, project, created, modified,
				start, due, timezone, allDay, hasAlarms, null, ORDER++, complete);  
	}
	
	/**
	 * Delete any existing projects, contexts and tasks and create the standard
	 * contexts.
	 * 
	 * @param androidContext
	 */
	public static void cleanSlate(android.content.Context androidContext,
			Handler handler) {
		initPresetContexts(androidContext.getResources());
		int deletedRows = androidContext.getContentResolver().delete(
				Shuffle.Tasks.CONTENT_URI, null, null);
		Log.d(cTag, "Deleted " + deletedRows + " tasks.");
		deletedRows = androidContext.getContentResolver().delete(
				Shuffle.Projects.CONTENT_URI, null, null);
		Log.d(cTag, "Deleted " + deletedRows + " projects.");
		deletedRows = androidContext.getContentResolver().delete(
				Shuffle.Contexts.CONTENT_URI, null, null);
		Log.d(cTag, "Deleted " + deletedRows + " contexts.");
		for (Context context : cPresetContexts) {
			insertContext(androidContext, context);
		}
		if (handler != null)
			handler.sendEmptyMessage(0);
	}

	private static boolean insertContext(
			android.content.Context androidContext,
			org.dodgybits.android.shuffle.model.Context context) {
		Uri uri = androidContext.getContentResolver().insert(
				Shuffle.Contexts.CONTENT_URI, null);
		long id = ContentUris.parseId(uri);
		Log.d(cTag, "Created context id=" + id + " uri=" + uri);
		context.id = id;
		ContentValues values = new ContentValues();
		BindingUtils.writeContext(values, context);
		int updatedRows = androidContext.getContentResolver().update(uri,
				values, null, null);
		return (updatedRows == 1);
	}

	private static boolean insertProject(
			android.content.Context androidContext, Project project) {
		Uri uri = androidContext.getContentResolver().insert(
				Shuffle.Projects.CONTENT_URI, null);
		long id = ContentUris.parseId(uri);
		Log.d(cTag, "Created context id=" + id + " uri=" + uri);
		project.id = id;
		ContentValues values = new ContentValues();
		BindingUtils.writeProject(values, project);
		int updatedRows = androidContext.getContentResolver().update(uri,
				values, null, null);
		return (updatedRows == 1);
	}

	private static boolean insertTask(android.content.Context androidContext,
			Task task) {
		Uri uri = androidContext.getContentResolver().insert(
				Shuffle.Tasks.CONTENT_URI, null);
		long id = ContentUris.parseId(uri);
		Log.d(cTag, "Created task id=" + id);
		task.id = id;
		ContentValues values = new ContentValues();
		BindingUtils.writeTask(values, task);
		int updatedRows = androidContext.getContentResolver().update(uri,
				values, null, null);
		return (updatedRows == 1);
	}

}
