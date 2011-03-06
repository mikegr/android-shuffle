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

package org.dodgybits.shuffle.android.view.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.EntityCache;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.util.CalendarUtils;
import org.dodgybits.shuffle.android.core.view.ContextIcon;
import org.dodgybits.shuffle.android.list.view.LabelView;
import org.dodgybits.shuffle.android.list.view.StatusView;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 * A generic activity for viewing a task.
 */
public class TaskViewActivity extends AbstractViewActivity<Task> {


    private @InjectView(R.id.project) TextView mProjectView;
    private @InjectView(R.id.description) TextView mDescriptionView;
    private @InjectView(R.id.context) LabelView mContextView;

    private @InjectView(R.id.details_entry) View mDetailsEntry;
    private @InjectView(R.id.details) TextView mDetailsView;

    private @InjectView(R.id.scheduling_entry) View mSchedulingEntry;
    private @InjectView(R.id.start) TextView mStartView;
    private @InjectView(R.id.due) TextView mDueView;

    private @InjectView(R.id.calendar_entry) View mCalendarEntry;
    private @InjectView(R.id.view_calendar_button) Button mViewCalendarButton;

//    private @InjectView(R.id.reminder_entry) View mReinderEntry;
//    private @InjectView(R.id.reminder) TextView mReminderView;

    private @InjectView(R.id.status) StatusView mStatusView;
    private @InjectView(R.id.completed) TextView mCompletedView;
    private @InjectView(R.id.created) TextView mCreatedView;
    private @InjectView(R.id.modified) TextView mModifiedView;

    @Inject private EntityCache<Project> mProjectCache;
    @Inject private EntityCache<Context> mContextCache;
    @Inject private TaskPersister mPersister;

    @Override
    protected void onCreate(Bundle icicle) {
        Ln.d("onCreate+");
        super.onCreate(icicle);
                
        loadCursors();

        mCursor.moveToFirst();
        mOriginalItem = mPersister.read(mCursor);
        updateUIFromItem(mOriginalItem);

        Drawable icon = getResources().getDrawable(R.drawable.ic_menu_view);
        icon.setBounds(0, 0, 36, 36);
        mViewCalendarButton.setCompoundDrawables(icon, null, null, null);
        mViewCalendarButton.setOnClickListener(this);
    }

    @Override
    protected void updateUIFromItem(Task task) {
        Context context = mContextCache.findById(task.getContextId());
        Project project = mProjectCache.findById(task.getProjectId());

        updateProject(project);
        updateDescription(task.getDescription());
        updateContext(context);
        updateDetails(task.getDetails());
        updateScheduling(task.getStartDate(), task.getDueDate());
        updateCalendar(task.getCalendarEventId());
        updateExtras(task, context, project);
    }

    @Override
    protected EntityPersister<Task> getPersister() {
        return mPersister;
    }

    /**
     * @return id of layout for this view
     */
    @Override
    protected int getContentViewResId() {
    	return R.layout.task_view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
	        case R.id.view_calendar_button: {
                Uri eventUri = ContentUris.appendId(
                        CalendarUtils.getEventContentUri().buildUpon(),
                        mOriginalItem.getCalendarEventId().getId()).build();
	        	Intent viewCalendarEntry = new Intent(Intent.ACTION_VIEW, eventUri);
	        	startActivity(viewCalendarEntry);
	        	break;
	        }

            default:
            	super.onClick(v);
            	break;
        }
    }

    private void loadCursors() {
        // Get the task if we're editing
        mCursor = managedQuery(mUri, TaskProvider.Tasks.FULL_PROJECTION, null, null, null);
        if (mCursor == null || mCursor.getCount() == 0) {
            // The cursor is empty. This can happen if the event was deleted.
            finish();
        }
    }

    private void updateProject(Project project) {
        if (project == null) {
            mProjectView.setVisibility(View.GONE);
        } else {
            mProjectView.setVisibility(View.VISIBLE);
            mProjectView.setText(project.getName());
        }

    }

    private void updateDescription(String description) {
        mDescriptionView.setTextKeepState(description);
    }

    private void updateContext(Context context) {
        if (context != null) {
            mContextView.setVisibility(View.VISIBLE);
            mContextView.setText(context.getName());
            mContextView.setColourIndex(context.getColourIndex());
            ContextIcon icon = ContextIcon.createIcon(context.getIconName(), getResources());
            int id = icon.smallIconId;
            if (id > 0) {
                mContextView.setIcon(getResources().getDrawable(id));
            } else {
                mContextView.setIcon(null);
            }
        } else {
            mContextView.setVisibility(View.INVISIBLE);
        }
    }

    private void updateDetails(String details) {
        if (TextUtils.isEmpty(details)) {
            mDetailsEntry.setVisibility(View.GONE);
        } else {
            mDetailsEntry.setVisibility(View.VISIBLE);
            mDetailsView.setText(details);
        }
    }

    private void updateScheduling(long startMillis, long dueMillis) {
        mStartView.setText(formatDateTime(startMillis));
        mDueView.setText(formatDateTime(dueMillis));
    }

    private void updateCalendar(Id calendarEntry) {
        if (calendarEntry.isInitialised()) {
            mCalendarEntry.setVisibility(View.VISIBLE);
        } else {
            mCalendarEntry.setVisibility(View.GONE);
        }
    }

    private final int cDateFormatFlags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR |
	                DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH |
	                DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_TIME;

    private String formatDateTime(long millis) {
    	String value;
    	if (millis > 0L) {
	        int flags = cDateFormatFlags;
            if (DateFormat.is24HourFormat(this)) {
                flags |= DateUtils.FORMAT_24HOUR;
            }
	        value = DateUtils.formatDateTime(this, millis, flags);
    	} else {
    		value = "";
    	}

        return value;
    }

    private void updateExtras(Task task, Context context, Project project) {
        mStatusView.updateStatus(task, context, project, !task.isComplete());
        mCompletedView.setText(task.isComplete() ? getString(R.string.completed) : "");
        mCreatedView.setText(formatDateTime(task.getCreatedDate()));
        mModifiedView.setText(formatDateTime(task.getModifiedDate()));
    }

}
