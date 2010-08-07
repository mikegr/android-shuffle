package org.dodgybits.shuffle.android.synchronisation.tracks.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.Analytics;
import org.dodgybits.shuffle.android.core.activity.flurry.FlurryEnabledActivity;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import org.dodgybits.shuffle.android.preference.view.Progress;
import org.dodgybits.shuffle.android.synchronisation.tracks.ApiException;
import org.dodgybits.shuffle.android.synchronisation.tracks.SyncProgressListener;
import org.dodgybits.shuffle.android.synchronisation.tracks.TracksSynchronizer;

import roboguice.inject.InjectView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.inject.Inject;
import com.google.inject.internal.Nullable;

/**
 * Activity to handle synchronization
 * 
 * @author Morten Nielsen
 */
public class SynchronizeActivity extends FlurryEnabledActivity implements SyncProgressListener {
    private TracksSynchronizer synchronizer = null;
    @InjectView(R.id.info_text) @Nullable TextView mInfo;
    @InjectView(R.id.progress_horizontal) @Nullable ProgressBar mProgress;

    @Inject Analytics mAnalytics;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.synchronize);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        super.onCreate(savedInstanceState);

        TextView url = (TextView) findViewById(R.id.syncUrl);
        TextView user = (TextView) findViewById(R.id.syncUser);
        url.setText(Preferences.getTracksUrl(this));
        user.setText(Preferences.getTracksUser(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            synchronizer = TracksSynchronizer.getActiveSynchronizer(this, mAnalytics);
        } catch (ApiException ignored) {

        }

        if (synchronizer != null) {
            synchronizer.registerListener(this);
            if (synchronizer.getStatus() != AsyncTask.Status.RUNNING) {
                synchronizer.execute();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (synchronizer != null) {
            synchronizer.unRegisterListener(this);
        }

    }

    @Override
    public void progressUpdate(Progress progress) {
        if (mInfo != null) {
            mInfo.setText(progress.getDetails());
        }
        if (mProgress != null) {
            mProgress.setProgress(progress.getProgressPercent());
        }
    }
}
