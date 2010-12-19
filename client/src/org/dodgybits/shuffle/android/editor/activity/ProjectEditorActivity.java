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
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Project.Builder;
import org.dodgybits.shuffle.android.core.model.encoding.EntityEncoder;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.view.ContextIcon;
import org.dodgybits.shuffle.android.list.activity.State;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;

import roboguice.inject.InjectView;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.inject.Inject;

public class ProjectEditorActivity extends AbstractEditorActivity<Project> {

    private static final String cTag = "ProjectEditorActivity";
   
    @InjectView(R.id.name) EditText mNameWidget;
    @InjectView(R.id.default_context) Spinner mDefaultContextSpinner;
    @InjectView(R.id.parallel_entry) RelativeLayout mParallelEntry;
    @InjectView(R.id.parallel_label) TextView mParallelLabel;
    @InjectView(R.id.parallel_icon) ImageView mParallelButton;

    private @InjectView(R.id.active_entry) View mActiveEntry;
    private @InjectView(R.id.active_entry_checkbox) CheckBox mActiveCheckBox;

    private @InjectView(R.id.deleted_entry) View mDeletedEntry;
    private CheckBox mDeletedCheckBox;

    @Inject private EntityPersister<Project> mPersister;
    @Inject private EntityEncoder<Project> mEncoder;
    
    private String[] mContextNames;
    private long[] mContextIds;
    private boolean isParallel;
    
    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        
        loadCursor();
		findViewsAndAddListeners();

        if (mState == State.STATE_EDIT) {
            // Make sure we are at the one and only row in the cursor.
            mCursor.moveToFirst();
            setTitle(R.string.title_edit_project);
            mOriginalItem = mPersister.read(mCursor);
          	updateUIFromItem(mOriginalItem);
        } else if (mState == State.STATE_INSERT) {
            isParallel = false;
            setTitle(R.string.title_new_project);
            mDeletedEntry.setVisibility(View.GONE);
            mDeletedCheckBox.setChecked(false);
            Bundle extras = getIntent().getExtras();
            updateUIFromExtras(extras);
        }
    }

    private void findViewsAndAddListeners() {
        Cursor contactCursor = getContentResolver().query(
                ContextProvider.Contexts.CONTENT_URI,
                new String[] {ContextProvider.Contexts._ID, ContextProvider.Contexts.NAME}, null, null, null);
        int size = contactCursor.getCount() + 1;
        mContextIds = new long[size];
        mContextIds[0] = 0;
        mContextNames = new String[size];
        mContextNames[0] = getText(R.string.none_empty).toString();
        for (int i = 1; i < size; i++) {
            contactCursor.moveToNext();
            mContextIds[i] = contactCursor.getLong(0);
            mContextNames[i] = contactCursor.getString(1);
        }
        contactCursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, mContextNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDefaultContextSpinner.setAdapter(adapter);

        mParallelEntry.setOnClickListener(this);

        mActiveEntry.setOnClickListener(this);
        mActiveEntry.setOnFocusChangeListener(this);

        mDeletedEntry.setOnClickListener(this);
        mDeletedEntry.setOnFocusChangeListener(this);
        mDeletedCheckBox = (CheckBox) mDeletedEntry.findViewById(R.id.deleted_entry_checkbox);
    }

    @Override
    protected boolean isValid() {
        String name = mNameWidget.getText().toString();
        return !TextUtils.isEmpty(name);
    }
    
    @Override
    protected void doDeleteAction() {
    	super.doDeleteAction();
        mNameWidget.setText("");
    }
        
    @Override
    protected Project createItemFromUI() {
        Builder builder = Project.newBuilder();
        if (mOriginalItem != null) {
            builder.mergeFrom(mOriginalItem);
        }

        builder.setName(mNameWidget.getText().toString());
        builder.setModifiedDate(System.currentTimeMillis());
        builder.setParallel(isParallel);
        
    	Id defaultContextId = Id.NONE;
    	int selectedItemPosition = mDefaultContextSpinner.getSelectedItemPosition();
		if (selectedItemPosition > 0) {
    		defaultContextId = Id.create(mContextIds[selectedItemPosition]);
    	}
		builder.setDefaultContextId(defaultContextId);
        builder.setDeleted(mDeletedCheckBox.isChecked());
        builder.setActive(mActiveCheckBox.isChecked());

        return builder.build();
    }
    
    @Override
    protected void updateUIFromExtras(Bundle extras) {
    	// do nothing for now
    }
    
    @Override
    protected void updateUIFromItem(Project project) {
        mNameWidget.setTextKeepState(project.getName());
        Id defaultContextId = project.getDefaultContextId();
        if (defaultContextId.isInitialised()) {
            for (int i = 1; i < mContextIds.length; i++) {
                if (mContextIds[i] == defaultContextId.getId()) {
                    mDefaultContextSpinner.setSelection(i);
                    break;
                }
            }
        } else {
        	mDefaultContextSpinner.setSelection(0);
        }
        
        isParallel = project.isParallel();
        updateParallelSection();

        mActiveCheckBox.setChecked(project.isActive());

        mDeletedEntry.setVisibility(project.isDeleted() ? View.VISIBLE : View.GONE);
        mDeletedCheckBox.setChecked(project.isDeleted());
    }
    
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parallel_entry: {
                isParallel = !isParallel;
                updateParallelSection();
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
    
    /**
     * @return id of layout for this view
     */
    @Override
    protected int getContentViewResId() {
    	return R.layout.project_editor;
    }

    @Override
    protected Intent getInsertIntent() {
    	return new Intent(Intent.ACTION_INSERT, ProjectProvider.Projects.CONTENT_URI);
    }
    
    @Override
    protected CharSequence getItemName() {
    	return getString(R.string.project_name);
    }
    
    @Override
    protected EntityEncoder<Project> getEncoder() {
        return mEncoder;
    }
    
    @Override
    protected EntityPersister<Project> getPersister() {
        return mPersister;
    }
    
    private void loadCursor() {
    	if (mUri != null && mState == State.STATE_EDIT)
    	{
            mCursor = managedQuery(mUri, ProjectProvider.Projects.FULL_PROJECTION, null, null, null);
	        if (mCursor == null || mCursor.getCount() == 0) {
	            // The cursor is empty. This can happen if the event was deleted.
	            finish();
	            return;
	        }
    	}
    }
    
    private void updateParallelSection() {
        if (isParallel) {
            mParallelLabel.setText(R.string.parallel_title);
            mParallelButton.setImageResource(R.drawable.parallel);
        } else {
            mParallelLabel.setText(R.string.sequence_title);
            mParallelButton.setImageResource(R.drawable.sequence);
        }
    }
    
    
}
