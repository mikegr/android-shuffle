package org.dodgybits.shuffle.android.core.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.FlurryEnabledListActivity;
import org.dodgybits.shuffle.android.core.view.IconArrayAdapter;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.persistence.provider.Shuffle;

import java.util.ArrayList;
import java.util.List;

public class LauncherShortcutActivity extends FlurryEnabledListActivity {
	private static final String cScreenId = "screenId";
	
    private static final int NEW_TASK = 0;
    private static final int INBOX = 1;
    private static final int DUE_TASKS = 2;
    private static final int TOP_TASKS = 3;
    private static final int PROJECTS = 4;
    private static final int CONTEXTS = 5;
	
    private List<String> mLabels;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        final Intent intent = getIntent();
        final String action = intent.getAction();

        setContentView(R.layout.launcher_shortcut);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        
        String[] perspectives = getResources().getStringArray(R.array.perspectives);
        
        mLabels = new ArrayList<String>();
        // TODO figure out a non-retarded way of added padding between text and icon
        mLabels.add(0, "  " + getString(R.string.title_new_task));
        for (String label : perspectives) {
            mLabels.add("  " + label);
        	
        }

        if (!Intent.ACTION_CREATE_SHORTCUT.equals(action)) {
        	int screenId = intent.getExtras().getInt(cScreenId, -1);
        	if (screenId < INBOX && screenId > CONTEXTS) {
        		// unknown id - just go to BootstrapActivity
            	startActivity(new Intent(this, BootstrapActivity.class));
        	} else {
        		int menuIndex = (screenId - INBOX) + MenuUtils.INBOX_ID;
        		MenuUtils.checkCommonItemsSelected(
        				menuIndex, this, -1, false);
        	}
        	finish();
        	return;
        }
        
        setTitle(R.string.title_shortcut_picker);
        
		Integer[] iconIds = new Integer[6];
		iconIds[NEW_TASK] = R.drawable.list_add;
		iconIds[INBOX] = R.drawable.inbox;
		iconIds[DUE_TASKS] = R.drawable.due_actions;
		iconIds[TOP_TASKS] = R.drawable.next_actions;
		iconIds[PROJECTS] = R.drawable.projects;
		iconIds[CONTEXTS] = R.drawable.contexts;
		
        ArrayAdapter<CharSequence> adapter = new IconArrayAdapter(
        		this, R.layout.text_item_view, R.id.name, mLabels.toArray(new String[0]), iconIds);
        setListAdapter(adapter);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Intent shortcutIntent;
    	Parcelable iconResource;
    	if (position == NEW_TASK) {
    		shortcutIntent = new Intent(Intent.ACTION_INSERT, Shuffle.Tasks.CONTENT_URI);
    		iconResource = Intent.ShortcutIconResource.fromContext(
                    this,  R.drawable.add_task_3d);
    	} else {
    		shortcutIntent = new Intent(this, LauncherShortcutActivity.class);
    		shortcutIntent.putExtra(cScreenId, position);
    		iconResource = Intent.ShortcutIconResource.fromContext(
                    this,  R.drawable.app_icon_3d);
    	}

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, mLabels.get(position).trim());
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

        // Now, return the result to the launcher

        setResult(RESULT_OK, intent);
        finish();
    }
    
}
