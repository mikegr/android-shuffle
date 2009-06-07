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
    
    private View mColourEntry;
    private TextView mColourWidget;

    private View mIconEntry;
    private ImageView mIconWidget;
    private TextView mIconNoneWidget;
    private ImageButton mClearIconButton;
    
    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        
        loadCursor();
        findViewsAndAddListeners();
        
        if (mState == State.STATE_EDIT) {
            if (mCursor != null) {
                // Make sure we are at the one and only row in the cursor.
                mCursor.moveToFirst();
                setTitle(R.string.title_edit_context);
                mOriginalItem = BindingUtils.readContext(mCursor,getResources());
              	updateUIFromItem(mOriginalItem);
            } else {
                setTitle(getText(R.string.error_title));
                mNameWidget.setText(getText(R.string.error_message));
            }
        } else if (mState == State.STATE_INSERT) {
            setTitle(R.string.title_new_context);
            Bundle extras = getIntent().getExtras();
            updateUIFromExtras(extras);
        }
    }
    
    @Override
    protected boolean isValid() {
        String name = mNameWidget.getText().toString();
        return !TextUtils.isEmpty(name);
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
    protected void writeItem(ContentValues values, Context context) {
    	BindingUtils.writeContext(values, context);
    }

    /**
     * Take care of deleting a context.  Simply deletes the entry.
     */
    @Override
    protected void doDeleteAction() {
    	super.doDeleteAction();
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
    
    @Override
    protected Context createItemFromUI() {
        String name = mNameWidget.getText().toString();
        return new Context(name, mColourIndex, mIcon);
    }
    
    @Override
    protected void updateUIFromExtras(Bundle extras) {
    	displayIcon();
        displayColour();
    }
    
    @Override
    protected void updateUIFromItem(Context context) {
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
    }
    
    private void loadCursor() {
    	if (mUri != null)
    	{
            mCursor = managedQuery(mUri, Shuffle.Contexts.cFullProjection, null, null, null);
	        if (mCursor == null || mCursor.getCount() == 0) {
	            // The cursor is empty. This can happen if the event was deleted.
	            finish();
	            return;
	        }
    	}
    }
    
    private void findViewsAndAddListeners() {
        // The text view for our context description, identified by its ID in the XML file.
        mNameWidget = (EditText) findViewById(R.id.name);

        mColourWidget = (TextView) findViewById(R.id.colour_display);
        mColourIndex = 0;
        
        mIconWidget = (ImageView) findViewById(R.id.icon_display);
        mIconNoneWidget = (TextView) findViewById(R.id.icon_none);
        mIcon = Icon.NONE;
        
        mColourEntry = findViewById(R.id.colour_entry);
        mColourEntry.setOnClickListener(this);
        mColourEntry.setOnFocusChangeListener(this);
        
        mIconEntry = findViewById(R.id.icon_entry);
        mIconEntry.setOnClickListener(this);
        mIconEntry.setOnFocusChangeListener(this);
        
        mClearIconButton = (ImageButton) findViewById(R.id.icon_clear_button);
        mClearIconButton.setOnClickListener(this);
        mClearIconButton.setOnFocusChangeListener(this);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.colour_entry: {
            	// Launch activity to pick colour
            	Intent intent = new Intent(Intent.ACTION_PICK);
            	intent.setType(ColourPickerActivity.TYPE);
            	startActivityForResult(intent, COLOUR_PICKER);
                break;
            }

            case R.id.icon_entry: {
            	// Launch activity to pick icon
            	Intent intent = new Intent(Intent.ACTION_PICK);
            	intent.setType(IconPickerActivity.TYPE);
            	startActivityForResult(intent, ICON_PICKER);
                break;
            }
                    
            case R.id.icon_clear_button: {
	        	mIcon = Icon.NONE;
	        	displayIcon();
	        	break;
            }
            
            default:
            	super.onClick(v);
            	break;
        }
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
