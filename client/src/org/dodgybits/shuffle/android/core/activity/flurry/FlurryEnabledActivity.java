package org.dodgybits.shuffle.android.core.activity.flurry;

import roboguice.activity.GuiceActivity;

import com.google.inject.Inject;

public abstract class FlurryEnabledActivity extends GuiceActivity {

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
