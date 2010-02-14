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

package org.dodgybits.shuffle.android.core.model.persistence;

import java.util.Calendar;
import java.util.TimeZone;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.persistence.provider.Shuffle;

import android.content.ContentUris;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;

public class InitialDataGenerator {
	private static final String cTag = "InitialDataGenerator";

	private static final int AT_HOME_INDEX = 0;
	private static final int AT_WORK_INDEX = 1;
	private static final int AT_COMPUTER_INDEX = 2;
	private static final int ERRANDS_INDEX = 3;
	private static final int COMMUNICATION_INDEX = 4;
	private static final int READ_INDEX = 5;

	private static Context[] cPresetContexts = null;

	
    public static Context getSampleContext(Resources res) {
        initPresetContexts(res);
        return cPresetContexts[ERRANDS_INDEX];
    }
    
    /**
     * Delete any existing projects, contexts and tasks and create the standard
     * contexts.
     * 
     * @param androidContext the android context, in this case the activity.
     * @param handler the android message handler
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
        ContextPersister persister = new ContextPersister(androidContext.getContentResolver());
        for (int i = 0; i < cPresetContexts.length; i++) {
            cPresetContexts[i] = insertContext(persister, cPresetContexts[i]);
        }
        if (handler != null)
            handler.sendEmptyMessage(0);
    }    
	
	/**
	 * Clean out the current data and populate the database with a set of sample
	 * data.
     * @param androidContext the context, that being the view
     * @param handler the message handler
     */
	public static void createSampleData(android.content.Context androidContext,
			Handler handler) {
		cleanSlate(androidContext, null);
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
		
		ProjectPersister projectPersister = new ProjectPersister(androidContext.getContentResolver());
		TaskPersister taskPersister = new TaskPersister(androidContext.getContentResolver());
		
		Project sellBike = createProject("Sell old Powerbook", null);
		sellBike = insertProject( projectPersister, sellBike);
		insertTask( 
		        taskPersister,
				createTask("Backup data", null, 
						AT_COMPUTER_INDEX, sellBike, 
						now, now + DateUtils.HOUR_IN_MILLIS));
		insertTask( 
                taskPersister,
				createTask("Reformat HD", "Install Leopard and updates", 
						AT_COMPUTER_INDEX, sellBike, 
						twoDays, twoDays + 2 * DateUtils.HOUR_IN_MILLIS));
		insertTask( 
                taskPersister,
				createTask("Determine good price", "Take a look on ebay for similar systems", 
						AT_COMPUTER_INDEX, sellBike, 
						oneWeek, oneWeek + 2 * DateUtils.HOUR_IN_MILLIS));
		insertTask( 
                taskPersister,
				createTask("Put up ad", AT_COMPUTER_INDEX, sellBike, twoWeeks));

		Project cleanGarage = createProject("Clean out garage", null);
		cleanGarage = insertProject(projectPersister, cleanGarage);
		insertTask( 
                taskPersister,
				createTask("Sort out contents", "Split into keepers and junk", 
						AT_HOME_INDEX, cleanGarage, 
						yesterday, yesterday));
		insertTask( 
                taskPersister,
				createTask("Advertise garage sale", "Local paper(s) and on craigslist", 
						AT_COMPUTER_INDEX, cleanGarage, 
						oneWeek, oneWeek + 2 * DateUtils.HOUR_IN_MILLIS));
		insertTask( 
                taskPersister,
				createTask("Contact local charities", "See what they want or maybe just put in charity bins", 
						COMMUNICATION_INDEX, cleanGarage, 
						now, now));
		insertTask( 
                taskPersister,
				createTask("Take rest to tip", "Hire trailer?", 
						ERRANDS_INDEX, cleanGarage, 
						now, now));

		Project skiTrip = createProject("Organise ski trip", null);
		skiTrip = insertProject(projectPersister, skiTrip);
		insertTask( 
                taskPersister,
				createTask("Send email to determine best week", null, 
						COMMUNICATION_INDEX, skiTrip, 
						now, now + 2 * DateUtils.HOUR_IN_MILLIS));
		insertTask( 
                taskPersister,
				createTask("Look up package deals", 
						AT_COMPUTER_INDEX, skiTrip, 0L));
		insertTask( 
                taskPersister,
				createTask("Book chalet", 
						AT_COMPUTER_INDEX, skiTrip, 0L)); 
		insertTask( 
                taskPersister,
				createTask("Book flights", 
						AT_COMPUTER_INDEX, skiTrip, 0L));
		insertTask( 
                taskPersister,
				createTask("Book hire car", 
						AT_COMPUTER_INDEX, skiTrip, 0L));
		insertTask( 
                taskPersister,
				createTask("Get board waxed", 
						ERRANDS_INDEX, skiTrip, 0L));

		Project discussI8n = createProject("Discuss internationalization", cPresetContexts[AT_WORK_INDEX].getLocalId());
		discussI8n = insertProject(projectPersister, discussI8n);
		insertTask( 
                taskPersister,
				createTask("Read up on options", null, 
						AT_COMPUTER_INDEX, discussI8n, 
						twoDays, twoDays + 2 * DateUtils.HOUR_IN_MILLIS));
		insertTask( 
                taskPersister,
				createTask("Kickoff meeting", null, 
						COMMUNICATION_INDEX, discussI8n, 
						oneWeek, oneWeek + 2 * DateUtils.HOUR_IN_MILLIS));
		insertTask( 
                taskPersister,
				createTask("Produce report", null, 
						AT_WORK_INDEX, discussI8n, 
						twoWeeks, twoWeeks + 2 * DateUtils.HOUR_IN_MILLIS));

		// a few stand alone tasks
		insertTask( 
                taskPersister,
				createTask("Organise music collection", 
						AT_COMPUTER_INDEX, null, 0L));
		insertTask( 
                taskPersister,
				createTask("Make copy of door keys", 
						ERRANDS_INDEX, null, yesterday));
		insertTask( 
                taskPersister,
				createTask("Read Falling Man", 
						READ_INDEX, null, 0L));
		insertTask( 
                taskPersister,
				createTask("Buy Tufte books", 
						ERRANDS_INDEX, null, oneWeek));
		if (handler != null)
			handler.sendEmptyMessage(0);
	}
	
    
    private static void initPresetContexts(Resources res) {
        if (cPresetContexts == null) {
            cPresetContexts = new Context[] {
                    createContext(res.getText(R.string.context_athome).toString(), 5, "go_home"), // 0
                    createContext(res.getText(R.string.context_atwork).toString(), 19, "system_file_manager"), // 1
                    createContext(res.getText(R.string.context_online).toString(), 1, "applications_internet"), // 2
                    createContext(res.getText(R.string.context_errands).toString(), 14, "applications_development"), // 3
                    createContext(res.getText(R.string.context_contact).toString(), 22, "system_users"), // 4
                    createContext(res.getText(R.string.context_read).toString(), 16, "format_justify_fill") // 5
            };
        }
    }

    private static Context createContext(String name, int colourIndex, String iconName) {
        Context.Builder builder = Context.newBuilder();
        builder
            .setName(name)
            .setColourIndex(colourIndex)
            .setIconName(iconName);
        return builder.build();
    }	

	private static Task createTask(String description, int contextIndex, Project project, long start) {
		return createTask(description, null, contextIndex, project, start);
	}
	
	private static Task createTask(String description, String details, 
			int contextIndex, Project project, long start) {
		return createTask(description, details, contextIndex, project, start, start);

	}		

	private static int ORDER = 1;
	
	private static Task createTask(String description, String details, 
			int contextIndex, Project project, long start, long due) {
		Id contextId = contextIndex > -1 ? cPresetContexts[contextIndex].getLocalId() : Id.NONE;
		long created = System.currentTimeMillis();
        String timezone = TimeZone.getDefault().getID();

		Task.Builder builder = Task.newBuilder();
		builder
		    .setDescription(description)
		    .setDetails(details)
		    .setContextId(contextId)
		    .setProjectId(project == null ? Id.NONE : project.getLocalId())
		    .setCreatedDate(created)
		    .setModifiedDate(created)
		    .setStartDate(start)
		    .setDueDate(due)
		    .setTimezone(timezone)
		    .setOrder(ORDER++);
		return builder.build();
	}
	
    private static Project createProject(String name, Id defaultContextId) {
        Project.Builder builder = Project.newBuilder();
        builder
            .setName(name)
            .setDefaultContextId(defaultContextId)
            .setModifiedDate(System.currentTimeMillis());
        return builder.build();
    }

	private static Context insertContext(
			ContextPersister persister,
			org.dodgybits.shuffle.android.core.model.Context context) {
		Uri uri = persister.insert(context);
		long id = ContentUris.parseId(uri);
		Log.d(cTag, "Created context id=" + id + " uri=" + uri);
		Context.Builder builder = Context.newBuilder();
		builder.mergeFrom(context);
		builder.setLocalId(Id.create(id));
		context = builder.build();
		return context;
	}

	private static Project insertProject(
			ProjectPersister persister,
			Project project) {
		Uri uri = persister.insert(project);
		long id = ContentUris.parseId(uri);
		Log.d(cTag, "Created project id=" + id + " uri=" + uri);
		Project.Builder builder = Project.newBuilder();
		builder.mergeFrom(project);
		builder.setLocalId(Id.create(id));
		project = builder.build();
		return project;
	}

	private static Task insertTask(
	        TaskPersister persister,
			Task task) {
		Uri uri = persister.insert(task);
		long id = ContentUris.parseId(uri);
		Log.d(cTag, "Created task id=" + id);
		Task.Builder builder = Task.newBuilder();
		builder.mergeFrom(task);
		builder.setLocalId(Id.create(id));
		task = builder.build();
		return task;
	}

}