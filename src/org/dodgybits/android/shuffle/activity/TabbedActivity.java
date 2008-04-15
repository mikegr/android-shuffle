package org.dodgybits.android.shuffle.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * Not currently working due to Android bug.
 * See http://code.google.com/p/android/issues/detail?id=276
 */
public class TabbedActivity extends Activity {
	private TabHost mTabHost;
	
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(android.R.layout.tab_content);
        
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabSpec tabSpec = mTabHost.newTabSpec("Inbox");
        tabSpec.setContent(new Intent(this, HelpActivity.class));
        tabSpec.setIndicator("Inbox");
        mTabHost.addTab(tabSpec);
        tabSpec = mTabHost.newTabSpec("Top Task");
        tabSpec.setContent(new Intent(this, TopTasksActivity.class));
        tabSpec.setIndicator("Top Tasks");
        mTabHost.addTab(tabSpec);
        mTabHost.setCurrentTab(1); 
    }
}
