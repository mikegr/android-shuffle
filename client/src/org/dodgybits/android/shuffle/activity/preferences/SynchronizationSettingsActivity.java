package org.dodgybits.android.shuffle.activity.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.CompoundButton;
import android.view.View;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Preferences;
import static org.dodgybits.android.shuffle.model.Preferences.*;

/**
 * Created by IntelliJ IDEA.
 * User: Morten
 * Date: 2010-jan-03
 * Time: 10:34:49
 * Activity that changes the options set for synchronization
 */
public class SynchronizationSettingsActivity extends Activity {
    private TextView mUrlTextbox;
    private TextView mUserTextbox;
    private TextView mPassTextbox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.synchronize_settings);


        mUrlTextbox = (TextView) findViewById(R.id.url);
        mUserTextbox = (TextView) findViewById(R.id.user);
        mPassTextbox = (TextView) findViewById(R.id.pass);

        mUrlTextbox.setText(Preferences.getTracksUrl(this));
        mUserTextbox.setText(Preferences.getTracksUser(this));
        mPassTextbox.setText(Preferences.getTracksPassword(this));

       CompoundButton.OnClickListener saveClick = new CompoundButton.OnClickListener() {

           @Override
           public void onClick(View view) {
              	savePrefs();
               finish();
           }
       };

            CompoundButton.OnClickListener cancelClick = new CompoundButton.OnClickListener() {

           @Override
           public void onClick(View view) {
              finish();
           }
       };

        // Setup the bottom buttons
        View view = findViewById(R.id.saveButton);
        view.setOnClickListener(saveClick);
        view = findViewById(R.id.discardButton);
        view.setOnClickListener(cancelClick);
    }

    private void savePrefs() {

        SharedPreferences.Editor ed = Preferences.getEditor(this);
        ed.putString(TRACKS_URL, mUrlTextbox.getText().toString());
        ed.putString(TRACKS_USER, mUserTextbox.getText().toString());
        ed.putString(TRACKS_PASSWORD, mPassTextbox.getText().toString());
        ed.commit();
    }

}
