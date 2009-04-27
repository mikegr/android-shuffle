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
import java.util.Date;

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
					new Context("At home", 5, createIcon("go_home", res)), // 0
					new Context("At work", 19, createIcon("system_file_manager", res)), // 1
					new Context("Online", 1, createIcon("applications_internet", res)), // 2
					new Context("Errands", 14, createIcon("applications_development", res)), // 3
					new Context("Contact", 22, createIcon("system_users", res)), // 4
					new Context("Read", 16, createIcon("format_justify_fill", res)), // 5
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
		Date now = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		Date yesterday = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, 3);
		Date twoDays = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, 5);
		Date oneWeek = cal.getTime();
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		Date twoWeeks = cal.getTime();
		int i = 1;
		
		Project sellBike = new Project("Sell old Powerbook", null, false);
		insertProject(androidContext, sellBike);
		insertTask(androidContext, new Task("Backup data", null,
				cPresetContexts[AT_COMPUTER_INDEX], sellBike, now, now, 
				now, new Date(now.getTime() + DateUtils.HOUR_IN_MILLIS), false, false,
				i++, false));
		insertTask(androidContext, new Task("Reformat HD", "Install Leopard and updates",
				cPresetContexts[AT_COMPUTER_INDEX], sellBike, now, now,
				twoDays, new Date(twoDays.getTime() + 2 * DateUtils.HOUR_IN_MILLIS), false, false,
				i++, false));
		insertTask(androidContext, new Task("Determine good price", 
				"Take a look on ebay for similar systems",
				cPresetContexts[AT_COMPUTER_INDEX], sellBike, now, now,
				oneWeek, new Date(oneWeek.getTime() + 2 * DateUtils.HOUR_IN_MILLIS), false, false,
				i++, false));
		insertTask(androidContext, new Task("Put up ad", null,
				cPresetContexts[AT_COMPUTER_INDEX], sellBike, now, now,
				twoWeeks, new Date(twoWeeks.getTime() + 2 * DateUtils.HOUR_IN_MILLIS), false, false,
				i++, false));

		i = 1;
		Project cleanGarage = new Project("Clean out garage", null, false);
		insertProject(androidContext, cleanGarage);
		insertTask(androidContext, new Task("Sort out contents",
				"Split into keepers and junk", cPresetContexts[AT_HOME_INDEX],
				cleanGarage, now, now, 
				yesterday, new Date(oneWeek.getTime() + 2 * DateUtils.HOUR_IN_MILLIS), false, false,
				i++, false));
		insertTask(androidContext, new Task("Advertise garage sale", 
				"Local paper(s) and on craigslist",
				cPresetContexts[AT_COMPUTER_INDEX], 
				cleanGarage, now, now,
				oneWeek, new Date(oneWeek.getTime() + 2 * DateUtils.HOUR_IN_MILLIS), false, false,
				i++, false));
		insertTask(androidContext, new Task("Contact local charities",
				"See what they want or maybe just put in charity bins", 
				cPresetContexts[COMMUNICATION_INDEX],
				cleanGarage, now, now, 
				null, null, false, false,
				i++, false));
		insertTask(androidContext, new Task("Take rest to tip",
				"Hire trailer?", cPresetContexts[ERRANDS_INDEX], cleanGarage,
				now, now, 
				null, null, false, false,
				i++, false));

		i = 1;
		Project skiTrip = new Project("Organise ski trip", null, false);
		insertProject(androidContext, skiTrip);
		insertTask(androidContext, new Task(
				"Send email to determine best week", null,
				cPresetContexts[COMMUNICATION_INDEX], skiTrip, now, now, 
				now, new Date(now.getTime() + 2 * DateUtils.HOUR_IN_MILLIS), false, false,
				i++, false));
		insertTask(androidContext, new Task("Look up package deals", null,
				cPresetContexts[AT_COMPUTER_INDEX], skiTrip, now, now, 
				twoDays, twoDays, true, false,
				i++, false));
		insertTask(androidContext, new Task("Book chalet", null,
				cPresetContexts[AT_COMPUTER_INDEX], skiTrip, now, now, 
				null, null, false, false,
				i++, false));
		insertTask(androidContext, new Task("Book flights", null,
				cPresetContexts[AT_COMPUTER_INDEX], skiTrip, now, now, 
				null, null, false, false,
				i++, false));
		insertTask(androidContext, new Task("Book hire car", null,
				cPresetContexts[AT_COMPUTER_INDEX], skiTrip, now, now, 
				null, null, false, false,
				i++, false));
		insertTask(androidContext, new Task("Get board waxed", null,
				cPresetContexts[ERRANDS_INDEX], skiTrip, now, now, 
				twoWeeks, new Date(twoWeeks.getTime() + 2 * DateUtils.HOUR_IN_MILLIS), false, false,
				i++, false));

		i = 1;
		Project discussI8n = new Project("Discuss internationalization",
				cPresetContexts[AT_WORK_INDEX].id, false);
		insertProject(androidContext, discussI8n);
		insertTask(androidContext, new Task("Read up on options", null,
				cPresetContexts[AT_COMPUTER_INDEX], discussI8n, now, now,
				twoDays, new Date(twoDays.getTime() + 2 * DateUtils.HOUR_IN_MILLIS), false, false,
				i++, false));
		insertTask(androidContext, new Task("Kickoff meeting", null,
				cPresetContexts[COMMUNICATION_INDEX], discussI8n, now, now,
				oneWeek, new Date(oneWeek.getTime() + 2 * DateUtils.HOUR_IN_MILLIS), false, false,
				i++, false));
		insertTask(androidContext, new Task("Produce report", null,
				cPresetContexts[AT_WORK_INDEX], discussI8n, now, now, 
				twoWeeks, new Date(twoWeeks.getTime() + 2 * DateUtils.HOUR_IN_MILLIS), false, false,
				i++, false));

		// a few stand alone tasks
		insertTask(androidContext, new Task("Organise music collection", null,
				cPresetContexts[AT_COMPUTER_INDEX], null, now, now, 
				null, null, false, false,
				-1, false));
		insertTask(androidContext, new Task("Make copy of door keys", null,
				cPresetContexts[ERRANDS_INDEX], null, now, now, 
				yesterday, new Date(yesterday.getTime() + 2 * DateUtils.HOUR_IN_MILLIS), false, false,
				-1, false));
		insertTask(androidContext, new Task("Read Falling Man", null,
				cPresetContexts[READ_INDEX], null, now, now, 
				null, null, false, false,
				-1, false));
		insertTask(androidContext, new Task("Buy Tufte books", null,
				cPresetContexts[ERRANDS_INDEX], null, now, now, 
				oneWeek, new Date(oneWeek.getTime() + 2 * DateUtils.HOUR_IN_MILLIS), false, false,
				-1, false));

		if (handler != null)
			handler.sendEmptyMessage(0);
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
		int id = (int) ContentUris.parseId(uri);
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
		int id = (int) ContentUris.parseId(uri);
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
		int id = (int) ContentUris.parseId(uri);
		Log.d(cTag, "Created task id=" + id);
		task.id = id;
		ContentValues values = new ContentValues();
		BindingUtils.writeTask(values, task);
		int updatedRows = androidContext.getContentResolver().update(uri,
				values, null, null);
		return (updatedRows == 1);
	}

}
