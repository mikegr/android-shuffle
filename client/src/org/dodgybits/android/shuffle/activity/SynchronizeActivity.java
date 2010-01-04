package org.dodgybits.android.shuffle.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Preferences;
import org.dodgybits.android.shuffle.server.tracks.TracksSynchronizer;
import org.dodgybits.android.shuffle.server.tracks.WebClient;

/**
 * Created by IntelliJ IDEA.
 * User: Morten
 * Date: 2009-dec-31
 * Time: 15:15:31
 * Activity to handle sycnhronization
 */
public class SynchronizeActivity extends Activity {
    private TracksSynchronizer synchronizer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
            setContentView(R.layout.synchronize);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        super.onCreate(savedInstanceState);

        TextView info = (TextView) findViewById(R.id.info_text);
        ProgressBar progress = (ProgressBar) findViewById(R.id.progress_horizontal);

        
          TextView url = (TextView) findViewById(R.id.syncUrl);
        TextView user = (TextView) findViewById(R.id.syncUser);
        url.setText(Preferences.getTracksUrl(this));
        user.setText(Preferences.getTracksUser(this));
        try {
            synchronizer = new TracksSynchronizer(getContentResolver(),getResources(), new WebClient(this, Preferences.getTracksUser(this), Preferences.getTracksPassword(this)), this, info, progress, Preferences.getTracksUrl(this));
        } catch (WebClient.ApiException ignored) {

        }
        if (synchronizer != null) {
            synchronizer.execute();
        }
    }

}
