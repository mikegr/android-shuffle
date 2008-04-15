package org.dodgybits.android.shuffle.activity;

import static org.dodgybits.android.shuffle.model.Preferences.DISPLAY_CONTEXT_NAME_KEY;
import static org.dodgybits.android.shuffle.model.Preferences.DISPLAY_DUE_DATE_KEY;
import static org.dodgybits.android.shuffle.model.Preferences.DISPLAY_CONTEXT_ICON_KEY;
import static org.dodgybits.android.shuffle.model.Preferences.DISPLAY_PROJECT_KEY;
import static org.dodgybits.android.shuffle.model.Preferences.DISPLAY_DETAILS_KEY;

import java.util.Date;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.Preferences;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.util.ModelUtils;
import org.dodgybits.android.shuffle.view.TaskView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TableRow.LayoutParams;

public class PreferencesAppearanceActivity extends Activity  {
    private static final String cTag = "PreferencesAppearanceActivity";
	
    private TaskView mTaskView;
    private Task mSampleTask;
	private CheckBox mDisplayIconCheckbox;
	private CheckBox mDisplayContextCheckbox;
	private CheckBox mDisplayDueDateCheckbox;
	private CheckBox mDisplayProjectCheckbox;
	private CheckBox mDisplayDetailsCheckbox;
	private boolean mSaveChanges;	
	private boolean mDisplayIcon, mDisplayContext, mDisplayDueDate, mDisplayProject, mDisplayDetails;
	
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

        setContentView(R.layout.preferences_appearance);
        
        
        mDisplayIconCheckbox = (CheckBox) findViewById(R.id.display_icon);
        mDisplayContextCheckbox = (CheckBox) findViewById(R.id.display_context);
        mDisplayDueDateCheckbox = (CheckBox) findViewById(R.id.display_due_date);
        mDisplayProjectCheckbox = (CheckBox) findViewById(R.id.display_project);
        mDisplayDetailsCheckbox = (CheckBox) findViewById(R.id.display_details);
        
        setupSampleTask();
        
        // need to add task view programatically due to issues adding via XML
        
        mTaskView = new TaskView(this);
        mTaskView.updateView(mSampleTask, false);
        LayoutParams taskLayout = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT );
        taskLayout.span = 2;
        TableRow taskRow = new TableRow(this);
        taskRow.addView(mTaskView, taskLayout);
        TableLayout table = (TableLayout)findViewById(R.id.appearance_table);
        TableLayout.LayoutParams rowLayout = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
        table.addView(taskRow, 0, rowLayout);
        
        // currently no cancel button
        mSaveChanges = true;
        
        OnCheckedChangeListener listener = new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
	        	savePrefs();
		        mTaskView.updateView(mSampleTask, false);
			}
        };
        mDisplayIconCheckbox.setOnCheckedChangeListener(listener);
        mDisplayContextCheckbox.setOnCheckedChangeListener(listener);
        mDisplayDueDateCheckbox.setOnCheckedChangeListener(listener);
        mDisplayProjectCheckbox.setOnCheckedChangeListener(listener);
        mDisplayDetailsCheckbox.setOnCheckedChangeListener(listener);

	}
	
	private void setupSampleTask() {
        Date now = new Date();
        Project sampleProject = new Project("Sample project", 0, false);
        Context sampleContext = ModelUtils.cPresetContexts[3];
        mSampleTask = new Task("Sample action", "Additional action details", sampleContext, sampleProject, now, now, now, 1, false);
		
	}
	
    @Override
	protected void onResume() {
        Log.d(cTag, "onResume+");
		super.onResume();
		
        readPrefs();
	}

	@Override
    protected void onPause() {
        Log.d(cTag, "onPause+");
        super.onPause();

        if (!mSaveChanges) {
        	revertPrefs();
        }
    }
    
	private void readPrefs() {
		Log.d(cTag, "Settings prefs controls");
		mDisplayIcon = Preferences.displayContextIcon(this);
		mDisplayContext = Preferences.displayContextName(this);
		mDisplayDueDate = Preferences.displayDueDate(this);
		mDisplayProject = Preferences.displayProject(this);
		mDisplayDetails = Preferences.displayDetails(this);
		
		mDisplayIconCheckbox.setChecked(mDisplayIcon);
		mDisplayContextCheckbox.setChecked(mDisplayContext);
		mDisplayDueDateCheckbox.setChecked(mDisplayDueDate);
		mDisplayProjectCheckbox.setChecked(mDisplayProject);
		mDisplayDetailsCheckbox.setChecked(mDisplayDetails);
	}
	
	private void revertPrefs() {
		Log.d(cTag, "Reverting prefs");
		SharedPreferences.Editor ed = Preferences.getEditor(this);
		ed.putBoolean(DISPLAY_CONTEXT_ICON_KEY, mDisplayIcon);
		ed.putBoolean(DISPLAY_CONTEXT_NAME_KEY, mDisplayContext);
		ed.putBoolean(DISPLAY_DUE_DATE_KEY, mDisplayDueDate);
		ed.putBoolean(DISPLAY_PROJECT_KEY, mDisplayProject);
		ed.putBoolean(DISPLAY_DETAILS_KEY, mDisplayDetails);
		ed.commit();
	}
	
	private void savePrefs() {
		Log.d(cTag, "Saving prefs");
		SharedPreferences.Editor ed = Preferences.getEditor(this);
		ed.putBoolean(DISPLAY_CONTEXT_ICON_KEY, mDisplayIconCheckbox.isChecked());
		ed.putBoolean(DISPLAY_CONTEXT_NAME_KEY, mDisplayContextCheckbox.isChecked());
		ed.putBoolean(DISPLAY_DUE_DATE_KEY, mDisplayDueDateCheckbox.isChecked());
		ed.putBoolean(DISPLAY_PROJECT_KEY, mDisplayProjectCheckbox.isChecked());
		ed.putBoolean(DISPLAY_DETAILS_KEY, mDisplayDetailsCheckbox.isChecked());
		ed.commit();
	}
	
}
