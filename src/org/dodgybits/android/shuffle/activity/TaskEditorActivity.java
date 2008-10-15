package org.dodgybits.android.shuffle.activity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.State;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.AutoCompleteCursorAdapter;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

/**
 * A generic activity for editing a task in the database.  This can be used
 * either to simply view a task (Intent.VIEW_ACTION), view and edit a task
 * (Intent.EDIT_ACTION), or create a new task (Intent.INSERT_ACTION).  
 */
public class TaskEditorActivity extends AbstractEditorActivity<Task> {
    private static final String cTag = "TaskEditorActivity";

    private static final String[] cContextProjection = new String[] {
    	Shuffle.Contexts.NAME,
    	Shuffle.Contexts._ID
    };
    
    private static final String[] cProjectProjection = new String[] {
    	Shuffle.Projects.NAME,
    	Shuffle.Projects._ID
    };

	private TabHost mTabHost;

    private Cursor mProjectCursor;
    private Cursor mContextCursor;
    
    private EditText mDescriptionWidget;
    private AutoCompleteTextView mContextView;
    private AutoCompleteTextView mProjectView;
    private Date mDueDate;
    private Button mSetDueDateButton;
    private Button mClearDueDateButton;
    private CheckBox mCompletedCheckBox;
    private TextView mDueDateWidget;
    private EditText mDetailsWidget;
    private Spinner mContactSpinner;    
    
    private String[] mContactNames;
    private long[] mContactIds;
    
    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        
        // The text view for our task description, identified by its ID in the XML file.
        mDescriptionWidget = (EditText) findViewById(R.id.description);
        mContextView = (AutoCompleteTextView) findViewById(R.id.context);
        mProjectView = (AutoCompleteTextView) findViewById(R.id.project);
        mSetDueDateButton = (Button) findViewById(R.id.due_date_set_button);
        mClearDueDateButton = (Button) findViewById(R.id.due_date_clear_button);
        mDueDateWidget = (TextView) findViewById(R.id.due_date_display);
        mCompletedCheckBox = (CheckBox) findViewById(R.id.completed_checkbox);
        mDetailsWidget = (EditText) findViewById(R.id.details);
        mContactSpinner = (Spinner) findViewById(R.id.contact);
        
        // Get the task!
        mCursor = managedQuery(mUri, Shuffle.Tasks.cExpandedProjection, null, null, null);
        // Get the context and project lists for our pulldown lists
        mContextCursor = managedQuery(Shuffle.Contexts.CONTENT_URI, cContextProjection, null, null, null);
        mProjectCursor = managedQuery(Shuffle.Projects.CONTENT_URI, cProjectProjection, null, null, null);
        

        mContextView.setAdapter(new AutoCompleteCursorAdapter(this, mContextCursor, cContextProjection, Shuffle.Contexts.CONTENT_URI));
        //mContextView.setAdapter(Test.createContextAdapter(this));
        mProjectView.setAdapter(new AutoCompleteCursorAdapter(this, mProjectCursor, cProjectProjection, Shuffle.Projects.CONTENT_URI));
        
        Cursor peopleCursor = getContentResolver().query(People.CONTENT_URI, new String[] {People._ID, People.NAME}, null, null, null);
        int size = peopleCursor.getCount() + 1;
        mContactIds = new long[size];
        mContactIds[0] = 0L;
        mContactNames = new String[size];
        mContactNames[0] = "None";
        for (int i = 1; i < size; i++) {
        	peopleCursor.moveToNext();
        	mContactIds[i] = peopleCursor.getLong(0);
        	mContactNames[i] = peopleCursor.getString(1);
        }
        peopleCursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mContactNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mContactSpinner.setAdapter(adapter);
        
        mSetDueDateButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	Calendar defaults = Calendar.getInstance();
            	if (mDueDate != null) {
            		defaults.setTime(mDueDate);
            	} 
                new DatePickerDialog(TaskEditorActivity.this,
                        mDateSetListener,
                        defaults.get(Calendar.YEAR),
                        defaults.get(Calendar.MONTH),
                        defaults.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        mClearDueDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mDueDate = null;
            	drawDateWidget();
            }
        });
        
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabSpec tabSpec = mTabHost.newTabSpec("Main");
        tabSpec.setContent(R.id.main);
        tabSpec.setIndicator("Main");
        mTabHost.addTab(tabSpec);
        tabSpec = mTabHost.newTabSpec("Extras");
        tabSpec.setContent(R.id.extras);
        tabSpec.setIndicator("Extras");
        mTabHost.addTab(tabSpec);
        mTabHost.setCurrentTab(0); 
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
    	return BindingUtils.restoreTask(icicle);
    }
    
    @Override
    protected void saveItem(Bundle outState, Task item) {
    	BindingUtils.saveTask(outState, item);
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
            } else if (mState == State.STATE_INSERT) {
                setTitle(R.string.title_new_task);
            }
            // This is a little nasty: we have resumed after previously being
            // paused/stopped.  We want to re-retrieve the data to make sure
            // we are still accurately showing what is in the cursor...  but
            // we don't want to lose any UI state like the current cursor
            // position.  This trick accomplishes that.  In the future we
            // should have a better API for doing this...
            Task task = BindingUtils.readTask(mCursor);
            mDetailsWidget.setTextKeepState(task.details == null ? "" : task.details);
            
            Collection<Long> contactIds = BindingUtils.fetchContactIds(this, task.id);
            List<String> names = BindingUtils.fetchContactNames(this, contactIds);
            Log.d(cTag, "For contact ids " + contactIds + " got names " + names);
            if (names.isEmpty()) {
            	mContactSpinner.setSelection(0);
            } else {
            	Long contactId = contactIds.iterator().next();
            	for (int i = 1; i < mContactIds.length; i++) {
            		if (mContactIds[i] == contactId) {
            			mContactSpinner.setSelection(i);
            			break;
            		}
            	}
            }
            
            mDescriptionWidget.setTextKeepState(task.description);
            if (task.context != null) {
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
            mDueDate = task.dueDate;
            drawDateWidget();
            mCompletedCheckBox.setChecked(task.complete);
            // If we hadn't previously retrieved the original task, do so
            // now.  This allows the user to revert their changes.
            if (mOriginalItem == null) {
            	mOriginalItem = task;
            }
        } else {
            setTitle(getText(R.string.error_title));
            mDescriptionWidget.setText(getText(R.string.error_message));
        }
        // select the description
        mDescriptionWidget.selectAll();
    }
    
    private OnDateSetListener mDateSetListener =
        new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {
            	mDueDate = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
            	drawDateWidget();
            }
        };

    private void drawDateWidget() {
    	if (mDueDate == null) {
    		mDueDateWidget.setText(R.string.undefined);
            mClearDueDateButton.setEnabled(false);
    	} else {
    		DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
    		mDueDateWidget.setText(format.format(mDueDate));
            mClearDueDateButton.setEnabled(true);
    	}
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
            	Date dueDate = mDueDate;
            	Integer order;
            	String details = mDetailsWidget.getText().toString();
            	Context context = fetchOrCreateContext(mContextView.getText().toString());
            	Project project = fetchOrCreateProject(mProjectView.getText().toString());
            	Boolean complete = mCompletedCheckBox.isChecked();
            	
                // If we are creating a new task, set the creation date
            	if (mState == State.STATE_INSERT) {
            		created = modified;
            		order = calculateTaskOrder(project);
                } else {
                	assert mOriginalItem != null;
                	created = mOriginalItem.created;
            		order = mOriginalItem.order;
                }

            	Task task  = new Task(description, details, context, project, created, modified, dueDate, order, complete);
                ContentValues values = new ContentValues();
            	writeItem(values, task);
            	Collection<Long> contactIds = new ArrayList<Long>();
            	int selectionPos = mContactSpinner.getSelectedItemPosition();
            	if (selectionPos > 0) {
            		contactIds.add(mContactIds[selectionPos]);
            	}
            	BindingUtils.updateContactIds(this, mOriginalItem.id, contactIds);
            	
                // Commit all of our changes to persistent storage. When the update completes
                // the content provider will notify the cursor of the change, which will
                // cause the UI to be updated.
                getContentResolver().update(mUri, values, null, null);    	
            }
        }
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
			context = BindingUtils.readContext(cursor);
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
     * If not project is defined, order is meaningless, so return -1.
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

    @Override
    protected void writeItem(ContentValues values, Task task) {
    	BindingUtils.writeTask(values, task);
    }

    /**
     * Take care of deleting a task.  Simply deletes the entry.
     */
    @Override
    protected void deleteItem() {
    	super.deleteItem();
        mDescriptionWidget.setText("");
    }
    
}
