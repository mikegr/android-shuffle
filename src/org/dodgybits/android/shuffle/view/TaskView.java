package org.dodgybits.android.shuffle.view;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Preferences;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.util.DateUtils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskView extends ItemView<Task> {
	protected LabelView mContext;
	protected TextView mDescription;
	protected ImageView mIcon;
	protected TextView mDueDate;
	protected boolean mShowContext;
	protected TextView mProject;
	protected TextView mDetails;
	
	public TaskView(Context androidContext) {
		this(androidContext, true);
	}
	
	public TaskView(Context androidContext, boolean showContext) {
		super(androidContext);
		
        LayoutInflater vi = (LayoutInflater)androidContext.
			getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(getViewResourceId(), this, true); 
		
		mContext = (LabelView) findViewById(R.id.context);
		mDescription = (TextView) findViewById(R.id.description);
		mIcon = (ImageView) findViewById(R.id.icon);
		mDueDate = (TextView) findViewById(R.id.due_date);
		mProject = (TextView) findViewById(R.id.project);
		mDetails = (TextView) findViewById(R.id.details);
		mShowContext = showContext;
	}
		
	protected int getViewResourceId() {
		return R.layout.list_task_view;
	}
	
	public void updateView(Task task) {
		org.dodgybits.android.shuffle.model.Context context = task.context;
		boolean displayContext = Preferences.displayContextName(getContext());
		boolean displayIcon = Preferences.displayContextIcon(getContext());
		if (mShowContext && context != null && (displayContext || displayIcon)) {
			if (mIcon != null) {
				// add context icon if preferences indicate to
				Integer iconResource = context.iconResource;
				Integer id = 0;
				if (iconResource != null) {
					String name = getResources().getResourceName(iconResource) + "_small";
					id = getResources().getIdentifier(name, null, null);
				}
				if (id > 0 && displayIcon) {
					mIcon.setImageResource(id);
					mIcon.setVisibility(View.VISIBLE);
				} else {
					mIcon.setVisibility(View.GONE);
				}
			}
			
			mContext.setText(displayContext ? context.name : "");
			mContext.setColourIndex(context.colourIndex);
			// add context icon if preferences indicate to
			Integer iconResource = context.iconResource;
			Integer id = 0;
			if (iconResource != null) {
				String name = getResources().getResourceName(iconResource) + "_small";
				id = getResources().getIdentifier(name, null, null);
			}
			if (id > 0 && displayIcon) {
				mContext.setIcon(getResources().getDrawable(id));
			} else {
				mContext.setIcon(null);
			}
			mContext.setVisibility(View.VISIBLE);
		} else {
			mContext.setVisibility(View.GONE);
			if (mIcon != null) {
				mIcon.setVisibility(View.GONE);
			}
		}		
		CharSequence description = task.description;
		if (task.complete) {
			// add strike-through for completed tasks
			SpannableString desc = new SpannableString(description);
			desc.setSpan(new StrikethroughSpan(), 0, description.length(), Spanned.SPAN_PARAGRAPH);
			description = desc;
		}
		mDescription.setText(description);
		if (Preferences.displayDueDate(getContext()) && (task.dueDate != null)) {
			mDueDate.setText(DateUtils.displayDate(getContext(), task.dueDate));
			if (!DateUtils.isToday(task.dueDate) && (task.dueDate.getTime() < System.currentTimeMillis())) {
				// task is overdue
				mDueDate.setTypeface(Typeface.DEFAULT_BOLD);
				mDueDate.setTextColor(Color.RED);
			} else {
				mDueDate.setTypeface(Typeface.DEFAULT);
				mDueDate.setTextColor(getContext().getResources().getColor(R.drawable.dark_blue));
			}
			mDueDate.setVisibility(View.VISIBLE);
		} else {
			mDueDate.setVisibility(View.GONE);
		}
		if (Preferences.displayProject(getContext()) && (task.project != null)) {
			mProject.setText(task.project.name);
			mProject.setVisibility(View.VISIBLE);
		} else {
			mProject.setVisibility(View.GONE);
		}
		if (Preferences.displayDetails(getContext()) && (task.details != null)) {
			mDetails.setText(task.details);
			mDetails.setVisibility(View.VISIBLE);
		} else {
			mDetails.setVisibility(View.GONE);
		}
		
	}

	
}
