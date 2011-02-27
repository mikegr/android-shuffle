package org.dodgybits.shuffle.android.list.config;

import static org.dodgybits.shuffle.android.core.model.persistence.selector.Flag.yes;

import java.util.HashMap;

import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector.PredefinedQuery;
import org.dodgybits.shuffle.android.list.activity.task.InboxActivity;
import org.dodgybits.shuffle.android.list.activity.task.TabbedDueActionsActivity;
import org.dodgybits.shuffle.android.list.activity.task.TicklerActivity;
import org.dodgybits.shuffle.android.list.activity.task.TopTasksActivity;

import android.content.Context;
import android.content.Intent;

public class StandardTaskQueries {

    public static final String cInbox = "inbox";
    public static final String cDueToday = "due_today";
    public static final String cDueNextWeek = "due_next_week";
    public static final String cDueNextMonth = "due_next_month";
    public static final String cNextTasks = "next_tasks";
    public static final String cTickler = "tickler";


    public static final String cDueTasksFilterPrefs = "due_tasks";
    public static final String cProjectFilterPrefs = "project";
    public static final String cContextFilterPrefs = "context";

    private static final TaskSelector cInboxQuery = 
        TaskSelector.newBuilder().setPredefined(PredefinedQuery.inbox).build();
        
    private static final TaskSelector cDueTodayQuery = 
        TaskSelector.newBuilder().setPredefined(PredefinedQuery.dueToday).build();

    private static final TaskSelector cDueNextWeekQuery = 
        TaskSelector.newBuilder().setPredefined(PredefinedQuery.dueNextWeek).build();

    private static final TaskSelector cDueNextMonthQuery = 
        TaskSelector.newBuilder().setPredefined(PredefinedQuery.dueNextMonth).build();
    
    private static final TaskSelector cNextTasksQuery = 
        TaskSelector.newBuilder().setPredefined(PredefinedQuery.nextTasks).build();

    private static final TaskSelector cTicklerQuery = 
        TaskSelector.newBuilder().setPredefined(PredefinedQuery.tickler).build();
    

    private static final HashMap<String,TaskSelector> cQueryMap = new HashMap<String,TaskSelector>();
    static {
        cQueryMap.put(cInbox, cInboxQuery);
        cQueryMap.put(cDueToday, cDueTodayQuery);
        cQueryMap.put(cDueNextWeek, cDueNextWeekQuery);
        cQueryMap.put(cDueNextMonth, cDueNextMonthQuery);
        cQueryMap.put(cNextTasks, cNextTasksQuery);
        cQueryMap.put(cTickler, cTicklerQuery);
    }

    private static final HashMap<String,String> cFilterPrefsMap = new HashMap<String,String>();
    static {
        cFilterPrefsMap.put(cInbox, cInbox);
        cFilterPrefsMap.put(cDueToday, cDueTasksFilterPrefs);
        cFilterPrefsMap.put(cDueNextWeek, cDueTasksFilterPrefs);
        cFilterPrefsMap.put(cDueNextMonth, cDueTasksFilterPrefs);
        cFilterPrefsMap.put(cNextTasks, cNextTasks);
        cFilterPrefsMap.put(cTickler, cTickler);
    }

    public static TaskSelector getQuery(String name) {
        return cQueryMap.get(name);
    }

    public static String getFilterPrefsKey(String name) {
        return cFilterPrefsMap.get(name);
    }

    public static Intent getActivityIntent(Context context, String name) {
        if (cInbox.equals(name)) {
            return new Intent(context, InboxActivity.class);
        }
        if (cNextTasks.equals(name)) {
            return new Intent(context, TopTasksActivity.class);
        }
        if (cTickler.equals(name)) {
            return new Intent(context, TicklerActivity.class);
        }

        PredefinedQuery query = PredefinedQuery.dueToday;
        if (cDueNextWeek.equals(name)) {
            query = PredefinedQuery.dueNextWeek;
        } else if (cDueNextMonth.equals(name)) {
            query = PredefinedQuery.dueNextMonth;
        }
        Intent intent = new Intent(context, TabbedDueActionsActivity.class);
        intent.putExtra(TabbedDueActionsActivity.DUE_MODE, query.name());
        return intent;
    }
    
}
