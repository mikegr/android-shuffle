package org.dodgybits.shuffle.android.core.activity.flurry;

import com.google.inject.Inject;
import roboguice.activity.RoboPreferenceActivity;

public abstract class FlurryEnabledPreferenceActivity extends RoboPreferenceActivity {

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
