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
    private CheckBox mCompletedCheckBox;
    private EditText mDetailsWidget;
    
    private boolean mSchedulingExpanded;
    private Button mStartDateButton;
    private Button mDueDateButton;
    private Button mStartTimeButton;
    private Button mDueTimeButton;
    private CheckBox mAllDayCheckBox;

    private Time mStartTime;
    private Time mDueTime;
    
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
                mCompletedCheckBox.setVisibility(View.VISIBLE);    
                mOriginalItem = BindingUtils.readTask(mCursor,getResources());
              	updateUIFromItem(mOriginalItem);
            } else {
                setTitle(getText(R.string.error_title));
                mDescriptionWidget.setText(getText(R.string.error_message));
            }
        } else if (mState == State.STATE_INSERT) {
            setTitle(R.string.title_new_task);
            mCompletedCheckBox.setVisibility(View.GONE);
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
    }
    
    @Override
    protected void updateUIFromItem(Task task) {
        mDetailsWidget.setTextKeepState(task.details == null ? "" : task.details);
        
        mDescriptionWidget.setTextKeepState(task.description);
        if ( task.context != null) {
            mContextView.setTextKeepState(task.context.name);
        }
        if (task.project != null) {
            mProjectView.setTextKeepState(task.project.name);
        }
        
        // If the event is all-day, read the times in UTC timezone
        if (task.startDate != 0) {
            if (task.allDay) {
                String tz = mStartTime.timezone;
                mStartTime.timezone = Time.TIMEZONE_UTC;
                mStartTime.set(task.startDate);
                mStartTime.timezone = tz;

                // Calling normalize to calculate isDst
                mStartTime.normalize(true);
            } else {
                mStartTime.set(task.startDate);
            }
        }

        if (task.dueDate != 0) {
            if (task.allDay) {
                String tz = mStartTime.timezone;
                mDueTime.timezone = Time.TIMEZONE_UTC;
                mDueTime.set(task.dueDate);
                mDueTime.timezone = tz;

                // Calling normalize to calculate isDst
                mDueTime.normalize(true);
            } else {
                mDueTime.set(task.dueDate);
            }
        }
        
        mAllDayCheckBox.setOnCheckedChangeListener(this);            
        mAllDayCheckBox.setChecked(task.allDay);
        
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
    	long startDate = mStartTime.toMillis(true);
    	long dueDate = mDueTime.toMillis(true);
    	
    	// TODO handle all day events
    	
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
    			startDate, dueDate, allDay, hasAlarms,
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
            

//            case R.id.delete: {
//                EditEntry entry = findEntryForView(v);
//                if (entry != null) {
//                    // Clear the text and hide the view so it gets saved properly
//                    ((TextView) entry.view.findViewById(R.id.data)).setText(null);
//                    entry.view.setVisibility(View.GONE);
//                    entry.isDeleted = true;
//                }
//                
//                // Force rebuild of views because section headers might need to change
//                buildViews();
//                break;
//            }

            default:
            	super.onClick(v);
            	break;
        }
    }
    
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (mDueTime.hour == 0 && mDueTime.minute == 0) {
                mDueTime.monthDay--;
                long dueMillis = mDueTime.normalize(true);

                // Do not allow an event to have an end time before the start time.
                if (mDueTime.before(mStartTime)) {
                    mDueTime.set(mStartTime);
                    dueMillis = mDueTime.normalize(true);
                }
                setDate(mDueDateButton, dueMillis);
                setTime(mDueTimeButton, dueMillis);
            }

            mStartTimeButton.setVisibility(View.GONE);
            mDueTimeButton.setVisibility(View.GONE);
        } else {
            if (mDueTime.hour == 0 && mDueTime.minute == 0) {
                mDueTime.monthDay++;
                long endMillis = mDueTime.normalize(true);
                setDate(mDueDateButton, endMillis);
                setTime(mDueTimeButton, endMillis);
            }

            mStartTimeButton.setVisibility(View.VISIBLE);
            mDueTimeButton.setVisibility(View.VISIBLE);
        }
    }

    private void loadCursors() {
        // Get the task if we're editing
    	if (mUri != null)
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
    	
        View schedulingEntry = findViewById(R.id.scheduling_entry);
        schedulingEntry.setOnClickListener(this);
        schedulingEntry.setOnFocusChangeListener(this);
                
        View completeEntry = findViewById(R.id.completed_entry);
        completeEntry.setOnClickListener(this);
        completeEntry.setOnFocusChangeListener(this);
        mCompletedCheckBox = (CheckBox) completeEntry.findViewById(R.id.checkbox);
    	
        mContextView.setAdapter(new AutoCompleteCursorAdapter(this, mContextCursor, 
        		cContextProjection, Shuffle.Contexts.CONTENT_URI));
        mProjectView.setAdapter(new AutoCompleteCursorAdapter(this, mProjectCursor, 
        		cProjectProjection, Shuffle.Projects.CONTENT_URI));

        mStartDateButton = (Button) findViewById(R.id.start_date);
        mStartTimeButton = (Button) findViewById(R.id.start_time);
        mDueDateButton = (Button) findViewById(R.id.due_date);
        mDueTimeButton = (Button) findViewById(R.id.due_time);
        mAllDayCheckBox = (CheckBox) findViewById(R.id.is_all_day);
        
        mStartDateButton.setOnClickListener(new DateClickListener(mStartTime));
        mDueDateButton.setOnClickListener(new DateClickListener(mDueTime));

        mStartTimeButton.setOnClickListener(new TimeClickListener(mStartTime));
        mDueTimeButton.setOnClickListener(new TimeClickListener(mDueTime));
        
        mSchedulingExpanded = true;
    }
    
    private void toggleSchedulingSection() {
        ViewGroup schedulingSection = (ViewGroup) findViewById(R.id.scheduling_section);
        View schedulingEntry = findViewById(R.id.scheduling_entry);
        View addButton = schedulingEntry.findViewById(R.id.expand);
        View removeButton = schedulingEntry.findViewById(R.id.collapse);
        View schedulingExtra = schedulingSection.findViewById(R.id.scheduling_extra); 
        if (mSchedulingExpanded)
        {
        	schedulingExtra.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
            removeButton.setVisibility(View.GONE);
        }
        else
        {
        	schedulingExtra.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.GONE);
            removeButton.setVisibility(View.VISIBLE);
        }
        mSchedulingExpanded = !mSchedulingExpanded;
    }
    
    private void setDate(TextView view, long millis) {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR |
                DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH |
                DateUtils.FORMAT_ABBREV_WEEKDAY;
        view.setText(DateUtils.formatDateTime(this, millis, flags));
    }

    private void setTime(TextView view, long millis) {
        int flags = DateUtils.FORMAT_SHOW_TIME;
        if (DateFormat.is24HourFormat(this)) {
            flags |= DateUtils.FORMAT_24HOUR;
        }
        view.setText(DateUtils.formatDateTime(this, millis, flags));
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

                // Also update the end time to keep the duration constant.
                dueTime.hour = hourOfDay + hourDuration;
                dueTime.minute = minute + minuteDuration;
                dueMillis = dueTime.normalize(true);
            } else {
                // The end time was changed.
                startMillis = startTime.toMillis(true);
                dueTime.hour = hourOfDay;
                dueTime.minute = minute;
                dueMillis = dueTime.normalize(true);

                // Do not allow an event to have an end time before the start time.
                if (dueTime.before(startTime)) {
                    dueTime.set(startTime);
                    dueMillis = startMillis;
                }
            }

            setDate(mDueDateButton, dueMillis);
            setTime(mStartTimeButton, startMillis);
            setTime(mDueTimeButton, dueMillis);
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

            // Cache the start and end millis so that we limit the number
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

                // Also update the end date to keep the duration constant.
                dueTime.year = year + yearDuration;
                dueTime.month = month + monthDuration;
                dueTime.monthDay = monthDay + monthDayDuration;
                dueMillis = dueTime.normalize(true);
            } else {
                // The end date was changed.
                startMillis = startTime.toMillis(true);
                dueTime.year = year;
                dueTime.month = month;
                dueTime.monthDay = monthDay;
                dueMillis = dueTime.normalize(true);

                // Do not allow an event to have an end time before the start time.
                if (dueTime.before(startTime)) {
                    dueTime.set(startTime);
                    dueMillis = startMillis;
                }
            }

            setDate(mStartDateButton, startMillis);
            setDate(mDueDateButton, dueMillis);
            setTime(mDueTimeButton, dueMillis); // In case end time had to be reset
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
