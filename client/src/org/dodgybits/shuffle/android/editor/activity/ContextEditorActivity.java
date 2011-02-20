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

package org.dodgybits.shuffle.android.editor.activity;

import android.widget.*;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Context.Builder;
import org.dodgybits.shuffle.android.core.model.encoding.EntityEncoder;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.util.TextColours;
import org.dodgybits.shuffle.android.core.view.ContextIcon;
import org.dodgybits.shuffle.android.core.view.DrawableUtils;
import org.dodgybits.shuffle.android.list.activity.State;
import org.dodgybits.shuffle.android.list.view.ContextView;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;

import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.google.inject.Inject;

public class ContextEditorActivity extends AbstractEditorActivity<Context> implements TextWatcher {

    private static final String cTag = "ContextEditorActivity";
   
    private static final int COLOUR_PICKER = 0;
    private static final int ICON_PICKER = 1;
    
    private int mColourIndex;
    private ContextIcon mIcon;
    
    @InjectView(R.id.name) private EditText mNameWidget;

    @InjectView(R.id.colour_display) private TextView mColourWidget;

    @InjectView(R.id.icon_display) private ImageView mIconWidget;
    @InjectView(R.id.icon_none) private TextView mIconNoneWidget;
    @InjectView(R.id.icon_clear_button) private ImageButton mClearIconButton;
	@InjectView(R.id.context_preview) private ContextView mContext;

    private @InjectView(R.id.deleted_entry) View mDeletedEntry;
    private CheckBox mDeletedCheckBox;

    private @InjectView(R.id.active_entry) View mActiveEntry;
    private @InjectView(R.id.active_entry_checkbox) CheckBox mActiveCheckBox;

	@Inject private EntityPersister<Context> mPersister;
	@Inject private EntityEncoder<Context> mEncoder;
	
	@Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        
        loadCursor();
        findViewsAndAddListeners();

        if (mState == State.STATE_EDIT) {
            // Make sure we are at the one and only row in the cursor.
            mCursor.moveToFirst();
            setTitle(R.string.title_edit_context);
            mOriginalItem = mPersister.read(mCursor);
          	updateUIFromItem(mOriginalItem);
        } else if (mState == State.STATE_INSERT) {
            setTitle(R.string.title_new_context);
            mDeletedEntry.setVisibility(View.GONE);
            mDeletedCheckBox.setChecked(false);
            mActiveCheckBox.setChecked(true);
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
    	        	updatePreview();
    			}
    		}
    		break;
    	case ICON_PICKER:
        	if (resultCode == Activity.RESULT_OK) {
    			if (data != null) {
    				String iconName = data.getStringExtra("iconName");
    				mIcon = ContextIcon.createIcon(iconName, getResources());
    				displayIcon();
    	        	updatePreview();
    			}
    		}
    		break;
    		default:
    			Log.e(cTag, "Unknown requestCode: " + requestCode);
    	}
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
    protected Intent getInsertIntent() {
    	return new Intent(Intent.ACTION_INSERT, ContextProvider.Contexts.CONTENT_URI);
    }
    
    @Override
    protected CharSequence getItemName() {
    	return getString(R.string.context_name);
    }
    
    @Override
    protected Context createItemFromUI() {
        Builder builder = Context.newBuilder();
        if (mOriginalItem != null) {
            builder.mergeFrom(mOriginalItem);
        }
        
        builder.setName(mNameWidget.getText().toString());
        builder.setModifiedDate(System.currentTimeMillis());
        builder.setColourIndex(mColourIndex);
        builder.setIconName(mIcon.iconName);
        builder.setDeleted(mDeletedCheckBox.isChecked());
        builder.setActive(mActiveCheckBox.isChecked());

        return builder.build();
    }
    
    @Override
    protected void updateUIFromExtras(Bundle extras) {
    	if (mColourIndex == -1) {
    		mColourIndex = 0;
    	}
    	
    	displayIcon();
        displayColour();
    	updatePreview();
    }
    
    @Override
    protected void updateUIFromItem(Context context) {
        mNameWidget.setTextKeepState(context.getName());
        
       	mColourIndex = context.getColourIndex();
       	displayColour();
       	
       	final String iconName = context.getIconName();
       	mIcon = ContextIcon.createIcon(iconName, getResources());
       	displayIcon();

    	updatePreview();

        mActiveCheckBox.setChecked(context.isActive());

        mDeletedEntry.setVisibility(context.isDeleted() ? View.VISIBLE : View.GONE);
        mDeletedCheckBox.setChecked(context.isDeleted());

        if (mOriginalItem == null) {
        	mOriginalItem = context;
        }    	
    }
    
    @Override
    protected EntityEncoder<Context> getEncoder() {
        return mEncoder;
    }
    
    @Override
    protected EntityPersister<Context> getPersister() {
        return mPersister;
    }
    
    private void loadCursor() {
    	if (mUri != null && mState == State.STATE_EDIT)
    	{
            mCursor = managedQuery(mUri, ContextProvider.Contexts.FULL_PROJECTION, null, null, null);
	        if (mCursor == null || mCursor.getCount() == 0) {
	            // The cursor is empty. This can happen if the event was deleted.
	            finish();
            }
    	}
    }

    private void findViewsAndAddListeners() {
        // The text view for our context description, identified by its ID in the XML file.
		mNameWidget.addTextChangedListener(this);

		mColourIndex = -1;

		mIcon = ContextIcon.NONE;

		View colourEntry = findViewById(R.id.colour_entry);
		colourEntry.setOnClickListener(this);
		colourEntry.setOnFocusChangeListener(this);

		View iconEntry = findViewById(R.id.icon_entry);
		iconEntry.setOnClickListener(this);
		iconEntry.setOnFocusChangeListener(this);

		mClearIconButton.setOnClickListener(this);
		mClearIconButton.setOnFocusChangeListener(this);

        mActiveEntry.setOnClickListener(this);
        mActiveEntry.setOnFocusChangeListener(this);

        mDeletedEntry.setOnClickListener(this);
        mDeletedEntry.setOnFocusChangeListener(this);
        mDeletedCheckBox = (CheckBox) mDeletedEntry.findViewById(R.id.deleted_entry_checkbox);
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
	        	mIcon = ContextIcon.NONE;
	        	displayIcon();
	        	updatePreview();
	        	break;
            }

            case R.id.active_entry: {
                mActiveCheckBox.toggle();
                break;
            }

            case R.id.deleted_entry: {
                mDeletedCheckBox.toggle();
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
    	if (mIcon == ContextIcon.NONE) {
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
	
	private void updatePreview() {
		String name = mNameWidget.getText().toString();
		if (TextUtils.isEmpty(name) || mColourIndex == -1) {
			mContext.setVisibility(View.INVISIBLE);
		} else {
			mContext.updateView(createItemFromUI());
			mContext.setVisibility(View.VISIBLE);
		}				
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		updatePreview();
	}
	
	
}
