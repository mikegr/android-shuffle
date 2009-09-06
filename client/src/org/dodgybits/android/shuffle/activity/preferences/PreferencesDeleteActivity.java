/*
 * Copyright (C) 2009 Android Shuffle Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dodgybits.android.shuffle.activity.preferences;

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
