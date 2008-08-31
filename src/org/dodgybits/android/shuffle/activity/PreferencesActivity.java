package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PreferencesActivity extends ListActivity {
    private static final String cTag = "PreferencesActivity";

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        
        String[] prefScreens = getResources().getStringArray(R.array.preference_screens);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.list_item_view, R.id.name, prefScreens);
        setListAdapter(adapter);
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // add just help?
        //MenuUtils.addPrefsHelpMenuItems(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (MenuUtils.checkCommonItemsSelected(item, this, -1)) {
//        	return true;
//        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	switch (position) {
    	case 0:
    		startActivity(new Intent(this, PreferencesAppearanceActivity.class));
    		break;
    	case 1:
    		startActivity(new Intent(this, PreferencesLayoutActivity.class));
    		break;
    	case 2:
    		startActivity(new Intent(this, PreferencesCleanActivity.class));
    		break;
    	}
    }

}
