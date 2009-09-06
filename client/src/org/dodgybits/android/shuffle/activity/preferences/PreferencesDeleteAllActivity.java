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
import org.dodgybits.android.shuffle.util.ModelUtils;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class PreferencesDeleteAllActivity extends PreferencesDeleteActivity {
    private static final String cTag = "PreferencesDeleteAllActivity";

    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        
        setProgressBarIndeterminate(true);
        
        mDeleteButton.setText(R.string.clean_slate_button_title);
        mText.setText(R.string.clean_slate_warning);
    }
    
	@Override
	protected void onDelete() {
    	Log.i(cTag, "Cleaning the slate");
    	ModelUtils.cleanSlate(this, null);
        Toast.makeText(this, R.string.clean_slate_message, Toast.LENGTH_SHORT).show();
    	finish();
	}
		
}
