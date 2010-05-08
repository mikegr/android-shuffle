package org.dodgybits.shuffle.android.core.activity.flurry;

import static org.dodgybits.shuffle.android.core.util.Constants.cFlurryApiKey;
import roboguice.activity.GuiceListActivity;

import com.flurry.android.FlurryAgent;

public abstract class FlurryEnabledListActivity extends GuiceListActivity {

    @Override
    public void onStart()
    {
       super.onStart();
       FlurryAgent.onStartSession(this, cFlurryApiKey);
    }
    
    @Override
    public void onStop()
    {
       super.onStop();
       FlurryAgent.onEndSession(this);
    }
    
}
