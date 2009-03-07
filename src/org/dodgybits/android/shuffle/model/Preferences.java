package org.dodgybits.android.shuffle.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
	public static final String FIRST_TIME = "first_time";
	public static final String SCREEN_KEY = "screen";
	public static final String DELETE_COMPLETED_PERIOD_KEY = "delete_complete_period_str";
	public static final String LAST_DELETE_COMPLETED_KEY = "last_delete_completed";
	public static final String LAST_INBOX_CLEAN_KEY = "last_inbox_clean";
	
	public static final String DISPLAY_CONTEXT_ICON_KEY = "display_context_icon";
	public static final String DISPLAY_CONTEXT_NAME_KEY = "display_context_name";
	public static final String DISPLAY_PROJECT_KEY = "display_project";
	public static final String DISPLAY_DETAILS_KEY = "display_details";
	public static final String DISPLAY_DUE_DATE_KEY = "display_due_date";
	
	public static final String PROJECT_VIEW_KEY = "project_view";
	public static final String CONTEXT_VIEW_KEY = "context_view";
	
	public enum DeleteCompletedPeriod {
		hourly, daily, weekly, never
	}
	
	private static SharedPreferences sPrefs = null;
	
	private static SharedPreferences getSharedPreferences(Context context) {
		if (sPrefs == null) {
			sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return sPrefs;
	}
	
	public static boolean isFirstTime(Context context) {
		getSharedPreferences(context);
		return sPrefs.getBoolean(FIRST_TIME, true);
	}
	
	public static String getDeleteCompletedPeriod(Context context) {
		getSharedPreferences(context);
		return sPrefs.getString(DELETE_COMPLETED_PERIOD_KEY, DeleteCompletedPeriod.never.name());
	}

	public static long getLastDeleteCompleted(Context context) {
		getSharedPreferences(context);
		return sPrefs.getLong(LAST_DELETE_COMPLETED_KEY, 0L);
	}

	public static long getLastInboxClean(Context context) {
		getSharedPreferences(context);
		return sPrefs.getLong(LAST_INBOX_CLEAN_KEY, 0L);
	}

	public static Boolean isProjectViewExpandable(Context context) {
		getSharedPreferences(context);
		return !sPrefs.getBoolean(PROJECT_VIEW_KEY, false);
	}

	public static Boolean isContextViewExpandable(Context context) {
		getSharedPreferences(context);
		return !sPrefs.getBoolean(CONTEXT_VIEW_KEY, true);
	}

	public static boolean displayContextIcon(Context context) {
		getSharedPreferences(context);
		return sPrefs.getBoolean(DISPLAY_CONTEXT_ICON_KEY, true);
	}

	public static boolean displayContextName(Context context) {
		getSharedPreferences(context);
		return sPrefs.getBoolean(DISPLAY_CONTEXT_NAME_KEY, true);
	}

	public static boolean displayDueDate(Context context) {
		getSharedPreferences(context);
		return sPrefs.getBoolean(DISPLAY_DUE_DATE_KEY, true);
	}

	public static boolean displayProject(Context context) {
		getSharedPreferences(context);
		return sPrefs.getBoolean(DISPLAY_PROJECT_KEY, true);
	}

	public static boolean displayDetails(Context context) {
		getSharedPreferences(context);
		return sPrefs.getBoolean(DISPLAY_DETAILS_KEY, true);
	}

	public static SharedPreferences.Editor getEditor(Context context) {
		getSharedPreferences(context);
		return sPrefs.edit();
	}
	
	public static void cleanUpInbox(Context context) {
		SharedPreferences.Editor ed = getEditor(context);
		ed.putLong(LAST_INBOX_CLEAN_KEY, System.currentTimeMillis());
		ed.commit();
	}

	
	
}
