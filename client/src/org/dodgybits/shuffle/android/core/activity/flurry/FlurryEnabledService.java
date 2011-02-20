package org.dodgybits.shuffle.android.core.activity.flurry;

import android.content.Intent;
import com.google.inject.Inject;
import roboguice.service.RoboService;

public abstract class FlurryEnabledService extends RoboService {

    @Inject protected Analytics mAnalytics;
    

    @Override
    public void onStart(Intent intent, int startId) {
       super.onStart(intent, startId);
       mAnalytics.start();
    }
    
    @Override
    public void onDestroy()
    {
       super.onDestroy();
       mAnalytics.stop();
    }
    
}
