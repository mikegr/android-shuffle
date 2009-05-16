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

package org.dodgybits.android.shuffle.activity;

import java.util.TimeZone;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.State;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.AutoCompleteCursorAdapter;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * A generic activity for editing a task in the database.  This can be used
 * either to simply view a task (Intent.VIEW_ACTION), view and edit a task
 * (Intent.EDIT_ACTION), or create a new task (Intent.INSERT_ACTION).  
 */
public class TaskEditorActivity extends AbstractEditorActivity<Task>
	implements CompoundButton.OnCheckedChangeListener {
	
    private static final String cTag = "TaskEditorActivity";

    private static final String[] cContextProjection = new String[] {
    	Shuffle.Contexts.NAME,
    	Shuffle.Contexts._ID
    };
    
    private static final String[] cProjectProjection = new String[] {
    	Shuffle.Projects.NAME,
    	Shuffle.Projects._ID
    };

    private Cursor mProjectCursor;
    private Cursor mContextCursor;
        
    private EditText mDescriptionWidget;
    private AutoCompleteTextView mContextView;
    private AutoCompleteTextView mProjectView;
    private EditText mDetailsWidget;
    
    private boolean mSchedulingExpanded;
    private Button mStartDateButton;
    private Button mDueDateButton;
    private Button mStartTimeButton;
    private Button mDueTimeButton;
    private CheckBox mAllDayCheckBox;

    private boolean mShowStart;
    private Time mStartTime;
    private boolean mShowDue;
    private Time mDueTime;

	private View mSchedulingExtra;
	private View mExpandButton;
	private View mCollapseButton;

	private View mCompleteEntry;
    private CheckBox mCompletedCheckBox;
    
	
    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
                
        mStartTime = new Time();
        mDueTime = new Time();
        
        loadCursors();
        findViewsAndAddListeners();
        
        if (mState == State.STATE_EDIT) {
            if (mCursor != null) {
                // Make sure we are at the one and only row in the cursor.
                mCursor.moveToFirst();
                // Modify our overall title depending on the mode we are running in.
                setTitle(R.string.title_edit_task);
                mCompleteEntry.setVisibility(View.VISIBLE);    
                mOriginalItem = BindingUtils.readTask(mCursor,getResources());
              	updateUIFromItem(mOriginalItem);
            } else {
                setTitle(getText(R.string.error_title));
                mDescriptionWidget.setText(getText(R.string.error_message));
            }
        } else if (mState == State.STATE_INSERT) {
            setTitle(R.string.title_new_task);
            mCompleteEntry.setVisibility(View.GONE);
            // see if the context or project were suggested for this task
            Bundle extras = getIntent().getExtras();
            updateUIFromExtras(extras);
        }
    }
    
    @Override
    protected boolean isValid() {
        String description = mDescriptionWidget.getText().toString();
        return !TextUtils.isEmpty(description);
    }
    
    @Override
    protected void updateUIFromExtras(Bundle extras) {
    	if (extras != null) {
        	String contextName = extras.getString(Shuffle.Tasks.CONTEXT_ID);
        	if (contextName != null) mContextView.setTextKeepState(contextName);
        	String projectName = extras.getString(Shuffle.Tasks.PROJECT_ID);
        	if (projectName != null) mProjectView.setTextKeepState(projectName);
        }
    	
        setWhenDefaults();   
        populateWhen();
        
        mStartTimeButton.setVisibility(View.VISIBLE);
        mDueTimeButton.setVisibility(View.VISIBLE);
    }
    
    @Override
    protected void updateUIFromItem(Task task) {
        mDetailsWidget.setTextKeepState(task.details == null ? "" : task.details);
        
        mDescriptionWidget.setTextKeepState(task.description);
        if (task.context != null) {
            mContextView.setTextKeepState(task.context.name);
        }
        if (task.project != null) {
            mProjectView.setTextKeepState(task.project.name);
        }
                         
        Boolean allDay = task.allDay;
		if (allDay) {
            String tz = mStartTime.timezone;
            mStartTime.timezone = Time.TIMEZONE_UTC;
            mStartTime.set(task.startDate);
            mStartTime.timezone = tz;

            // Calling normalize to calculate isDst
            mStartTime.normalize(true);
        } else {
            mStartTime.set(task.startDate);
        }

        if (allDay) {
            String tz = mStartTime.timezone;
            mDueTime.timezone = Time.TIMEZONE_UTC;
            mDueTime.set(task.dueDate);
            mDueTime.timezone = tz;

            // Calling normalize to calculate isDst
            mDueTime.normalize(true);
        } else {
            mDueTime.set(task.dueDate);
        }

        setWhenDefaults();   
        populateWhen();
        
    	// show scheduling section if either start or due date are set
    	setSchedulingVisibility(mShowStart || mShowDue);
        
        mAllDayCheckBox.setChecked(allDay);
        updateTimeVisibility(!allDay);
        
        mCompletedCheckBox.setChecked(task.complete);
        // If we hadn't previously retrieved the original task, do so
        // now.  This allows the user to revert their changes.
        if (mOriginalItem == null) {
        	mOriginalItem = task;
        }
    }
        
    @Override
    protected Task createItemFromUI() {
        String description = mDescriptionWidget.getText().toString();

        // Bump the modification time to now.
    	long modified = System.currentTimeMillis();
    	long created;
    	Boolean allDay = mAllDayCheckBox.isChecked();
    	
        String timezone = null;
        long startMillis = 0L;
        long dueMillis = 0L;
        if (allDay) {
            // Reset start and end time, increment the monthDay by 1, and set
            // the timezone to UTC, as required for all-day events.
            timezone = Time.TIMEZONE_UTC;
            mStartTime.hour = 0;
            mStartTime.minute = 0;
            mStartTime.second = 0;
            mStartTime.timezone = timezone;
            startMillis = mStartTime.normalize(true);

            mDueTime.hour = 0;
            mDueTime.minute = 0;
            mDueTime.second = 0;
            mDueTime.monthDay++;
            mDueTime.timezone = timezone;
            dueMillis = mDueTime.normalize(true);
        } else {
        	if (mShowStart && !Time.isEpoch(mStartTime)) {
        		startMillis = mStartTime.toMillis(true);
        	}
        	
        	if (mShowDue && !Time.isEpoch(mDueTime)) {
        		dueMillis = mDueTime.toMillis(true);
        	}
        	
        	if (mState == State.STATE_INSERT) {
                // The timezone for a new task is the currently displayed timezone
                timezone = TimeZone.getDefault().getID();
        	}
        	else
        	{
        		timezone = mOriginalItem.timezone;
                
                // The timezone might be null if we are changing an existing
                // all-day task to a non-all-day event.  We need to assign
                // a timezone to the non-all-day task.
                if (TextUtils.isEmpty(timezone)) {
                    timezone = TimeZone.getDefault().getID();
                }
            }
        }
        
    	Integer order;
    	String details = mDetailsWidget.getText().toString();
    	Context context = fetchOrCreateContext(mContextView.getText().toString());
    	Project project = fetchOrCreateProject(mProjectView.getText().toString());
    	Boolean complete = mCompletedCheckBox.isChecked();
    	Boolean hasAlarms = false;
    	
        // If we are creating a new task, set the creation date
    	if (mState == State.STATE_INSERT) {
    		created = modified;
        } else {
        	assert mOriginalItem != null;
        	created = mOriginalItem.created;
        }
		order = calculateTaskOrder(project);

    	Task task  = new Task(description, details, 
    			context, project, created, modified, 
    			startMillis, dueMillis, timezone, allDay, hasAlarms,
    			order, complete);
    	return task;
	}

    @Override
    protected Intent getInsertIntent() {
    	Intent intent = new Intent(Intent.ACTION_INSERT, Shuffle.Tasks.CONTENT_URI);
    	// give new task the same project and context as this one
    	Bundle extras = intent.getExtras();
    	if (extras == null) extras = new Bundle();
    	CharSequence contextName = mContextView.getText();
    	if (!TextUtils.isEmpty(contextName)) {
    		extras.putString(Shuffle.Tasks.CONTEXT_ID, contextName.toString());    		
    	}
    	CharSequence projectName = mProjectView.getText();
    	if (!TextUtils.isEmpty(projectName)) {
    		extras.putString(Shuffle.Tasks.PROJECT_ID, projectName.toString());    		
    	}
    	intent.putExtras(extras);
    	return intent;
    }
    
    /**
     * @return id of layout for this view
     */
    @Override
    protected int getContentViewResId() {
    	return R.layout.task_editor;
    }

    @Override
    protected Task restoreItem(Bundle icicle) {
    	return BindingUtils.restoreTask(icicle,getResources());
    }
    
    @Override
    protected void saveItem(Bundle outState, Task item) {
    	BindingUtils.saveTask(outState, item);
    }
    
    @Override
    protected void writeItem(ContentValues values, Task task) {
    	BindingUtils.writeTask(values, task);
    }

    @Override
    protected CharSequence getItemName() {
    	return getString(R.string.task_name);
    }
    
    /**
     * Take care of deleting a task.  Simply deletes the entry.
     */
    @Override
    protected void doDeleteAction() {
    	super.doDeleteAction();
        mDescriptionWidget.setText("");
    }    
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scheduling_entry: {
            	toggleSchedulingSection();
                break;
            }
            
            case R.id.completed_entry: {
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkbox);
                checkBox.toggle();
                break;
            }
            
            default:
            	super.onClick(v);
            	break;
        }
    }
    
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (mDueTime.hour == 0 && mDueTime.minute == 0) {
                mDueTime.monthDay--;

                // Do not allow an event to have an end time before the start time.
                if (mDueTime.before(mStartTime)) {
                    mDueTime.set(mStartTime);
                }
            }
        } else {
            if (mDueTime.hour == 0 && mDueTime.minute == 0) {
                mDueTime.monthDay++;
            }
        }

    	mShowStart = true;
        long startMillis = mStartTime.normalize(true);
        setDate(mStartDateButton, startMillis, mShowStart);
        setTime(mStartTimeButton, startMillis, mShowStart);

    	mShowDue = true;
        long dueMillis = mDueTime.normalize(true);
        setDate(mDueDateButton, dueMillis, mShowDue);
        setTime(mDueTimeButton, dueMillis, mShowDue);
        
        updateTimeVisibility(!isChecked);
    }
    
    private void updateTimeVisibility(boolean showTime) {
    	if (showTime) {
            mStartTimeButton.setVisibility(View.VISIBLE);
            mDueTimeButton.setVisibility(View.VISIBLE);
    	} else {
            mStartTimeButton.setVisibility(View.GONE);
            mDueTimeButton.setVisibility(View.GONE);
    	}
    }

    private void loadCursors() {
        // Get the task if we're editing
    	if (mUri != null && mState == State.STATE_EDIT )
    	{
	        mCursor = managedQuery(mUri, Shuffle.Tasks.cExpandedProjection, null, null, null);
	        if (mCursor == null || mCursor.getCount() == 0) {
	            // The cursor is empty. This can happen if the event was deleted.
	            finish();
	            return;
	        }
    	}
    	
        // Get the context and project lists for our pulldown lists
        mContextCursor = managedQuery(Shuffle.Contexts.CONTENT_URI, 
        		cContextProjection, null, null, null);
        mProjectCursor = managedQuery(Shuffle.Projects.CONTENT_URI, 
        		cProjectProjection, null, null, null);
    }
    
    private void findViewsAndAddListeners() {
        // The text view for our task description, identified by its ID in the XML file.
        mDescriptionWidget = (EditText) findViewById(R.id.description);
        mContextView = (AutoCompleteTextView) findViewById(R.id.context);
        mProjectView = (AutoCompleteTextView) findViewById(R.id.project);

        mDetailsWidget = (EditText) findViewById(R.id.details);
                
        mCompleteEntry = findViewById(R.id.completed_entry);
        mCompleteEntry.setOnClickListener(this);
        mCompleteEntry.setOnFocusChangeListener(this);
        mCompletedCheckBox = (CheckBox) mCompleteEntry.findViewById(R.id.checkbox);
    	
        mContextView.setAdapter(new AutoCompleteCursorAdapter(this, mContextCursor, 
        		cContextProjection, Shuffle.Contexts.CONTENT_URI));
        mProjectView.setAdapter(new AutoCompleteCursorAdapter(this, mProjectCursor, 
        		cProjectProjection, Shuffle.Projects.CONTENT_URI));

        mStartDateButton = (Button) findViewById(R.id.start_date);
        mStartDateButton.setOnClickListener(new DateClickListener(mStartTime));
        
        mStartTimeButton = (Button) findViewById(R.id.start_time);
        mStartTimeButton.setOnClickListener(new TimeClickListener(mStartTime));
        
        mDueDateButton = (Button) findViewById(R.id.due_date);
        mDueDateButton.setOnClickListener(new DateClickListener(mDueTime));
        
        mDueTimeButton = (Button) findViewById(R.id.due_time);
        mDueTimeButton.setOnClickListener(new TimeClickListener(mDueTime));

        mAllDayCheckBox = (CheckBox) findViewById(R.id.is_all_day);
        mAllDayCheckBox.setOnCheckedChangeListener(this);            

        ViewGroup schedulingSection = (ViewGroup) findViewById(R.id.scheduling_section);
        View schedulingEntry = findViewById(R.id.scheduling_entry);
        schedulingEntry.setOnClickListener(this);
        schedulingEntry.setOnFocusChangeListener(this);

        mSchedulingExtra = schedulingSection.findViewById(R.id.scheduling_extra); 
        mExpandButton = schedulingEntry.findViewById(R.id.expand);
        mCollapseButton = schedulingEntry.findViewById(R.id.collapse);
        mSchedulingExpanded = mSchedulingExtra.getVisibility() == View.VISIBLE;
    }
    
    private void toggleSchedulingSection() {
        mSchedulingExpanded = !mSchedulingExpanded;
        setSchedulingVisibility(mSchedulingExpanded);
    }

    private void setSchedulingVisibility(boolean visible) {
        if (visible) {
        	mSchedulingExtra.setVisibility(View.VISIBLE);
            mExpandButton.setVisibility(View.GONE);
            mCollapseButton.setVisibility(View.VISIBLE);
        } else {
        	mSchedulingExtra.setVisibility(View.GONE);
            mExpandButton.setVisibility(View.VISIBLE);
            mCollapseButton.setVisibility(View.GONE);
        }
    }
    
    private void setWhenDefaults() {
    	// it's possible to have:
    	// 1) no times set
    	// 2) due time set, but not start time
    	// 3) start and due time set
    	
    	mShowStart = !Time.isEpoch(mStartTime);
    	mShowDue = !Time.isEpoch(mDueTime);
    	
    	if (!mShowStart && !mShowDue) {
            mStartTime.setToNow();

            // Round the time to the nearest half hour.
            mStartTime.second = 0;
            int minute = mStartTime.minute;
            if (minute > 0 && minute <= 30) {
                mStartTime.minute = 30;
            } else {
                mStartTime.minute = 0;
                mStartTime.hour += 1;
            }

            long startMillis = mStartTime.normalize(true /* ignore isDst */);
            mDueTime.set(startMillis + DateUtils.HOUR_IN_MILLIS);
        } else if (!mShowStart) {
        	// default start to same as due
        	mStartTime.set(mDueTime);
        }
    }
    
    private void populateWhen() {
        long startMillis = mStartTime.toMillis(false /* use isDst */);
        long endMillis = mDueTime.toMillis(false /* use isDst */);
        setDate(mStartDateButton, startMillis, mShowStart);
        setDate(mDueDateButton, endMillis, mShowDue);

        setTime(mStartTimeButton, startMillis, mShowStart);
        setTime(mDueTimeButton, endMillis, mShowDue);
    }
    
    private void setDate(TextView view, long millis, boolean showValue) {
    	CharSequence value;
    	if (showValue) {
	        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR |
	                DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH |
	                DateUtils.FORMAT_ABBREV_WEEKDAY;
	        value = DateUtils.formatDateTime(this, millis, flags);
    	} else {
    		value = "";
    	}
        view.setText(value);
    }

    private void setTime(TextView view, long millis, boolean showValue) {
    	CharSequence value;
    	if (showValue) {
	        int flags = DateUtils.FORMAT_SHOW_TIME;
	        if (DateFormat.is24HourFormat(this)) {
	            flags |= DateUtils.FORMAT_24HOUR;
	        }
	        value = DateUtils.formatDateTime(this, millis, flags);
    	} else {
    		value = "";
    	}
        view.setText(value);
    }
    
    private Context fetchOrCreateContext(String contextName) {
    	Context context = null;
    	if (!TextUtils.isEmpty(contextName)) {
    		// first check if context already exists
    		Cursor cursor =  getContentResolver().query(
    				Shuffle.Contexts.CONTENT_URI, 
    				Shuffle.Contexts.cFullProjection, 
    				"name = ?", new String[] {contextName}, null);
    		if (!cursor.moveToFirst()) {
    			cursor.close();
    			// context didn't exist - create a new one
    			ContentValues values = new ContentValues(1);
    			values.put(Shuffle.Contexts.NAME, contextName);
    			Uri uri = getContentResolver().insert(Shuffle.Contexts.CONTENT_URI, values);
    			cursor = getContentResolver().query(uri, Shuffle.Contexts.cFullProjection, null, null, null);
        		cursor.moveToFirst();
    		}
			context = BindingUtils.readContext(cursor,getResources());
			cursor.close();
    	}
    	return context;
    }
    
    private Project fetchOrCreateProject(String projectName) {
    	Project project = null;
    	if (!TextUtils.isEmpty(projectName)) {
    		// first check if context already exists
    		Cursor cursor =  getContentResolver().query(
    				Shuffle.Projects.CONTENT_URI, 
    				Shuffle.Projects.cFullProjection, 
    				"name = ?", new String[] {projectName}, null);
    		if (!cursor.moveToFirst()) {
    			cursor.close();
    			// context didn't exist - create a new one
    			ContentValues values = new ContentValues(1);
    			values.put(Shuffle.Projects.NAME, projectName);
    			Uri uri = getContentResolver().insert(Shuffle.Projects.CONTENT_URI, values);
    			cursor = getContentResolver().query(uri, Shuffle.Projects.cFullProjection, null, null, null);
        		cursor.moveToFirst();
    		}
			project = BindingUtils.readProject(cursor);
			cursor.close();
    	}
    	return project;    	
    }

    /**
     * Calculate where this task should appear on the list for the given project.
     * If no project is defined, order is meaningless, so return -1.
     * New tasks go on the end of the list, so the highest current order
     * value for tasks for this project and add one to this.
     * For existing tasks, check if the project changed, and if so
     * treat like a new task, otherwise leave the order as is.
     * 
     * @param newProject the project selected for this task
     * @return 0-indexed order of task when displayed in the project view
     */
    private Integer calculateTaskOrder(Project newProject) {
    	if (newProject == null) return -1;
    	int order;
    	if (mState == State.STATE_INSERT || !newProject.equals(mOriginalItem.project)) {
    		// get current highest order value    		
    		Cursor cursor =  getContentResolver().query(
    				Shuffle.Tasks.CONTENT_URI, 
    				new String[] {Shuffle.Tasks.PROJECT_ID, Shuffle.Tasks.DISPLAY_ORDER}, 
    				Shuffle.Tasks.PROJECT_ID + " = ?", 
    				new String[] {newProject.id.toString()}, 
    				Shuffle.Tasks.DISPLAY_ORDER + " desc");
    		if (cursor.moveToFirst()) {
    			// first entry is current highest value
    			int highest = cursor.getInt(1);
    			order =  highest + 1;
    		} else {
    			// no tasks in the project yet.
    			order = 0;
    		}
    		cursor.close();
    	} else {
    		order = mOriginalItem.order; 
    	}
    	return order;
    }
    
    /* This class is used to update the time buttons. */
    private class TimeListener implements OnTimeSetListener {
        private View mView;

        public TimeListener(View view) {
            mView = view;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Cache the member variables locally to avoid inner class overhead.
            Time startTime = mStartTime;
            Time dueTime = mDueTime;

            // Cache the start and due millis so that we limit the number
            // of calls to normalize() and toMillis(), which are fairly
            // expensive.
            long startMillis;
            long dueMillis;
            if (mView == mStartTimeButton) {
                // The start time was changed.
                int hourDuration = dueTime.hour - startTime.hour;
                int minuteDuration = dueTime.minute - startTime.minute;

                startTime.hour = hourOfDay;
                startTime.minute = minute;
                startMillis = startTime.normalize(true);
                mShowStart = true;
                
                // Also update the due time to keep the duration constant.
                dueTime.hour = hourOfDay + hourDuration;
                dueTime.minute = minute + minuteDuration;
                dueMillis = dueTime.normalize(true);
                mShowDue = true;
            } else {
                // The due time was changed.
                startMillis = startTime.toMillis(true);
                dueTime.hour = hourOfDay;
                dueTime.minute = minute;
                dueMillis = dueTime.normalize(true);
                mShowDue = true;

                if (mShowStart) {
	                // Do not allow an event to have a due time before the start time.
	                if (dueTime.before(startTime)) {
	                    dueTime.set(startTime);
	                    dueMillis = startMillis;
	                }
                } else {
                	// if start time is not shown, default it to be the same as due time
                	startTime.set(dueTime);
                }
            }

            // update all 4 buttons in case visibility has changed
            setDate(mStartDateButton, startMillis, mShowStart);
            setTime(mStartTimeButton, startMillis, mShowStart);
            setDate(mDueDateButton, dueMillis, mShowDue);
            setTime(mDueTimeButton, dueMillis, mShowDue);
        }
        
    }

    private class TimeClickListener implements View.OnClickListener {
        private Time mTime;

        public TimeClickListener(Time time) {
            mTime = time;
        }

        public void onClick(View v) {
            new TimePickerDialog(TaskEditorActivity.this, new TimeListener(v),
                    mTime.hour, mTime.minute,
                    DateFormat.is24HourFormat(TaskEditorActivity.this)).show();
        }
    }
    
    private class DateListener implements OnDateSetListener {
        View mView;

        public DateListener(View view) {
            mView = view;
        }
        
        public void onDateSet(DatePicker view, int year, int month, int monthDay) {
            // Cache the member variables locally to avoid inner class overhead.
            Time startTime = mStartTime;
            Time dueTime = mDueTime;

            // Cache the start and due millis so that we limit the number
            // of calls to normalize() and toMillis(), which are fairly
            // expensive.
            long startMillis;
            long dueMillis;
            if (mView == mStartDateButton) {
                // The start date was changed.
                int yearDuration = dueTime.year - startTime.year;
                int monthDuration = dueTime.month - startTime.month;
                int monthDayDuration = dueTime.monthDay - startTime.monthDay;

                startTime.year = year;
                startTime.month = month;
                startTime.monthDay = monthDay;
                startMillis = startTime.normalize(true);
                mShowStart = true;
                
                // Also update the end date to keep the duration constant.
                dueTime.year = year + yearDuration;
                dueTime.month = month + monthDuration;
                dueTime.monthDay = monthDay + monthDayDuration;
                dueMillis = dueTime.normalize(true);
                mShowDue = true;
            } else {
                // The end date was changed.
                startMillis = startTime.toMillis(true);
                dueTime.year = year;
                dueTime.month = month;
                dueTime.monthDay = monthDay;
                dueMillis = dueTime.normalize(true);
                mShowDue = true;
                
                if (mShowStart) {
	                // Do not allow an event to have an end time before the start time.
	                if (dueTime.before(startTime)) {
	                    dueTime.set(startTime);
	                    dueMillis = startMillis;
	                }
                } else {
                	// if start time is not shown, default it to be the same as due time
                	startTime.set(dueTime);
                }
            }

            // update all 4 buttons in case visibility has changed
            setDate(mStartDateButton, startMillis, mShowStart);
            setTime(mStartTimeButton, startMillis, mShowStart);
            setDate(mDueDateButton, dueMillis, mShowDue);
            setTime(mDueTimeButton, dueMillis, mShowDue);
        }
        
    }
    
    private class DateClickListener implements View.OnClickListener {
        private Time mTime;

        public DateClickListener(Time time) {
            mTime = time;
        }

        public void onClick(View v) {
            new DatePickerDialog(TaskEditorActivity.this, new DateListener(v), mTime.year,
                    mTime.month, mTime.monthDay).show();
        }
    }
        
}
