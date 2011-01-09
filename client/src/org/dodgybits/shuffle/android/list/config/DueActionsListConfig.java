package org.dodgybits.shuffle.android.list.config;

import android.content.ContextWrapper;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.Flag;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;

public class DueActionsListConfig extends AbstractTaskListConfig {

    private TaskSelector.PredefinedQuery mMode = TaskSelector.PredefinedQuery.dueToday;

    @Inject
    public DueActionsListConfig(TaskPersister persister) {

        super(
                createSelector(TaskSelector.PredefinedQuery.dueToday),
                persister,
                new ListPreferenceSettings("due_tasks").setDefaultCompleted(Flag.no));
    }

    public TaskSelector.PredefinedQuery getMode() {
        return mMode;
    }

    public void setMode(TaskSelector.PredefinedQuery mode) {
        mMode = mode;
        setTaskSelector(createSelector(mode));
    }

    @Override
    public int getContentViewResId() {
        return R.layout.tabbed_due_tasks;
    }

    public int getCurrentViewMenuId() {
        return MenuUtils.CALENDAR_ID;
    }

    public String createTitle(ContextWrapper context)
    {
        return context.getString(R.string.title_calendar, getSelectedPeriod(context));
    }

    private String getSelectedPeriod(ContextWrapper context) {
        String result = null;
        switch (mMode) {
        case dueToday:
            result = context.getString(R.string.day_button_title).toLowerCase();
            break;
        case dueNextWeek:
            result = context.getString(R.string.week_button_title).toLowerCase();
            break;
        case dueNextMonth:
            result = context.getString(R.string.month_button_title).toLowerCase();
            break;
        }
        return result;
    }

    private static TaskSelector createSelector(TaskSelector.PredefinedQuery mMode) {
        return TaskSelector.newBuilder().setPredefined(mMode).build();
    }

}
