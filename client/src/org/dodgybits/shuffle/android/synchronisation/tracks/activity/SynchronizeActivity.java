package org.dodgybits.shuffle.android.synchronisation.tracks.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import org.dodgybits.shuffle.android.preference.view.Progress;
import org.dodgybits.shuffle.android.synchronisation.tracks.SyncProgressListener;
import org.dodgybits.shuffle.android.synchronisation.tracks.TracksSynchronizer;
import org.dodgybits.shuffle.android.synchronisation.tracks.WebClient;

/**
 * Activity to handle synchronization
 * 
 * @author Morten Nielsen
 */
public class SynchronizeActivity extends Activity implements SyncProgressListener {
    private TracksSynchronizer synchronizer = null;
    private TextView info;
    private ProgressBar progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.synchronize);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        super.onCreate(savedInstanceState);

        info = (TextView) findViewById(R.id.info_text);
        progress = (ProgressBar) findViewById(R.id.progress_horizontal);

        TextView url = (TextView) findViewById(R.id.syncUrl);
        TextView user = (TextView) findViewById(R.id.syncUser);
        url.setText(Preferences.getTracksUrl(this));
        user.setText(Preferences.getTracksUser(this));
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            synchronizer = TracksSynchronizer.getActiveSynchronizer(this);
        } catch (WebClient.ApiException ignored) {

        }

        if (synchronizer != null) {
            synchronizer.registerListener(this);
            if (synchronizer.getStatus() != AsyncTask.Status.RUNNING) {
                synchronizer.execute();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (synchronizer != null) {
            synchronizer.unRegisterListener(this);
        }

    }

    @Override
    public void progressUpdate(Progress progress) {
        info.setText(progress.getDetails());
        this.progress.setProgress(progress.getProgressPercent());
    }
}
