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

package org.dodgybits.shuffle.android.core.activity;

import static org.dodgybits.shuffle.android.core.util.Constants.cPackage;
import static org.dodgybits.shuffle.android.core.util.Constants.cStringType;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.FlurryEnabledActivity;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class HelpActivity extends FlurryEnabledActivity {
    public static final String cHelpPage = "helpPage";
    
	@InjectView(R.id.help_screen) Spinner mHelpSpinner;
	@InjectView(R.id.help_text) TextView mHelpContent;
	@InjectView(R.id.previous_button) Button mPrevious;
	@InjectView(R.id.next_button) Button mNext;
    	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

        setContentView(R.layout.help);
        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        		this, R.array.help_screens,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHelpSpinner.setAdapter(adapter);
        mHelpSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	public void onNothingSelected(AdapterView<?> arg0) {
        		// do nothing
        	}
        	
        	public void onItemSelected(AdapterView<?> parent, View v,
        			int position, long id) {
        		int resId = HelpActivity.this.getResources().getIdentifier(
        				"help" + position, cStringType, cPackage);
        		mHelpContent.setText(HelpActivity.this.getText(resId));
        		updateNavigationButtons();
        	}
        });

        mPrevious.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		int position = mHelpSpinner.getSelectedItemPosition();
            	mHelpSpinner.setSelection(position - 1);
            }
        });        

        
        mNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		int position = mHelpSpinner.getSelectedItemPosition();
            	mHelpSpinner.setSelection(position + 1);
            }
        });        
        
        setSelectionFromBundle(getIntent().getExtras());
	}
	
	private void setSelectionFromBundle(Bundle bundle) {
        int position = 0;
        if (bundle != null) {
        	position = bundle.getInt(cHelpPage, 0);
        }
        mHelpSpinner.setSelection(position);
	}

	private void updateNavigationButtons() {
		int position = mHelpSpinner.getSelectedItemPosition();
		mPrevious.setEnabled(position > 0);
		mNext.setEnabled(position < mHelpSpinner.getCount() - 1);
	}
	
}
