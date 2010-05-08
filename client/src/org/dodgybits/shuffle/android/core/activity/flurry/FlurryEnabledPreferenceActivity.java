package org.dodgybits.shuffle.android.core.activity.flurry;

import static org.dodgybits.shuffle.android.core.util.Constants.cFlurryApiKey;
import roboguice.activity.GuicePreferenceActivity;

import com.flurry.android.FlurryAgent;

public abstract class FlurryEnabledPreferenceActivity extends GuicePreferenceActivity {

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
