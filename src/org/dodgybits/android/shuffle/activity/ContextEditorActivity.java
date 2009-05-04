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

package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.State;
import org.dodgybits.android.shuffle.model.Context.Icon;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.util.DrawableUtils;
import org.dodgybits.android.shuffle.util.TextColours;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ContextEditorActivity extends AbstractEditorActivity<Context> {

    private static final String cTag = "ContextEditorActivity";
   
    private static final int COLOUR_PICKER = 0;
    private static final int ICON_PICKER = 1;
    
    private int mColourIndex;
    private Icon mIcon;
    
    private EditText mNameWidget;
    private TextView mColourWidget;
    private Button mSetColourButton;
    private ImageButton mClearColourButton;
    private ImageView mIconWidget;
    private TextView mIconNoneWidget;
    private Button mSetIconButton;
    private ImageButton mClearIconButton;
        
    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        
        // The text view for our context description, identified by its ID in the XML file.
        mNameWidget = (EditText) findViewById(R.id.name);

        mColourWidget = (TextView) findViewById(R.id.colour_display);
        mColourIndex = -1;
        
        mIconWidget = (ImageView) findViewById(R.id.icon_display);
        mIconNoneWidget = (TextView) findViewById(R.id.icon_none);
        mIcon = Icon.NONE;
        
        mSetColourButton = (Button) findViewById(R.id.colour_set_button);
        mSetColourButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	// Launch activity to pick colour
            	Intent intent = new Intent(Intent.ACTION_PICK);
            	intent.setType(ColourPickerActivity.TYPE);
            	startActivityForResult(intent, COLOUR_PICKER);
            }
        });
        mClearColourButton = (ImageButton) findViewById(R.id.colour_clear_button);
        mClearColourButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	mColourIndex = 0;
            	displayColour();
            }
        });        
        
        mSetIconButton = (Button) findViewById(R.id.icon_set_button);
        mSetIconButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	// Launch activity to pick icon
            	Intent intent = new Intent(Intent.ACTION_PICK);
            	intent.setType(IconPickerActivity.TYPE);
            	startActivityForResult(intent, ICON_PICKER);
            }
        });
        mClearIconButton = (ImageButton) findViewById(R.id.icon_clear_button);
        mClearIconButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	mIcon = Icon.NONE;
            	displayIcon();
            }
        });        
        
        // Get the context!
        mCursor = managedQuery(mUri, Shuffle.Contexts.cFullProjection, null, null, null);
    }
    
    @Override
    protected void onResume() {
        Log.d(cTag, "onResume+");
        super.onResume();

        // If we didn't have any trouble retrieving the data, it is now
        // time to get at the stuff.
        if (mCursor != null) {
            // Make sure we are at the one and only row in the cursor.
            mCursor.moveToFirst();

            // Modify our overall title depending on the mode we are running in.
            if (mState == State.STATE_EDIT) {
                setTitle(R.string.title_edit_context);
            } else if (mState == State.STATE_INSERT) {
                setTitle(R.string.title_new_context);
            }

            // This is a little tricky: we may be resumed after previously being
            // paused/stopped.  We want to put the new text in the text view,
            // but leave the user where they were (retain the cursor position
            // etc).  This version of setText does that for us.
            Context context = BindingUtils.readContext(mCursor,getResources());
            mNameWidget.setTextKeepState(context.name);
            
            if (mColourIndex == -1) {
            	// if colour already set, use that (e.g. after use picks a new one)
            	mColourIndex = context.colourIndex;
            }
            displayColour();
            
            if (mIcon == Icon.NONE) {
            	// if icon already set, use that (e.g. after user picks a new one)
                mIcon = context.icon;
            }
        	displayIcon();

            // If we hadn't previously retrieved the original context, do so
            // now.  This allows the user to revert their changes.
            if (mOriginalItem == null) {
            	mOriginalItem = context;
            }
        } else {
            setTitle(getText(R.string.error_title));
            mNameWidget.setText(getText(R.string.error_message));
        }
        
        // select the description
        mNameWidget.selectAll();
        
    }
    
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
    	Log.d(cTag, "Got resultCode " + resultCode + " with data " + data);		
    	switch (requestCode) {
    	case COLOUR_PICKER:
        	if (resultCode == Activity.RESULT_OK) {
    			if (data != null) {
    				mColourIndex = Integer.parseInt(data.getStringExtra("colour"));
    				displayColour();
    			}
    		}
    		break;
    	case ICON_PICKER:
        	if (resultCode == Activity.RESULT_OK) {
    			if (data != null) {
    				String iconName = data.getStringExtra("iconName");
    				mIcon = Icon.createIcon(iconName, getResources());
    				displayIcon();
    			}
    		}
    		break;
    		default:
    			Log.e(cTag, "Unknown requestCode: " + requestCode);
    	}
	}
    
    @Override
    protected void onPause() {
        Log.d(cTag, "onPause+");
        super.onPause();

        // The user is going somewhere else, so make sure their current
        // changes are safely saved away in the provider.  We don't need
        // to do this if only viewing.
        if (mCursor != null) {
            String name = mNameWidget.getText().toString();

            // If this activity is finished, and there is no text, then we
            // do something a little special: simply delete the project entry.
            if (isFinishing() && mState == State.STATE_INSERT && TextUtils.isEmpty(name) ) {
                setResult(RESULT_CANCELED);
                deleteItem();
            } else {
            	if (TextUtils.isEmpty(name) && mOriginalItem != null) {
            		// we'll assume deleting the name was an accident
            		name = mOriginalItem.name;
            	}
            	Context context  = new Context(name, mColourIndex, mIcon);
                ContentValues values = new ContentValues();
            	writeItem(values, context);

                // Commit all of our changes to persistent storage. When the update completes
                // the content provider will notify the cursor of the change, which will
                // cause the UI to be updated.
                getContentResolver().update(mUri, values, null, null);    	
                //showSaveToast();
            }
        }
    }
    
    @Override
    protected void writeItem(ContentValues values, Context context) {
    	BindingUtils.writeContext(values, context);
    }

    /**
     * Take care of deleting a context.  Simply deletes the entry.
     */
    @Override
    protected void deleteItem() {
    	super.deleteItem();
        mNameWidget.setText("");
    }
    
    /**
     * @return id of layout for this view
     */
    @Override
    protected int getContentViewResId() {
    	return R.layout.context_editor;
    }

    @Override
    protected Context restoreItem(Bundle icicle) {
    	return BindingUtils.restoreContext(icicle,getResources());
    }
    
    @Override
    protected void saveItem(Bundle outState, Context item) {
    	BindingUtils.saveContext(outState, item);
    }
    
    @Override
    protected Intent getInsertIntent() {
    	return new Intent(Intent.ACTION_INSERT, Shuffle.Contexts.CONTENT_URI);
    }
    
    @Override
    protected CharSequence getItemName() {
    	return getString(R.string.context_name);
    }
    
	private void displayColour() {
		int bgColour = TextColours.getInstance(this).getBackgroundColour(mColourIndex);
		GradientDrawable drawable = DrawableUtils.createGradient(bgColour, Orientation.TL_BR);
		drawable.setCornerRadius(8.0f);
		mColourWidget.setBackgroundDrawable(drawable);
    }


	private void displayIcon() {
    	if (mIcon == Icon.NONE) {
    		mIconNoneWidget.setVisibility(View.VISIBLE);
    		mIconWidget.setVisibility(View.GONE);
    		mClearIconButton.setEnabled(false);
    	} else {
    		mIconNoneWidget.setVisibility(View.GONE);
    		mIconWidget.setImageResource(mIcon.largeIconId);
    		mIconWidget.setVisibility(View.VISIBLE);
    		mClearIconButton.setEnabled(true);
    	}
    }    
    
}
