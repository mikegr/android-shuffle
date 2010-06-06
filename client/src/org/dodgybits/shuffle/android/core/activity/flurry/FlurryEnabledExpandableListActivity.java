package org.dodgybits.shuffle.android.core.activity.flurry;

import com.google.inject.Inject;

import roboguice.activity.GuiceExpandableListActivity;

public abstract class FlurryEnabledExpandableListActivity extends GuiceExpandableListActivity {

    @Inject protected Analytics mAnalytics;
    
    @Override
    public void onStart()
    {
       super.onStart();
       mAnalytics.start();
    }
    
    @Override
    public void onStop()
    {
       super.onStop();
       mAnalytics.stop();
    }
    
}
