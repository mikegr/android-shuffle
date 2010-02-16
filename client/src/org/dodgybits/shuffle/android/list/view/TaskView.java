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

package org.dodgybits.shuffle.android.list.view;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.EntityCache;
import org.dodgybits.shuffle.android.core.util.DateUtils;
import org.dodgybits.shuffle.android.core.view.ContextIcon;
import org.dodgybits.shuffle.android.core.view.DrawableUtils;
import org.dodgybits.shuffle.android.preference.model.Preferences;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class TaskView extends ItemView<Task> {
    private EntityCache<Context> mContextCache;
    private EntityCache<Project> mProjectCache;
    
    protected LabelView mContext;
    protected TextView mDescription;
    protected TextView mDateDisplay;
    protected TextView mProject;
    protected TextView mDetails;
    protected boolean mShowContext;
    protected boolean mShowProject;
    
    public TaskView(android.content.Context androidContext, 
            EntityCache<Context> contextCache,
            EntityCache<Project> projectCache) {
        super(androidContext);
        
        mContextCache = contextCache;
        mProjectCache = projectCache;
        
        LayoutInflater vi = (LayoutInflater)androidContext.
            getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(getViewResourceId(), this, true); 
        
        mContext = (LabelView) findViewById(R.id.context);
        mDescription = (TextView) findViewById(R.id.description);
        mDateDisplay = (TextView) findViewById(R.id.due_date);
        mProject = (TextView) findViewById(R.id.project);
        mDetails = (TextView) findViewById(R.id.details);
        mShowContext = true;
        mShowProject = true;
        
        int bgColour = getResources().getColor(R.drawable.list_background);
        GradientDrawable drawable = DrawableUtils.createGradient(bgColour, Orientation.TOP_BOTTOM, 1.1f, 0.95f);
        setBackgroundDrawable(drawable);
    }
        
    protected int getViewResourceId() {
        return R.layout.list_task_view;
    }
    
    public void setShowContext(boolean showContext) {
        mShowContext = showContext;
    }
    
    public void setShowProject(boolean showProject) {
        mShowProject = showProject;
    }
    
    
    public void updateView(Task task) {
        updateContext(task);
        updateDescription(task);
        updateWhen(task);
        updateProject(task);
        updateDetails(task);
    }
    
    private void updateContext(Task task) {
        Context context = mContextCache.findById(task.getContextId());
        boolean displayContext = Preferences.displayContextName(getContext());
        boolean displayIcon = Preferences.displayContextIcon(getContext());
        if (mShowContext && context != null && (displayContext || displayIcon)) {           
            mContext.setText(displayContext ? context.getName() : "");
            mContext.setColourIndex(context.getColourIndex());
            // add context icon if preferences indicate to
            ContextIcon icon = ContextIcon.createIcon(context.getIconName(), getResources());
            int id = icon.smallIconId;
            if (id > 0 && displayIcon) {
                mContext.setIcon(getResources().getDrawable(id));
            } else {
                mContext.setIcon(null);
            }
            mContext.setVisibility(View.VISIBLE);
        } else {
            mContext.setVisibility(View.GONE);
        }               
    }
    
    private void updateDescription(Task task) {
        CharSequence description = task.getDescription();
        if (task.isComplete()) {
            // add strike-through for completed tasks
            SpannableString desc = new SpannableString(description);
            desc.setSpan(new StrikethroughSpan(), 0, description.length(), Spanned.SPAN_PARAGRAPH);
            description = desc;
        }
        mDescription.setText(description);  
    }

    private void updateWhen(Task task) {
        if (Preferences.displayDueDate(getContext())) {
            CharSequence dateRange = DateUtils.displayDateRange(
                    getContext(), task.getStartDate(), task.getDueDate(), !task.isAllDay());
            mDateDisplay.setText(dateRange);
            if (task.getDueDate() < System.currentTimeMillis()) {
                // task is overdue
                mDateDisplay.setTypeface(Typeface.DEFAULT_BOLD);
                mDateDisplay.setTextColor(Color.RED);
            } else {
                mDateDisplay.setTypeface(Typeface.DEFAULT);
                mDateDisplay.setTextColor(
                        getContext().getResources().getColor(R.drawable.dark_blue));
            }
            mDateDisplay.setVisibility(View.VISIBLE);
        } else {
            mDateDisplay.setVisibility(View.INVISIBLE);
        }
    }
    
    private void updateProject(Task task) {
        Project project = mProjectCache.findById(task.getProjectId());
        if (mShowProject && Preferences.displayProject(getContext()) && (project != null)) {
            mProject.setText(project.getName());
            mProject.setVisibility(View.VISIBLE);
        } else {
            mProject.setVisibility(View.INVISIBLE);
        }
    }
    
    private void updateDetails(Task task) {
        final String details = task.getDetails();
        if (Preferences.displayDetails(getContext()) && (details != null)) {
            mDetails.setText(details);
            mDetails.setVisibility(View.VISIBLE);
        } else {
            mDetails.setVisibility(View.INVISIBLE);
        }
    }
}
