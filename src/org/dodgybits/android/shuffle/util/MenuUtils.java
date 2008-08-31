package org.dodgybits.android.shuffle.util;

import static org.dodgybits.android.shuffle.model.Preferences.EXPANDABLE_VIEW;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.activity.ContextsActivity;
import org.dodgybits.android.shuffle.activity.ExpandableContextsActivity;
import org.dodgybits.android.shuffle.activity.ExpandableProjectsActivity;
import org.dodgybits.android.shuffle.activity.HelpActivity;
import org.dodgybits.android.shuffle.activity.InboxActivity;
import org.dodgybits.android.shuffle.activity.PreferencesActivity;
import org.dodgybits.android.shuffle.activity.ProjectsActivity;
import org.dodgybits.android.shuffle.activity.TabbedDueActionsActivity;
import org.dodgybits.android.shuffle.activity.TopTasksActivity;
import org.dodgybits.android.shuffle.model.Preferences;
import org.dodgybits.android.shuffle.model.State;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

public class MenuUtils {
	private static final String cTag = "MenuUtils";

	private MenuUtils() {
		// deny
	}
	
    // Identifiers for our menu items.
    public static final int REVERT_ID = Menu.FIRST;
    public static final int DISCARD_ID = Menu.FIRST + 1;
    public static final int DELETE_ID = Menu.FIRST + 2;
    public static final int INSERT_ID = Menu.FIRST + 3;
    public static final int INSERT_CHILD_ID = Menu.FIRST + 3;
    public static final int INSIDE_GROUP_ID = Menu.FIRST + 4;
    
    public static final int INBOX_ID = Menu.FIRST + 10;
    public static final int CALENDAR_ID = Menu.FIRST + 11;
    public static final int TOP_TASKS_ID = Menu.FIRST + 12;
    public static final int PROJECT_ID = Menu.FIRST + 13;
    public static final int CONTEXT_ID = Menu.FIRST + 14;
    public static final int PREFERENCE_ID = Menu.FIRST + 15;
    public static final int HELP_ID = Menu.FIRST + 16;

    public static final int CLEAN_INBOX_ID = Menu.FIRST + 50;

    // Menu item for activity specific items
    public static final int COMPLETE_ID = Menu.FIRST + 100;
    public static final int MOVE_UP_ID = Menu.FIRST + 101;
    public static final int MOVE_DOWN_ID = Menu.FIRST + 102;

    
	public static void addInsertMenuItems(Menu menu, String itemName, boolean isTaskList, Context context) {
		String menuName = context.getResources().getString(R.string.menu_insert, itemName);
		MenuItem item = menu.add(0, INSERT_ID, 0, menuName).setIcon(R.drawable.list_add);
        item.setAlphabeticShortcut(isTaskList ? 'c' : 'a');
	}

	public static void addExpandableInsertMenuItems(Menu menu, String groupName, String childName, Context context) {
		String menuName;
//		menuName = context.getResources().getString(R.string.menu_insert, childName);
//        menu.add(0, INSERT_CHILD_ID, menuName, R.drawable.list_add).setAlphabeticShortcut('c');
		menuName = context.getResources().getString(R.string.menu_insert, groupName);
        menu.add(0, INSIDE_GROUP_ID, 0, menuName).setIcon(R.drawable.folder_new).setAlphabeticShortcut('a');
	}

	public static void addViewMenuItems(Menu menu, int currentViewMenuId) {
        SubMenu viewMenu  = menu.addSubMenu(0, 0, 0, R.string.menu_view)
        	.setIcon(R.drawable.preferences_system_windows);
        MenuItem item;
        item = viewMenu.add(0, INBOX_ID, 0, R.string.title_inbox)
        	.setChecked(INBOX_ID == currentViewMenuId)
        	.setShortcut('1','1');
        item = viewMenu.add(0, CALENDAR_ID, 0, R.string.title_due_tasks)
        	.setChecked(INBOX_ID == currentViewMenuId)
        	.setShortcut('2','2');
        item = viewMenu.add(0, TOP_TASKS_ID, 0, R.string.title_next_tasks)
        	.setChecked(TOP_TASKS_ID == currentViewMenuId)
        	.setShortcut('3','3');
        item = viewMenu.add(0, PROJECT_ID, 0, R.string.title_project)
        	.setChecked(PROJECT_ID == currentViewMenuId)
        	.setShortcut('4','4');
        item = viewMenu.add(0, CONTEXT_ID, 0, R.string.title_context)
        	.setChecked(CONTEXT_ID == currentViewMenuId)
        	.setShortcut('5','5');
	}
	
	public static void addEditorMenuItems(Menu menu, int state) {
        // Build the menus that are shown when editing.
        if (state == State.STATE_EDIT) {
            menu.add(0, REVERT_ID, 0, R.string.menu_revert)
            	.setIcon(R.drawable.edit_undo).setAlphabeticShortcut('r');
            menu.add(0, DELETE_ID, 0, R.string.menu_delete)
            	.setIcon(R.drawable.edit_delete).setAlphabeticShortcut('d');

        // Build the menus that are shown when inserting.
        } else {
            menu.add(0, DISCARD_ID, 0, R.string.menu_discard)
            	.setIcon(R.drawable.edit_delete).setAlphabeticShortcut('d');
        }
	}
	
	public static void addPrefsHelpMenuItems(Menu menu) {
        menu.add(0, PREFERENCE_ID, 0, R.string.menu_preferences)
        	.setIcon(R.drawable.preferences_desktop).setAlphabeticShortcut('p');
        menu.add(0, HELP_ID, 0, R.string.menu_help)
        	.setIcon(R.drawable.help_browser).setAlphabeticShortcut('h');
	}
		
	public static void addAlternativeMenuItems(Menu menu, Uri uri, Activity activity) {
        // Generate any additional actions that can be performed on the
        // overall list.  In a normal install, there are no additional
        // actions found here, but this allows other applications to extend
        // our menu with their own actions.
		
		/* Disable for now since it's adding our own activities
		Intent intent = new Intent(null, uri); 
        intent.addCategory(Intent.ALTERNATIVE_CATEGORY);
        menu.addIntentOptions(
            Menu.ALTERNATIVE, 0, new ComponentName(activity, activity.getClass()),
            null, intent, 0, null);
            */
	}

	public static void addSelectedAlternativeMenuItems(Menu menu, Uri uri, Activity activity, boolean includeView) {
        // Build menu...  always starts with the EDIT action...
		
        int viewIndex = 0;
        int editIndex = (includeView ? 1 : 0);
        Intent[] specifics = new Intent[editIndex + 1];
        MenuItem[] items = new MenuItem[editIndex + 1];
        if (includeView) {
	        specifics[viewIndex] = new Intent(Intent.ACTION_VIEW, uri);
        }
        specifics[editIndex] = new Intent(Intent.ACTION_EDIT, uri);

        // ... is followed by whatever other actions are available...
        Intent intent = new Intent(null, uri);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0, null, specifics,
                              intent, 0, items);

        // Give a shortcut to the edit action.
        if (items[editIndex] != null) {
            items[editIndex].setAlphabeticShortcut('e');
            items[editIndex].setIcon(R.drawable.edit_find_replace);
        }		
        if (includeView && items[viewIndex] != null) {
            items[viewIndex].setAlphabeticShortcut('v');
            items[viewIndex].setIcon(R.drawable.edit_find_replace);
        }		
	}
	
	public static void addDeleteMenuItem(Menu menu) {
        menu.add(Menu.CATEGORY_ALTERNATIVE, DELETE_ID, 0, R.string.menu_delete).setIcon(R.drawable.edit_delete).setAlphabeticShortcut('d');
    }

	public static void addCompleteMenuItem(Menu menu) {
        menu.add(Menu.CATEGORY_ALTERNATIVE, COMPLETE_ID, 0, R.string.menu_complete).setAlphabeticShortcut('x');
    }

	public static void addCleanInboxMenuItem(Menu menu) {
        menu.add(0, CLEAN_INBOX_ID, 0, R.string.clean_inbox_button_title).setIcon(R.drawable.edit_clear).setAlphabeticShortcut('i');
    }

	public static void addMoveMenuItems(Menu menu) {
        menu.add(Menu.CATEGORY_ALTERNATIVE, MOVE_UP_ID, 0, R.string.menu_move_up).setIcon(R.drawable.go_up).setAlphabeticShortcut('k');
        menu.add(Menu.CATEGORY_ALTERNATIVE, MOVE_DOWN_ID, 0, R.string.menu_move_down).setIcon(R.drawable.go_down).setAlphabeticShortcut('j');
    }

	public static boolean checkCommonItemsSelected(MenuItem item, Activity activity, int currentViewMenuId) {
		return checkCommonItemsSelected(item.getItemId(), activity, currentViewMenuId, true);
	}
	
	public static boolean checkCommonItemsSelected(int menuItemId, Activity activity, int currentViewMenuId) {
		return checkCommonItemsSelected(menuItemId, activity, currentViewMenuId, true);
	}
	
	public static boolean checkCommonItemsSelected(int menuItemId, Activity activity, int currentViewMenuId, boolean finishCurrentActivity) {
		switch (menuItemId) {
        case MenuUtils.INBOX_ID:
        	if (currentViewMenuId != INBOX_ID) {
            	Log.d(cTag, "Switching to inbox");
            	activity.startActivity(new Intent(activity, InboxActivity.class));
        		if (finishCurrentActivity) activity.finish();
        	}
        	return true;
        case MenuUtils.CALENDAR_ID:
        	if (currentViewMenuId != CALENDAR_ID) {
            	Log.d(cTag, "Switching to calendar");
            	//activity.startActivity(new Intent(activity, CalendarActivity.class));
            	activity.startActivity(new Intent(activity, TabbedDueActionsActivity.class));
        		if (finishCurrentActivity) activity.finish();
        	}
        	return true;
        case MenuUtils.TOP_TASKS_ID:
        	if (currentViewMenuId != TOP_TASKS_ID) {
        		Log.d(cTag, "Switching to top tasks");
        		activity.startActivity(new Intent(activity, TopTasksActivity.class));
        		if (finishCurrentActivity) activity.finish();
        	}
        	return true;
        case MenuUtils.PROJECT_ID:
        	if (currentViewMenuId != PROJECT_ID) {
            	Log.d(cTag, "Switching to project list");
                Class<? extends Activity> activityClass = null;
            	if (Preferences.getProjectView(activity) == EXPANDABLE_VIEW) {
                	activityClass = ExpandableProjectsActivity.class;
            	} else {
                	activityClass = ProjectsActivity.class;
            	}
            	activity.startActivity(new Intent(activity, activityClass));
        		if (finishCurrentActivity) activity.finish();
        	}
        	return true;
        case CONTEXT_ID:
        	if (currentViewMenuId != CONTEXT_ID) {
            	Log.d(cTag, "Switching to context list");
                Class<? extends Activity> activityClass = null;
            	if (Preferences.getContextView(activity) == EXPANDABLE_VIEW) {
                	activityClass = ExpandableContextsActivity.class;
            	} else {
                	activityClass = ContextsActivity.class;
            	}
            	activity.startActivity(new Intent(activity, activityClass));
        		if (finishCurrentActivity) activity.finish();
        	}
        	return true;
        case PREFERENCE_ID:
        	Log.d(cTag, "Bringing up preferences");
        	activity.startActivity(new Intent(activity, PreferencesActivity.class));
        	return true;
	    case HELP_ID:
	    	Log.d(cTag, "Bringing up help");
	    	Intent intent = new Intent(activity, HelpActivity.class);
	    	intent.putExtra(HelpActivity.cHelpPage, getHelpScreen(currentViewMenuId));
	    	activity.startActivity(intent);
	    	
	    	
	    	return true;
        }
        return false;
	}
	
	private static int getHelpScreen(int currentViewMenuId) {
		int result = 0;
		switch (currentViewMenuId) {
		case INBOX_ID:
			result = 1;
			break;
		case PROJECT_ID:
			result = 2;
			break;
		case CONTEXT_ID:
			result = 3;
			break;
		case TOP_TASKS_ID:
			result = 4;
			break;
		case CALENDAR_ID:
			result = 5;
			break;
		}
		return result;
	}
	
}
