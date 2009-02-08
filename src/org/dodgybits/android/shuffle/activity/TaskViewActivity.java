package org.dodgybits.android.shuffle.activity;

import java.text.DateFormat;
import java.util.Date;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.view.LabelView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskViewActivity extends Activity {
    private static final String cTag = "TaskViewActivity";
	
    private TextView mDescriptionWidget;
    private TextView mDetailsWidget;
    private LabelView mContextView;
    private ImageView mContextIcon;
    private TextView mProjectView;
    private TextView mDueDateView;
    private TextView mCompletedView;
    private Button mEditButton;
    protected Cursor mCursor;
    
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
        setContentView(R.layout.task_view);
        
        // The text view for our task description, identified by its ID in the XML file.
        mDescriptionWidget = (TextView) findViewById(R.id.description);
        mDetailsWidget = (TextView) findViewById(R.id.details);
        mContextView = (LabelView) findViewById(R.id.context);
        mContextIcon = (ImageView) findViewById(R.id.context_icon);
        mProjectView = (TextView) findViewById(R.id.project);
        mDueDateView = (TextView) findViewById(R.id.due_date_display);
        mCompletedView = (TextView) findViewById(R.id.completed);
        mEditButton = (Button) findViewById(R.id.edit_button);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(new Intent(Intent.ACTION_EDIT, getIntent().getData()));
            	finish();
            }
        });        
        mCursor = managedQuery(getIntent().getData(), Shuffle.Tasks.cExpandedProjection, null, null, null);
	}

    @Override
	protected void onResume() {
        Log.d(cTag, "onResume+");
		super.onResume();
		
        if (mCursor != null) {
            // Make sure we are at the one and only row in the cursor.
            mCursor.moveToFirst();
            Task task = BindingUtils.readTask(mCursor);
            mDescriptionWidget.setText(task.description);
            if (TextUtils.isEmpty(task.details)) {
            	mDetailsWidget.setText(R.string.undefined);
            } else {
            	mDetailsWidget.setText(task.details);
            }
            if (task.context != null) {
            	mContextView.setColourIndex(task.context.colourIndex, false);
                mContextView.setTextKeepState(task.context.name);
    			Integer iconResource = task.context.iconResource;
    			if (iconResource != null) {
    				mContextIcon.setImageResource(iconResource);
    			}
                
            }
            if (task.project != null) {
                mProjectView.setText(task.project.name);
            }
            
    		DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
    		Date dueDate = task.dueDate;
    		if (dueDate != null) {
    			mDueDateView.setText(format.format(dueDate));
    		}
    		// TODO fetch from resource...
    		mCompletedView.setText(task.complete ? "Completed" : "Incomplete");
	    } else {
	        setTitle(getText(R.string.error_title));
	        mDescriptionWidget.setText(getText(R.string.error_message));
	    }
        
	}

}
