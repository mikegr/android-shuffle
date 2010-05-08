package org.dodgybits.shuffle.android.widget;

import java.util.ArrayList;
import java.util.List;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.view.IconArrayAdapter;
import org.dodgybits.shuffle.android.list.config.StandardTaskQueries;
import org.dodgybits.shuffle.android.preference.model.Preferences;

import roboguice.activity.GuiceListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * The configuration screen for the WidgetProvider widget.
 */
public class WidgetConfigure extends GuiceListActivity {
    static final String TAG = "WidgetConfigure";

    private static final int NEXT_TASKS = 0;
    private static final int DUE_TODAY = 1;
    private static final int DUE_NEXT_WEEK = 2;
    private static final int DUE_NEXT_MONTH = 3;
    private static final int INBOX = 4;
    
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private List<String> mLabels;

    public WidgetConfigure() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.launcher_shortcut);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        
        // Find the widget id from the intent. 
        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        
        mLabels = new ArrayList<String>();
        // TODO figure out a non-retarded way of added padding between text and icon
        mLabels.add("  " + getString(R.string.title_next_tasks));
        mLabels.add("  " + getString(R.string.title_due_today));
        mLabels.add("  " + getString(R.string.title_due_next_week));
        mLabels.add("  " + getString(R.string.title_due_next_month));
        mLabels.add("  " + getString(R.string.title_inbox));
        
        setTitle(R.string.title_widget_picker);
        
        Integer[] iconIds = new Integer[5];
        iconIds[NEXT_TASKS] = R.drawable.next_actions;
        iconIds[DUE_TODAY] = R.drawable.due_actions;
        iconIds[DUE_NEXT_WEEK] = R.drawable.due_actions;
        iconIds[DUE_NEXT_MONTH] = R.drawable.due_actions;
        iconIds[INBOX] = R.drawable.inbox;
        
        ArrayAdapter<CharSequence> adapter = new IconArrayAdapter(
                this, R.layout.text_item_view, R.id.name, mLabels.toArray(new String[0]), iconIds);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String key = Preferences.getWidgetQueryKey(mAppWidgetId);
        String queryName = queryValue(position);
        Preferences.getEditor(this).putString(key, queryName).commit();
        
        // Push widget update to surface with newly set prefix
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        WidgetProvider.updateAppWidget(this, appWidgetManager,
                mAppWidgetId, queryName);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
    
    private String queryValue(int position) {
        String result = null;
        switch (position) {
            case INBOX:
                result = StandardTaskQueries.cInbox;
                break;
        
            case NEXT_TASKS:
                result = StandardTaskQueries.cNextTasks;
                break;
                
            case DUE_TODAY:
                result = StandardTaskQueries.cDueToday;
                break;
                
            case DUE_NEXT_WEEK:
                result = StandardTaskQueries.cDueNextWeek;
                break;
                
            case DUE_NEXT_MONTH:
                result = StandardTaskQueries.cDueNextMonth;
                break;
        }
        return result;
    }

}



