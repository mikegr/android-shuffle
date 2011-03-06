package org.dodgybits.shuffle.android.editor.activity;

import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.FlurryEnabledActivity;
import org.dodgybits.shuffle.android.list.activity.State;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

public class TaskEditorSchedulingActivity extends FlurryEnabledActivity {

    private boolean mShowStart;
    private Time mStartTime;
    private boolean mShowDue;
    private Time mDueTime;

    private @InjectView(R.id.start_date) Button mStartDateButton;
    private @InjectView(R.id.due_date) Button mDueDateButton;
    private @InjectView(R.id.start_time) Button mStartTimeButton;
    private @InjectView(R.id.due_time) Button mDueTimeButton;
    private @InjectView(R.id.clear_dates) Button mClearButton;
    private @InjectView(R.id.is_all_day) CheckBox mAllDayCheckBox;

    private @InjectView(R.id.gcal_entry) View mUpdateCalendarEntry;
    private CheckBox mUpdateCalendarCheckBox;
    private TextView mCalendarLabel;
    private TextView mCalendarDetail;

    @Override
    protected void onCreate(Bundle icicle) {
        Ln.d("onCreate+");
        super.onCreate(icicle);

        mStartTime = new Time();
        mDueTime = new Time();
    }


}
