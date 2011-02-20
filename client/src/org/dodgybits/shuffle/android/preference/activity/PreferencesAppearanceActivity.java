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

package org.dodgybits.shuffle.android.preference.activity;

import static org.dodgybits.shuffle.android.preference.model.Preferences.DISPLAY_CONTEXT_ICON_KEY;
import static org.dodgybits.shuffle.android.preference.model.Preferences.DISPLAY_CONTEXT_NAME_KEY;
import static org.dodgybits.shuffle.android.preference.model.Preferences.DISPLAY_DETAILS_KEY;
import static org.dodgybits.shuffle.android.preference.model.Preferences.DISPLAY_DUE_DATE_KEY;
import static org.dodgybits.shuffle.android.preference.model.Preferences.DISPLAY_PROJECT_KEY;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.FlurryEnabledActivity;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.EntityCache;
import org.dodgybits.shuffle.android.core.model.persistence.InitialDataGenerator;
import org.dodgybits.shuffle.android.list.view.TaskView;
import org.dodgybits.shuffle.android.preference.model.Preferences;

import com.google.inject.Inject;

import roboguice.inject.InjectView;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TableRow.LayoutParams;
import roboguice.util.Ln;

public class PreferencesAppearanceActivity extends FlurryEnabledActivity  {
    private TaskView mTaskView;
    private Task mSampleTask;
    private Project mSampleProject;
    private Context mSampleContext;
    private boolean mSaveChanges;   
    private boolean mDisplayIcon, mDisplayContext, mDisplayDueDate, mDisplayProject, mDisplayDetails;
    
	@InjectView(R.id.display_icon) CheckBox mDisplayIconCheckbox;
	@InjectView(R.id.display_context) CheckBox mDisplayContextCheckbox;
	@InjectView(R.id.display_due_date) CheckBox mDisplayDueDateCheckbox;
	@InjectView(R.id.display_project) CheckBox mDisplayProjectCheckbox;
	@InjectView(R.id.display_details) CheckBox mDisplayDetailsCheckbox;

	@Inject InitialDataGenerator mGenerator;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

        setContentView(R.layout.preferences_appearance);
        
        
        // need to add task view programatically due to issues adding via XML
        setupSampleEntities();
        
        EntityCache<Context> contentCache = new EntityCache<Context>() {
        	@Override
        	public Context findById(Id localId) {
        		return mSampleContext;
        	}
		};
		
        EntityCache<Project> projectCache = new EntityCache<Project>() {
        	@Override
        	public Project findById(Id localId) {
        		return mSampleProject;
        	}
		};
        
        
        mTaskView = new TaskView(this, contentCache, projectCache);
        mTaskView.updateView(mSampleTask); // todo pass in project and context
        LayoutParams taskLayout = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT );
        LinearLayout layout = (LinearLayout)findViewById(R.id.appearance_layout);
        layout.addView(mTaskView, 0, taskLayout);
        
        // currently no cancel button
        mSaveChanges = true;
        
        OnCheckedChangeListener listener = new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
	        	savePrefs();
		        mTaskView.updateView(mSampleTask);
			}
        };
        mDisplayIconCheckbox.setOnCheckedChangeListener(listener);
        mDisplayContextCheckbox.setOnCheckedChangeListener(listener);
        mDisplayDueDateCheckbox.setOnCheckedChangeListener(listener);
        mDisplayProjectCheckbox.setOnCheckedChangeListener(listener);
        mDisplayDetailsCheckbox.setOnCheckedChangeListener(listener);

	}
	
	private void setupSampleEntities() {
        long now = System.currentTimeMillis();
        mSampleProject = Project.newBuilder().setName("Sample project").build();
        mSampleContext = mGenerator.getSampleContext();
        mSampleTask = Task.newBuilder()
            .setDescription("Sample action")
            .setDetails("Additional action details")
            .setCreatedDate(now)
            .setModifiedDate(now)
            .setStartDate(now + DateUtils.DAY_IN_MILLIS * 2)
            .setDueDate(now + DateUtils.DAY_IN_MILLIS * 7)
            .setAllDay(true)
            .build();
	}
	
    @Override
	protected void onResume() {
		super.onResume();
		
        readPrefs();
	}

	@Override
    protected void onPause() {
        super.onPause();

        if (!mSaveChanges) {
        	revertPrefs();
        }
    }
    
	private void readPrefs() {
		Ln.d("Settings prefs controls");
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
		Ln.d("Reverting prefs");
		SharedPreferences.Editor ed = Preferences.getEditor(this);
		ed.putBoolean(DISPLAY_CONTEXT_ICON_KEY, mDisplayIcon);
		ed.putBoolean(DISPLAY_CONTEXT_NAME_KEY, mDisplayContext);
		ed.putBoolean(DISPLAY_DUE_DATE_KEY, mDisplayDueDate);
		ed.putBoolean(DISPLAY_PROJECT_KEY, mDisplayProject);
		ed.putBoolean(DISPLAY_DETAILS_KEY, mDisplayDetails);
		ed.commit();
	}
	
	private void savePrefs() {
		Ln.d("Saving prefs");
		SharedPreferences.Editor ed = Preferences.getEditor(this);
		ed.putBoolean(DISPLAY_CONTEXT_ICON_KEY, mDisplayIconCheckbox.isChecked());
		ed.putBoolean(DISPLAY_CONTEXT_NAME_KEY, mDisplayContextCheckbox.isChecked());
		ed.putBoolean(DISPLAY_DUE_DATE_KEY, mDisplayDueDateCheckbox.isChecked());
		ed.putBoolean(DISPLAY_PROJECT_KEY, mDisplayProjectCheckbox.isChecked());
		ed.putBoolean(DISPLAY_DETAILS_KEY, mDisplayDetailsCheckbox.isChecked());
		ed.commit();
	}
	
}
