package org.dodgybits.shuffle.android.core.activity.flurry;

import static org.dodgybits.shuffle.android.core.util.Constants.cFlurryApiKey;
import roboguice.activity.GuiceExpandableListActivity;

import com.flurry.android.FlurryAgent;

public abstract class FlurryEnabledExpandableListActivity extends GuiceExpandableListActivity {

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
