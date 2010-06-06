package org.dodgybits.shuffle.android.core.activity.flurry;

import static org.dodgybits.shuffle.android.core.util.Constants.cFlurryApiKey;

import java.util.Map;

import org.dodgybits.shuffle.android.preference.model.Preferences;

import android.content.Context;

import com.flurry.android.FlurryAgent;
import com.google.inject.Inject;

public class Analytics {

    private Context mContext;
    
    @Inject
    public Analytics(Context context) {
        mContext = context;
    }
    
    public void start() {
        if (isEnabled()) {
            FlurryAgent.onStartSession(mContext, cFlurryApiKey);
        }
    }
    
    public void stop() {
        if (isEnabled()) {
            FlurryAgent.onEndSession(mContext);
        }
    }
    
    public void onEvent(String eventId, Map<String, String> parameters) {
        if (isEnabled()) {
            FlurryAgent.onEvent(eventId, parameters);
        }
    }
    
    public void onEvent(String eventId) {
        if (isEnabled()) {
            FlurryAgent.onEvent(eventId);
        }
    }
    
    public void onError(String errorId, String message, String errorClass) {
        if (isEnabled()) {
            FlurryAgent.onError(errorId, message, errorClass);
        }
    }
    
    public void onPageView(Context context) {
        if (isEnabled()) {
            FlurryAgent.onPageView();
        }
    }
    
    private boolean isEnabled() {
        return Preferences.isAnalyticsEnabled(mContext);
    }
    
}
