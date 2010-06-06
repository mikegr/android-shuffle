package org.dodgybits.shuffle.android.core.activity.flurry;

import com.google.inject.Inject;

import roboguice.activity.GuiceListActivity;

public abstract class FlurryEnabledListActivity extends GuiceListActivity {

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
