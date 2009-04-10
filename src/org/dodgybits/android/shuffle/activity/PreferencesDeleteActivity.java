package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public abstract class PreferencesDeleteActivity extends Activity {
    private static final String cTag = "PreferencesDeleteActivity";

    protected TextView mText;
    protected Button mDeleteButton;
    protected Button mCancelButton;
    
    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        setContentView(R.layout.delete_dialog);
        
        mText = (TextView) findViewById(R.id.text);
        
        mDeleteButton = (Button) findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	onDelete();
            }
        });        
        
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	finish();
            }
        });        
    }
    
    abstract protected void onDelete();
        
}
