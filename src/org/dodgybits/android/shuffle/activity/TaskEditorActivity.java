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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.State;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.AutoCompleteCursorAdapter;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.util.DateUtils;

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

    private Calendar mStartCal;
    private Calendar mDueCal;
    
    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
                
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
        
        
        // Get the task!
        mCursor = managedQuery(mUri, Shuffle.Tasks.cExpandedProjection, null, null, null);
        // Get the context and project lists for our pulldown lists
        mContextCursor = managedQuery(Shuffle.Contexts.CONTENT_URI, 
        		cContextProjection, null, null, null);
        mProjectCursor = managedQuery(Shuffle.Projects.CONTENT_URI, 
        		cProjectProjection, null, null, null);
        

        mContextView.setAdapter(new AutoCompleteCursorAdapter(this, mContextCursor, 
        		cContextProjection, Shuffle.Contexts.CONTENT_URI));
        mProjectView.setAdapter(new AutoCompleteCursorAdapter(this, mProjectCursor, 
        		cProjectProjection, Shuffle.Projects.CONTENT_URI));

        mStartDateButton = (Button) findViewById(R.id.start_date);
        mStartTimeButton = (Button) findViewById(R.id.start_time);
        mDueDateButton = (Button) findViewById(R.id.due_date);
        mDueTimeButton = (Button) findViewById(R.id.due_time);
        mAllDayCheckBox = (CheckBox) findViewById(R.id.is_all_day);
    }
    

    @Override
    protected void onResume() {
        Log.d(cTag, "onResume+");
        super.onResume();
        // see if the context or project was suggested for this task
        Bundle extras = getIntent().getExtras();
        // If we didn't have any trouble retrieving the data, it is now
        // time to get at the stuff.
        if (mCursor != null) {
            // Make sure we are at the one and only row in the cursor.
            mCursor.moveToFirst();
            // Modify our overall title depending on the mode we are running in.
            if (mState == State.STATE_EDIT) {
                setTitle(R.string.title_edit_task);
                mCompletedCheckBox.setVisibility(View.VISIBLE);                
            } else if (mState == State.STATE_INSERT) {
                setTitle(R.string.title_new_task);
                mCompletedCheckBox.setVisibility(View.GONE);
            }
            
            // This is a little nasty: we have resumed after previously being
            // paused/stopped.  We want to re-retrieve the data to make sure
            // we are still accurately showing what is in the cursor...  but
            // we don't want to lose any UI state like the current cursor
            // position.  This trick accomplishes that.  In the future we
            // should have a better API for doing this...
            Task task = BindingUtils.readTask(mCursor,getResources());
            mDetailsWidget.setTextKeepState(task.details == null ? "" : task.details);
            
            mDescriptionWidget.setTextKeepState(task.description);
            if ( task.context != null) {
                mContextView.setTextKeepState(task.context.name);
            } else if (extras != null) {
            	String contextName = extras.getString(Shuffle.Tasks.CONTEXT_ID);
            	if (contextName != null) mContextView.setTextKeepState(contextName);
            }
            if (task.project != null) {
                mProjectView.setTextKeepState(task.project.name);
            } else if (extras != null) {
            	String projectName = extras.getString(Shuffle.Tasks.PROJECT_ID);
            	if (projectName != null) mProjectView.setTextKeepState(projectName);
            }
            
            mStartCal = Calendar.getInstance(); 
            mDueCal = Calendar.getInstance();
            if (DateUtils.isEpoc(task.startDate))
            {
            	mStartCal.setTimeInMillis(0); // default to no start date
            }
            else
            {
                mStartCal.setTime(task.startDate);
            }
            if (DateUtils.isEpoc(task.dueDate))
            {
            	mDueCal.setTimeInMillis(0); // default to no due date
            }
            else
            {
            	mDueCal.setTime(task.dueDate);
            }

            setDate(mStartDateButton, mStartCal);
            setDate(mDueDateButton, mDueCal);

            setTime(mStartTimeButton, mStartCal);
            setTime(mDueTimeButton, mDueCal);

            mAllDayCheckBox.setChecked(task.allDay);
            
            mCompletedCheckBox.setChecked(task.complete);
            // If we hadn't previously retrieved the original task, do so
            // now.  This allows the user to revert their changes.
            if (mOriginalItem == null) {
            	mOriginalItem = task;
            }
            
            mStartDateButton.setOnClickListener(new DateClickListener(mStartCal));
            mDueDateButton.setOnClickListener(new DateClickListener(mDueCal));

            mStartTimeButton.setOnClickListener(new TimeClickListener(mStartCal));
            mDueTimeButton.setOnClickListener(new TimeClickListener(mDueCal));
            
            mAllDayCheckBox.setOnCheckedChangeListener(this);            
        } else {
            setTitle(getText(R.string.error_title));
            mDescriptionWidget.setText(getText(R.string.error_message));
        }
        // select the description
//        mDescriptionWidget.selectAll();
    }
    
    @Override
    protected void onPause() {
        Log.d(cTag, "onPause+");
        super.onPause();

        // The user is going somewhere else, so make sure their current
        // changes are safely saved away in the provider.  We don't need
        // to do this if only viewing.
        if (mCursor != null) {
            String description = mDescriptionWidget.getText().toString();
            int length = description.length();

            // If this activity is finished, and there is no text, then we
            // do something a little special: simply delete the task entry.
            // Note that we do this both for editing and inserting...  it
            // would be reasonable to only do it when inserting.
            if (isFinishing() && (length == 0)) {
                setResult(RESULT_CANCELED);
                deleteItem();

            // Get out updates into the provider.
            } else {
                // Bump the modification time to now.
            	Date modified = new Date();
            	Date created;
            	Date startDate = mStartCal.getTime();
            	Date dueDate = mDueCal.getTime();
            	Integer order;
            	String details = mDetailsWidget.getText().toString();
            	Context context = fetchOrCreateContext(mContextView.getText().toString());
            	Project project = fetchOrCreateProject(mProjectView.getText().toString());
            	Boolean complete = mCompletedCheckBox.isChecked();
            	Boolean allDay = mAllDayCheckBox.isChecked();
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
                ContentValues values = new ContentValues();
            	writeItem(values, task);
            	
                // Commit all of our changes to persistent storage. When the update completes
                // the content provider will notify the cursor of the change, which will
                // cause the UI to be updated.
                getContentResolver().update(mUri, values, null, null);    	
                showSaveToast();
            }
        }
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
    protected void deleteItem() {
    	super.deleteItem();
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
            if (mDueCal.get(Calendar.HOUR) == 0 && mDueCal.get(Calendar.MINUTE) == 0) {
            	mDueCal.add(Calendar.DAY_OF_MONTH, -1);
                long dueMillis = mDueCal.getTimeInMillis();
                long startMillis = mStartCal.getTimeInMillis();
                
                // Do not allow an event to have an due time before the start time.
                if (dueMillis < startMillis) {
                	mDueCal.setTimeInMillis(startMillis);
                	dueMillis = mDueCal.getTimeInMillis();
                }
                setDate(mDueDateButton, mDueCal);
                setTime(mDueTimeButton, mDueCal);
            }

            mStartTimeButton.setVisibility(View.GONE);
            mDueTimeButton.setVisibility(View.GONE);
        } else {
            if (mDueCal.get(Calendar.HOUR) == 0 && mDueCal.get(Calendar.MINUTE) == 0) {
            	mDueCal.add(Calendar.DAY_OF_MONTH, 1);
                setDate(mDueDateButton, mDueCal);
                setTime(mDueTimeButton, mDueCal);
            }

            mStartTimeButton.setVisibility(View.VISIBLE);
            mDueTimeButton.setVisibility(View.VISIBLE);
        }
    }
    
    private void toggleSchedulingSection() {
        ViewGroup schedulingSection = (ViewGroup) findViewById(R.id.scheduling_section);
        View schedulingEntry = findViewById(R.id.scheduling_entry);
        View addButton = schedulingEntry.findViewById(R.id.expand);
        View removeButton = schedulingEntry.findViewById(R.id.collapse);
        View schedulingExtra = schedulingSection.findViewById(R.id.scheduling_extra); 
        if (schedulingExtra.getVisibility() != View.VISIBLE)
        {
        	schedulingExtra.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.GONE);
            removeButton.setVisibility(View.VISIBLE);
        }
        else
        {
        	schedulingExtra.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
            removeButton.setVisibility(View.GONE);
        }
    }
    
    
    private void setDate(TextView view, Calendar cal) {
    	SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        view.setText(formatter.format(cal.getTime()));
    }

    private void setTime(TextView view, Calendar cal) {
    	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        view.setText(formatter.format(cal.getTime()));
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
            Calendar startCal = mStartCal;
            Calendar dueCal = mDueCal;

            long startMillis;
            long dueMillis;
            if (mView == mStartTimeButton) {
                // The start time was changed.
                int hourDuration = dueCal.get(Calendar.HOUR) - startCal.get(Calendar.HOUR);
                int minuteDuration = dueCal.get(Calendar.MINUTE) - startCal.get(Calendar.MINUTE);

                startCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startCal.set(Calendar.MINUTE, minute);
                startMillis = startCal.getTimeInMillis();

                // Also update the due time to keep the duration constant.
                dueCal.set(Calendar.HOUR_OF_DAY, hourOfDay + hourDuration);
                dueCal.set(Calendar.MINUTE, minute + minuteDuration);
                dueMillis = dueCal.getTimeInMillis();
            } else {
                // The due time was changed.
                startMillis = startCal.getTimeInMillis();
                dueCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dueCal.set(Calendar.MINUTE, minute);
                dueMillis = dueCal.getTimeInMillis();

                // Do not allow an event to have a due time before the start time.
                if (dueMillis < startMillis) {
                	dueCal.setTimeInMillis(startMillis);
                	dueMillis = startMillis;
                }
            }

            setDate(mDueDateButton, dueCal);
            setTime(mStartTimeButton, startCal);
            setTime(mDueTimeButton, dueCal); // In case end time had to be reset
            
        }
    }

    private class TimeClickListener implements View.OnClickListener {
        private Calendar mCal;

        public TimeClickListener(Calendar cal) {
            mCal = cal;
        }

        public void onClick(View v) {
            new TimePickerDialog(TaskEditorActivity.this, new TimeListener(v),
            		mCal.get(Calendar.HOUR), mCal.get(Calendar.MINUTE), true ).show();
        }
    }

    private class DateListener implements OnDateSetListener {
        View mView;

        public DateListener(View view) {
            mView = view;
        }
        
        // TODO - change following to use Calendar instead of Time (which isn't in Android 1.1)

        public void onDateSet(DatePicker view, int year, int month, int monthDay) {
            // Cache the member variables locally to avoid inner class overhead.
            Calendar startCal = mStartCal;
            Calendar dueCal = mDueCal;

            long startMillis;
            long dueMillis;
            if (mView == mStartDateButton) {
                // The start date was changed.
                int yearDuration = dueCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);
                int monthDuration = dueCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH);
                int monthDayDuration = dueCal.get(Calendar.DAY_OF_MONTH) - startCal.get(Calendar.DAY_OF_MONTH);

                startCal.set(Calendar.YEAR, year);
                startCal.set(Calendar.MONTH, month);
                startCal.set(Calendar.DAY_OF_MONTH, monthDay);
                startMillis = startCal.getTimeInMillis();

                // Also update the end date to keep the duration constant.
                dueCal.set(Calendar.YEAR, year + yearDuration);
                dueCal.set(Calendar.MONTH, month + monthDuration);
                dueCal.set(Calendar.DAY_OF_MONTH, monthDay + monthDayDuration);
                dueMillis = dueCal.getTimeInMillis();
            } else {
                // The due date was changed.
                startMillis = startCal.getTimeInMillis();
                dueCal.set(Calendar.YEAR, year);
                dueCal.set(Calendar.MONTH, month);
                dueCal.set(Calendar.DAY_OF_MONTH, monthDay);
                dueMillis = dueCal.getTimeInMillis();

                // Do not allow an event to have an end time before the start time.
                if (dueMillis < startMillis) {
                	dueCal.setTimeInMillis(startMillis);
                    dueMillis = startMillis;
                }
            }

            setDate(mStartDateButton, startCal);
            setDate(mDueDateButton, dueCal);
            setTime(mDueTimeButton, dueCal); // In case end time had to be reset
        }
    }

    private class DateClickListener implements View.OnClickListener {
        private Calendar mCal;

        public DateClickListener(Calendar cal) {
            mCal = cal;
        }

        public void onClick(View v) {
            new DatePickerDialog(TaskEditorActivity.this, new DateListener(v), mCal.get(Calendar.YEAR),
            		mCal.get(Calendar.MONTH), mCal.get(Calendar.DAY_OF_MONTH)).show();
        }
    }    
        
}
