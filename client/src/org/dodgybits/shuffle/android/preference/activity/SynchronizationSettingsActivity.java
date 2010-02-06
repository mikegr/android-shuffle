package org.dodgybits.shuffle.android.preference.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import org.dodgybits.android.shuffle.R;

import static org.dodgybits.shuffle.android.preference.model.Preferences.*;

import org.dodgybits.shuffle.android.preference.model.Preferences;
import org.dodgybits.shuffle.android.synchronisation.tracks.WebClient;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Activity that changes the options set for synchronization
 */
public class SynchronizationSettingsActivity extends Activity {
    private EditText mUrlTextbox;
    private EditText mUserTextbox;
    private EditText mPassTextbox;
    private Spinner mInterval;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.synchronize_settings);

        String[] options = new String[] {
                getText(R.string.sync_interval_none).toString(),
                getText(R.string.sync_interval_30min).toString(),
                getText(R.string.sync_interval_1h).toString(),
                getText(R.string.sync_interval_2h).toString(),
                getText(R.string.sync_interval_3h).toString() };

        mUrlTextbox = (EditText) findViewById(R.id.url);
        mUserTextbox = (EditText) findViewById(R.id.user);
        mPassTextbox = (EditText) findViewById(R.id.pass);
        mInterval = (Spinner) findViewById(R.id.sync_interval);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                this, android.R.layout.simple_list_item_1, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mInterval.setAdapter(adapter);
        mInterval.setSelection(Preferences.getTracksInterval(this));

        String tracksUrl = Preferences.getTracksUrl(this);
        mUrlTextbox.setText(tracksUrl);
        // select server portion of URL
        int startIndex = 0;
        int index = tracksUrl.indexOf("://");
        if (index > 0) {
            startIndex = index + 3;
        }
        mUrlTextbox.setSelection(startIndex, tracksUrl.length());
        
        mUserTextbox.setText(Preferences.getTracksUser(this));
        mPassTextbox.setText(Preferences.getTracksPassword(this));

        CompoundButton.OnClickListener saveClick = new CompoundButton.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (savePrefs()) {
                    finish();
                } else {
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(getApplicationContext(),
                            R.string.tracks_failed_to_check_url, duration);
                    toast.show();
                }
            }
        };

        CompoundButton.OnClickListener cancelClick = new CompoundButton.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        };
        final int color = mUrlTextbox.getCurrentTextColor();
        verifyUrl(color);
        // Setup the bottom buttons
        View view = findViewById(R.id.saveButton);
        view.setOnClickListener(saveClick);
        view = findViewById(R.id.discardButton);
        view.setOnClickListener(cancelClick);

        View url = findViewById(R.id.url);
        url.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                verifyUrl(color);

                return false;
            }
        });
    }

    private boolean verifyUrl(int color) {
        try {
            new URI(mUrlTextbox.getText().toString());
            mUrlTextbox.setTextColor(color);
            return true;
        } catch (URISyntaxException e) {
            mUrlTextbox.setTextColor(Color.RED);
            return false;
        }
    }

    private boolean savePrefs() {

        SharedPreferences.Editor ed = Preferences.getEditor(this);
        URI uri = null;
        try {
            uri = new URI(mUrlTextbox.getText().toString());
        } catch (URISyntaxException ignored) {

        }

        try {
            WebClient client = new WebClient(this, mUserTextbox.getText()
                    .toString(), mPassTextbox.getText().toString());

            if (uri != null && uri.isAbsolute()) {
                client.getUrlContent(uri.toString() + "/contexts.xml");
            }
        } catch (WebClient.ApiException e) {
            return false;
        }

        if (uri != null && uri.isAbsolute()) {

            ed.putString(TRACKS_URL, uri.toString());

            ed.putInt(TRACKS_INTERVAL, mInterval.getSelectedItemPosition());
            ed.putString(TRACKS_USER, mUserTextbox.getText().toString());
            ed.putString(TRACKS_PASSWORD, mPassTextbox.getText().toString());

            ed.commit();
            return true;
        }
        return false;
    }

}
