package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.MenuUtils;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;

public class CalendarActivity extends AbstractTaskListActivity {

	private int mMode;
    private Button mDayButton;
    private Button mWeekButton;
    private Button mMonthButton;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		mMode = Shuffle.Tasks.DAY_MODE;
		mDayButton = (Button) findViewById(R.id.day_button);
		mDayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mMode = Shuffle.Tasks.DAY_MODE;
            	updateCursor();
        	}
        });
		mWeekButton = (Button) findViewById(R.id.week_button);
		mWeekButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mMode = Shuffle.Tasks.WEEK_MODE;
            	updateCursor();
        	}
        });
		mMonthButton = (Button) findViewById(R.id.month_button);
		mMonthButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mMode = Shuffle.Tasks.MONTH_MODE;
            	updateCursor();
        	}
        });
	}
	
	private void updateCursor() {
    	mCursor = createItemQuery();
    	SimpleCursorAdapter adapter = (SimpleCursorAdapter)getListAdapter();
    	adapter.changeCursor(mCursor);
    	setTitle(createTitle());
	}

	@Override
	protected int getContentViewResId() {
		return R.layout.calendar;
	}

	@Override
	protected CharSequence createTitle() {
		return getString(R.string.title_calendar, getSelectedPeriod());
	}

	private String getSelectedPeriod() {
		String result = null;
		switch (mMode) {
		case Shuffle.Tasks.DAY_MODE:
			result = getString(R.string.day_button_title).toLowerCase();
			break;
		case Shuffle.Tasks.WEEK_MODE:
			result = getString(R.string.week_button_title).toLowerCase();
			break;
		case Shuffle.Tasks.MONTH_MODE:
			result = getString(R.string.month_button_title).toLowerCase();
			break;
		}
		return result;
	}
	
	@Override
	protected Uri getListContentUri() {
		return Shuffle.Tasks.cDueTasksContentURI.buildUpon().appendPath(String.valueOf(mMode)).build();
	}
	
	@Override
	protected int getCurrentViewMenuId() {
		return MenuUtils.CALENDAR_ID;
	}

}
