package org.dodgybits.shuffle.android.list.config;

import java.util.HashMap;

import org.dodgybits.shuffle.android.core.model.TaskQuery;
import org.dodgybits.shuffle.android.core.model.TaskQuery.PredefinedQuery;
import org.dodgybits.shuffle.android.list.activity.task.InboxActivity;
import org.dodgybits.shuffle.android.list.activity.task.TabbedDueActionsActivity;
import org.dodgybits.shuffle.android.list.activity.task.TopTasksActivity;

import android.content.Context;
import android.content.Intent;

public class StandardTaskQueries {

    public static final String cInbox = "inbox";
    public static final String cDueToday = "due_today";
    public static final String cDueNextWeek = "due_next_week";
    public static final String cDueNextMonth = "due_next_month";
    public static final String cNextTasks = "next_tasks";
    
    private static final TaskQuery cInboxQuery = 
        TaskQuery.newBuilder().setPredefined(PredefinedQuery.inbox).setDeletedTasksVisible(false).build();
        
    private static final TaskQuery cDueTodayQuery = 
        TaskQuery.newBuilder().setPredefined(PredefinedQuery.dueToday).setDeletedTasksVisible(false).build();

    private static final TaskQuery cDueNextWeekQuery = 
        TaskQuery.newBuilder().setPredefined(PredefinedQuery.dueNextWeek).setDeletedTasksVisible(false).build();

    private static final TaskQuery cDueNextMonthQuery = 
        TaskQuery.newBuilder().setPredefined(PredefinedQuery.dueNextMonth).setDeletedTasksVisible(false).build();
    
    private static final TaskQuery cNextTasksQuery = 
        TaskQuery.newBuilder()
        .setDeletedTasksVisible(false)
            .setPredefined(PredefinedQuery.nextTasks)
            .build();

    private static final HashMap<String,TaskQuery> cQueryMap = new HashMap<String,TaskQuery>();
    static {
        cQueryMap.put(cInbox, cInboxQuery);
        cQueryMap.put(cDueToday, cDueTodayQuery);
        cQueryMap.put(cDueNextWeek, cDueNextWeekQuery);
        cQueryMap.put(cDueNextMonth, cDueNextMonthQuery);
        cQueryMap.put(cNextTasks, cNextTasksQuery);
    }
    
    public static TaskQuery getQuery(String name) {
        return cQueryMap.get(name);
    }
    
    public static Intent getActivityIntent(Context context, String name) {
        if (cInbox.equals(name)) {
            return new Intent(context, InboxActivity.class);
        }
        
        if (cNextTasks.equals(name)) {
            return new Intent(context, TopTasksActivity.class);
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
