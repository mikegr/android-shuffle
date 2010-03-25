package org.dodgybits.shuffle.android.list.config;

import java.util.ArrayList;

import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.TaskQuery;
import org.dodgybits.shuffle.android.core.model.TaskQuery.PredefinedQuery;

public class StandardTaskQueries {

    public static final TaskQuery cInbox = TaskQuery.newBuilder()
        .setContexts(new ArrayList<Id>())
        .setProjects(new ArrayList<Id>())
        .build();
        
    public static final TaskQuery cDueToday = 
        TaskQuery.newBuilder().setPredefined(PredefinedQuery.dueToday).build();

    public static final TaskQuery cTopTasks = 
        TaskQuery.newBuilder()
            .setPredefined(PredefinedQuery.topTasks)
            .build();
    
}
